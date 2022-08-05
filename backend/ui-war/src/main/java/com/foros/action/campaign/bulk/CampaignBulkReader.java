package com.foros.action.campaign.bulk;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.ChannelExpressionLink;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.security.User;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.time.TimeSpan;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.campaign.bulk.BulkParseResult;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.template.OptionService;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.bulk.BulkReader;
import com.foros.util.bulk.BulkReader.BulkReaderRow;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class CampaignBulkReader {
    private static java.util.regex.Pattern NAME_PATTERN = java.util.regex.Pattern.compile("<|>");
    private static final int MAX_ROWS_COUNT = 65000;

    public static final ExtensionProperty<Object[]> ORIGINAL_VALUES = new ExtensionProperty<>(Object[].class);

    private MetaData<Column> metaData;
    private MetaDataBuilder metaDataBuilder;
    private List<Column> columns;
    private BulkReader reader;

    private List<Campaign> campaigns;
    private List<CampaignCreativeGroup> groups;
    private List<CampaignCreative> campaignCreatives;
    private Map<Long, Creative> existedCreatives;
    private Map<String, Creative> createdCreatives;
    private List<CCGKeyword> ccgKeywords;
    private Map<ILinkMarker, EntityBase> selfLinks;
    private IdentityHashMap<EntityBase, ILinkMarker> parentLinks;
    private Map<TextCreativeOption, Option> textCreativeOptionsMap;

    private UploadContext currentStatus;
    private IdentityHashMap<ILinkMarker, UploadContext> linkUploadContexts;
    private long sizeId;
    private long templateId;
    private TGTType tgtType;

    private BulkReaderRow currentRow;

    private DisplayCreativeService creativeService;
    private OptionService optionService;

    public CampaignBulkReader(BulkReader reader, MetaDataBuilder builder, TGTType tgtType, long sizeId, long templateId,
                              DisplayCreativeService creativeService, OptionService optionService) {
        this.reader = reader;
        metaDataBuilder = builder;
        metaData = builder.forUpload();
        columns = metaData.getColumns();
        this.tgtType = tgtType;
        this.sizeId = sizeId;
        this.templateId = templateId;
        this.creativeService = creativeService;
        this.optionService = optionService;
    }

    public BulkParseResult parse() throws IOException {
        campaigns = new LinkedList<>();
        groups = new LinkedList<>();
        campaignCreatives = new LinkedList<>();
        createdCreatives = new HashMap<>();
        existedCreatives = new HashMap<>();
        ccgKeywords = new LinkedList<>();
        parentLinks = new IdentityHashMap<>(3000);
        selfLinks = new HashMap<>(1000);
        currentStatus = new UploadContext();
        linkUploadContexts = new IdentityHashMap<>(1000);
        Locale locale = CurrentUserSettingsHolder.getLocale();
        final MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        final Set<Integer> allowedColumnsCount = new HashSet<>(Arrays.asList(
            metaDataBuilder.forUpload().getColumns().size(),
            metaDataBuilder.forExport().getColumns().size(),
            metaDataBuilder.forReview().getColumns().size()
            ));

        reader.setBulkReaderHandler(new BulkReader.BulkReaderHandler() {
            @Override
            public void handleRow(BulkReaderRow row) {
                long line = row.getRowNum();
                if (line == 1) {
                    if (!allowedColumnsCount.contains(row.getColumnCount())) {
                        throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
                    }
                    return;
                }

                if (line > MAX_ROWS_COUNT) {
                    throw ConstraintViolationException.newBuilder("site.error.tooManyRows").build();
                }

                currentRow = row;
                currentStatus = new UploadContext();

                if (!allowedColumnsCount.contains(row.getColumnCount())) {
                    String allowed = StringUtils.join(allowedColumnsCount, ", ");
                    String actual = String.valueOf(row.getColumnCount());
                    currentStatus.addFatal("errors.invalid.rowFormat").withParameters(allowed, actual);
                }

                EntityBase entity = readEntity();

                currentStatus.flush(interpolator);

                if (entity instanceof CCGKeyword &&
                        ((CCGKeyword) entity).getTriggerType() == null &&
                        !currentStatus.getWrongPaths().contains(CampaignFieldCsv.KeywordType.getFieldPath())) {
                    CCGKeyword pageKeyword = (CCGKeyword) entity;
                    pageKeyword.setTriggerType(KeywordTriggerType.PAGE_KEYWORD);
                    CCGKeyword searchKeyword = new CCGKeyword();
                    searchKeyword.setOriginalKeyword(pageKeyword.getOriginalKeyword());
                    searchKeyword.setMaxCpcBid(pageKeyword.getMaxCpcBid());
                    searchKeyword.setClickURL(pageKeyword.getClickURL());
                    searchKeyword.setTriggerType(KeywordTriggerType.SEARCH_KEYWORD);
                    searchKeyword.setStatus(pageKeyword.getStatus());
                    ccgKeywords.add(searchKeyword);
                    parentLinks.put(searchKeyword, readGroupLink(true));
                    UploadUtils.setRowNumber(searchKeyword, line);
                }

                UploadUtils.setRowNumber(entity, line);
                if (entity instanceof CampaignCreative) {
                    Creative creative = ((CampaignCreative) entity).getCreative();
                    if (UploadUtils.getRowNumber(creative) == null) {
                        UploadUtils.setRowNumber(creative, line);
                    }
                }

                if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                    entity.setProperty(UPLOAD_CONTEXT, currentStatus);
                    assignOriginalValues(entity);
                }

            }
        });

        reader.read();

        setLastCreativeToCC();

        buildTree();

        return new BulkParseResult(campaigns, getCreatives());
    }

    private void buildTree() {
        for (CCGKeyword ccgKeyword : ccgKeywords) {
            CampaignCreativeGroup group = findParentGroup(ccgKeyword);
            group.getCcgKeywords().add(ccgKeyword);
            ccgKeyword.setCreativeGroup(group);
        }

        for (CampaignCreative cc : campaignCreatives) {
            CampaignCreativeGroup group = findParentGroup(cc);
            group.getCampaignCreatives().add(cc);
            cc.setCreativeGroup(group);
        }

        for (CampaignCreativeGroup group : groups) {
            Campaign campaign = findParentCampaign(group);
            campaign.getCreativeGroups().add(group);
            group.setCampaign(campaign);
        }
    }

    private EntityBase readEntity() {
        CampaignLevelCsv level = readLevel();
        EntityBase entity;
        if (!currentStatus.isFatal()) {
            entity = readEntity(level);
        } else {
            // can't read anything
            Campaign campaign = new Campaign();
            campaigns.add(campaign);
            entity = campaign;
        }
        return entity;
    }

    private EntityBase readEntity(CampaignLevelCsv level) {
        EntityBase entity;

        switch (level) {
        case Campaign:
            entity = readCampaign();
            addSelfLink(readCampaignLink(false), entity);
            break;
        case AdGroup:
            entity = readGroup();
            addSelfLink(readGroupLink(false), entity);
            parentLinks.put(entity, readCampaignLink(true));
            break;
        case TextAd:
            entity = readCampaignCreative();
            parentLinks.put(entity, readGroupLink(true));
            break;
        case Keyword:
            entity = readKeyword();
            parentLinks.put(entity, readGroupLink(true));
            break;
        default:
            throw new RuntimeException("Unsupported level: " + level);
        }

        return entity;
    }

    private void addSelfLink(ILinkMarker key, EntityBase entity) {
        // link everything to first entity with given key
        if (selfLinks.containsKey(key)) {
            return;
        }
        selfLinks.put(key, entity);
    }

    private Campaign findParentCampaign(CampaignCreativeGroup child) {
        CampaignLink campaignLink = (CampaignLink) parentLinks.get(child);
        Campaign campaign = (Campaign) selfLinks.get(campaignLink);
        if (campaign == null) {
            campaign = new Campaign();
            campaign.setName(campaignLink.getCampaignName());
            campaign.setProperty(UPLOAD_CONTEXT, linkUploadContexts.get(campaignLink));
            addSelfLink(campaignLink, campaign);
            campaigns.add(campaign);
        }
        return campaign;
    }

    private CampaignCreativeGroup findParentGroup(EntityBase child) {
        GroupLink groupLink = (GroupLink) parentLinks.get(child);
        CampaignCreativeGroup group = (CampaignCreativeGroup) selfLinks.get(groupLink);
        if (group == null) {
            group = new CampaignCreativeGroup();
            group.setName(groupLink.getGroupName());
            group.setCcgType(CCGType.TEXT);
            group.setTgtType(tgtType);
            group.setProperty(UPLOAD_CONTEXT, linkUploadContexts.get(groupLink));
            addSelfLink(groupLink, group);
            parentLinks.put(group, groupLink.getCampaignLink());
            groups.add(group);
        }
        return group;
    }

    private Campaign readCampaign() {
        Campaign campaign = new Campaign();
        campaign.setCampaignType(CampaignType.TEXT);
        campaign.setName(readString(CampaignFieldCsv.CampaignName));
        campaign.setBudget(readBigDecimal(CampaignFieldCsv.CampaignBudget));
        setStatus(campaign, CampaignFieldCsv.CampaignStatus);
        campaign.setDateStart(readDateTime(CampaignFieldCsv.CampaignStartDate, true));
        campaign.setDateEnd(readDateTime(CampaignFieldCsv.CampaignEndDate, true));
        if (metaData.contains(CampaignFieldCsv.CampaignSalesManager)) {
            campaign.setSalesManager(readUser(CampaignFieldCsv.CampaignSalesManager));
        }
        campaign.setSoldToUser(readUser(CampaignFieldCsv.CampaignSoldToUser));
        campaign.setBillToUser(readUser(CampaignFieldCsv.CampaignBillToUser));
        setDeliveryPacingAndDailyBudget(campaign);
        if (metaData.contains(CampaignFieldCsv.CampaignFCPeriod)) {
            campaign.setFrequencyCap(readFrequencyCap(
                    CampaignFieldCsv.CampaignFCPeriod,
                    CampaignFieldCsv.CampaignFCWindow,
                    CampaignFieldCsv.CampaignFCWindowLength,
                    CampaignFieldCsv.CampaignFCLife));
        }
        campaigns.add(campaign);
        return campaign;
    }

    private CampaignCreativeGroup readGroup() {
        CampaignCreativeGroup ccg = new CampaignCreativeGroup();
        ccg.setCcgType(CCGType.TEXT);
        ccg.setTgtType(tgtType);
        ccg.setName(readString(CampaignFieldCsv.AdGroupName));
        ccg.setCcgRate(readCcgRate(CampaignFieldCsv.AdGroupRate, CampaignFieldCsv.AdGroupRateType));
        setStatus(ccg, CampaignFieldCsv.AdGroupStatus);
        ccg.setBudget(readBigDecimal(CampaignFieldCsv.AdGroupBudget));
        try {
            // first try to read column as a number
            BigDecimal dailyBudget = getNumericValue(CampaignFieldCsv.AdGroupDailyBudget);
            ccg.setDailyBudget(dailyBudget);
            ccg.setDeliveryPacing(DeliveryPacing.FIXED);
        } catch (ParseException e) {
            // only fixed value for text ccg is possible
            addError(CampaignFieldCsv.AdGroupDailyBudget, "errors.field.number");
        }
        ccg.setDateStart(readDateTime(CampaignFieldCsv.AdGroupStartDate, true));
        ccg.setDateEnd(readDateTime(CampaignFieldCsv.AdGroupEndDate, true));
        ccg.setLinkedToCampaignEndDateFlag(ccg.getDateEnd() == null);
        ccg.setCountry(readCountry(CampaignFieldCsv.AdGroupCountryTargeting));

        readDeviceTargeting(ccg);
        readChannelTarget(ccg);

        if (metaData.contains(CampaignFieldCsv.AdGroupFCPeriod)) {
            ccg.setFrequencyCap(readFrequencyCap(
                    CampaignFieldCsv.AdGroupFCPeriod,
                    CampaignFieldCsv.AdGroupFCWindow,
                    CampaignFieldCsv.AdGroupFCWindowLength,
                    CampaignFieldCsv.AdGroupFCLife));
        }
        groups.add(ccg);
        return ccg;
    }

    private void readDeviceTargeting(CampaignCreativeGroup ccg) {
        String deviceTargeting = readString(CampaignFieldCsv.AdGroupDeviceTargeting);
        String[] channels = StringUtils.isBlank(deviceTargeting) ? new String[0] : deviceTargeting.split("\\|");
        Set<DeviceChannel> deviceChannels = new HashSet<>();
        for (String channelId : channels) {
            try {
                DeviceChannel deviceChannel = new DeviceChannel();
                deviceChannel.setId(Long.valueOf(channelId));
                deviceChannels.add(deviceChannel);
            } catch (NumberFormatException e) {
                // only fixed value for text ccg is possible
                addError(CampaignFieldCsv.AdGroupDeviceTargeting, "errors.field.number");
            }
        }
        ccg.setDeviceChannels(deviceChannels);
    }

    private void readChannelTarget(CampaignCreativeGroup ccg) {
        if (!metaData.contains(CampaignFieldCsv.AdGroupChannelTarget)) {
            return;
        }

        String unparsedValue = readString(CampaignFieldCsv.AdGroupChannelTarget, true);
        if (StringUtil.isPropertyEmpty(unparsedValue)) {
            return;
        }

        try {
            // first try to read column as a ChannelTarget
            ChannelTarget channelTarget = ChannelTarget.valueOf(unparsedValue.toUpperCase());
            if (channelTarget == ChannelTarget.TARGETED) {
                addError(CampaignFieldCsv.AdGroupChannelTarget, "errors.field.invalid");
            } else {
                ccg.setChannelTarget(channelTarget);
                ccg.setChannel(null);
            }
        } catch (IllegalArgumentException e) {
            // OK it's not a ChannelTarget so it must be expression, check it later
            ccg.setChannel(new ChannelExpressionLink(unparsedValue));
            ccg.setChannelTarget(ChannelTarget.TARGETED);
        }
    }

    private CampaignCreative readCampaignCreative() {
        Creative creative = new Creative();
        creative.setId(readLong(CampaignFieldCsv.AdId));
        setStatus(creative, CampaignFieldCsv.AdStatus);
        if (metaData.contains(CampaignFieldCsv.AdApproval)) {
            creative.setQaStatus(readQaStatus(CampaignFieldCsv.AdApproval));
        }

        CreativeSize size = new CreativeSize();
        size.setDefaultName(CreativeSize.TEXT_SIZE);
        size.setId(sizeId);
        creative.setSize(size);
        CreativeTemplate template = new CreativeTemplate();
        template.setDefaultName(CreativeTemplate.TEXT_TEMPLATE);
        template.setId(templateId);
        creative.setTemplate(template);

        CreativeOptionValue headLineOption = readOptionValue(CampaignFieldCsv.AdHeadline);
        if (headLineOption.getValue() != null) {
            creative.setName(NAME_PATTERN.matcher(headLineOption.getValue()).replaceAll("_"));
        }
        creative.getOptions().add(headLineOption);
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdDescriptionLine1));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdDescriptionLine2));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdDescriptionLine3));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdDescriptionLine4));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdDisplayURL));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdClickURL));
        creative.getOptions().add(readOptionValue(CampaignFieldCsv.AdImageFile));

        if (creative.getId() != null) {
            existedCreatives.put(creative.getId(), creative);
        } else {
            String creativeHash = creativeService.calculateHash(creative);
            Creative tmp = createdCreatives.get(creativeHash);
            if (tmp == null) {
                createdCreatives.put(creativeHash, creative);
            } else {
                creative = tmp;
            }
        }

        CampaignCreative cc = new CampaignCreative();
        cc.setId(readLong(CampaignFieldCsv.AdLinkId));
        setStatus(cc, CampaignFieldCsv.AdLinkStatus);
        cc.setCreative(creative);
        if (metaData.contains(CampaignFieldCsv.AdFCPeriod)) {
            cc.setFrequencyCap(readFrequencyCap(
                    CampaignFieldCsv.AdFCPeriod,
                    CampaignFieldCsv.AdFCWindow,
                    CampaignFieldCsv.AdFCWindowLength,
                    CampaignFieldCsv.AdFCLife));
        }
        campaignCreatives.add(cc);

        if (creative.getId() == null && cc.getId() != null) {
            currentStatus.addFatal("campaign.csv.errors.textAd.textAdIdCreativeIdInconsistency")
                    .withPath(CampaignFieldCsv.AdId.getFieldPath())
                    .withParameters(StringUtil.getLocalizedString(CampaignFieldCsv.AdId.getNameKey()),
                            StringUtil.getLocalizedString(CampaignFieldCsv.AdLinkId.getNameKey()));
            return cc;
        }

        return cc;
    }

    private CreativeOptionValue readOptionValue(CampaignFieldCsv field) {
        if (textCreativeOptionsMap == null) {
            TextCreativeOption[] textCreativeOptions = TextCreativeOption.values();
            textCreativeOptionsMap = new HashMap<>(textCreativeOptions.length);
            for (TextCreativeOption textCreativeOption : textCreativeOptions) {
                textCreativeOptionsMap.put(textCreativeOption,
                        optionService.findByTokenFromTextTemplate(textCreativeOption.getToken()));
            }
        }
        CreativeOptionValue optionValue = new CreativeOptionValue();
        optionValue.setOption(textCreativeOptionsMap.get(field.getTextOption()));
        optionValue.setValue(readString(field));
        return optionValue;
    }

    private void setDeliveryPacingAndDailyBudget(Campaign campaign) {
        readString(CampaignFieldCsv.CampaignDailyBudget, true);
        try {
            // first try to read column as a number
            campaign.setDailyBudget(getNumericValue(CampaignFieldCsv.CampaignDailyBudget, true));
            campaign.setDeliveryPacing(DeliveryPacing.FIXED);
        } catch (ParseException e) {
            DeliveryPacing deliveryPacing = parseDeliveryPacing(CampaignFieldCsv.CampaignDailyBudget);
            campaign.setDeliveryPacing(deliveryPacing);
        }
    }

    private DeliveryPacing parseDeliveryPacing(CampaignFieldCsv column) {
        String value = readString(CampaignFieldCsv.CampaignDailyBudget);
        DeliveryPacing result = DeliveryPacing.UNRESTRICTED;
        try {
            result = DeliveryPacing.byNameIgnoreCase(value);
            if (result == DeliveryPacing.FIXED) {
                addError(column, "errors.upload.campaign.dailyBudget");
            }
        } catch (IllegalArgumentException e) {
            addError(column, "errors.upload.campaign.dailyBudget");
        }

        return result;
    }

    private CCGKeyword readKeyword() {
        CCGKeyword ccgKeyword = new CCGKeyword();
        ccgKeyword.setOriginalKeyword(readString(CampaignFieldCsv.Keyword));
        ccgKeyword.setMaxCpcBid(readBigDecimal(CampaignFieldCsv.KeywordRate));
        ccgKeyword.setClickURL(readString(CampaignFieldCsv.KeywordClickURL));
        ccgKeyword.setTriggerType(readKeywordTriggerType(CampaignFieldCsv.KeywordType));
        setStatus(ccgKeyword, CampaignFieldCsv.KeywordStatus);
        ccgKeywords.add(ccgKeyword);
        return ccgKeyword;
    }

    private KeywordTriggerType readKeywordTriggerType(CampaignFieldCsv column) {
        String str = readString(column);
        if (StringUtil.isPropertyNotEmpty(str)) {
            try {
                return KeywordTriggerType.byName(str);
            } catch (IllegalArgumentException e) {
                addError(column, "errors.field.invalid");
            }
        }
        return null;
    }

    private void setStatus(StatusEntityBase entityBase, CampaignFieldCsv column) {
        try {
            entityBase.setStatus(readStatus(column));
        } catch (IllegalArgumentException e) {
            addError(column, "errors.field.invalid");
            entityBase.setStatus(Status.ACTIVE); //can't assign null to status property
        }
    }

    private FrequencyCap readFrequencyCap(CampaignFieldCsv periodCol, CampaignFieldCsv windowCol,
            CampaignFieldCsv windowLengthCol, CampaignFieldCsv lifeCol) {
        FrequencyCap frequencyCap = new FrequencyCap();
        frequencyCap.setPeriodSpan(readTimeSpan(periodCol));
        frequencyCap.setWindowCount(readInteger(windowCol));
        frequencyCap.setWindowLengthSpan(readTimeSpan(windowLengthCol));
        frequencyCap.setLifeCount(readInteger(lifeCol));

        if (frequencyCap.isEmpty()) {
            return null;
        }

        return frequencyCap;
    }

    private TimeSpan readTimeSpan(CampaignFieldCsv column) {
        String string = readString(column);
        TimeSpan timeSpan;
        try {
            timeSpan = CampaignBulkHelper.parseTimeSpan(string);
        } catch (Exception e) {
            addError(column, "errors.field.intWithTimeUnit");
            return null;
        }
        return timeSpan;
    }

    private Country readCountry(CampaignFieldCsv column) {
        String code = readString(column);
        if (code == null) {
            return null;
        }

        return new Country(code);
    }

    private CcgRate readCcgRate(CampaignFieldCsv rateColumn, CampaignFieldCsv rateTypeColumn) {
        CcgRate rate = new CcgRate();

        RateType rateType = null;
        String string = readString(rateTypeColumn, true);
        try {
            rateType = CampaignBulkHelper.parseRateType(string);
        } catch (IllegalArgumentException e) {
            addError(rateTypeColumn, "errors.field.invalid");
        }

        if (rateType == null) {
            rateType = RateType.CPC;
        }

        rate.setRateType(rateType);

        switch (rateType) {
        case CPC:
            rate.setCpc(readBigDecimal(rateColumn));
            break;
        case CPM:
            rate.setCpm(readBigDecimal(rateColumn));
            break;
        case CPA:
            rate.setCpa(readBigDecimal(rateColumn));
            break;
        }

        return rate;
    }

    private User readUser(CampaignFieldCsv column) {
        String email = readString(column);
        if (email == null) {
            return null;
        }

        User user = new User();
        user.setEmail(email);
        return user;
    }

    private Status readStatus(CampaignFieldCsv column) {
        String string = readString(column);
        if (string == null) {
            addError(column, "errors.field.required");
            return Status.ACTIVE; //can't assign null to status property
        }

        try {
            return CampaignBulkHelper.parseStatus(string);
        } catch (IllegalArgumentException e) {
            addError(column, "errors.field.invalid");
            return Status.ACTIVE;
        }
    }

    private ApproveStatus readQaStatus(CampaignFieldCsv column) {
        String string = readString(column);
        if (string == null) {
            addError(column, "errors.field.required");
            return ApproveStatus.HOLD; //can't assign null to status property
        }

        try {
            return ApproveStatus.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            addError(column, "errors.field.invalid");
            return ApproveStatus.HOLD;
        }
    }

    private CampaignLink readCampaignLink(boolean required) {
        CampaignLink link = new CampaignLink();
        String name = readLinkName(link, CampaignFieldCsv.CampaignName, required);
        link.setCampaignName(name);
        return link;
    }

    private GroupLink readGroupLink(boolean required) {
        GroupLink groupLink = new GroupLink();
        CampaignLink campaignLink = new CampaignLink();
        campaignLink.setCampaignName(readLinkName(campaignLink, CampaignFieldCsv.CampaignName, required));
        groupLink.setGroupName(readLinkName(groupLink, CampaignFieldCsv.AdGroupName, required));
        groupLink.setCampaignLink(campaignLink);
        return groupLink;
    }

    private String readLinkName(ILinkMarker link, CampaignFieldCsv column, boolean required) {
        UploadContext uploadContext = new UploadContext(UploadStatus.LINK);
        linkUploadContexts.put(link, uploadContext);
        return readString(column, uploadContext, required);
    }

    private CampaignLevelCsv readLevel() {
        CampaignFieldCsv level = CampaignFieldCsv.Level;
        String levelStr = readString(level);
        if (levelStr == null) {
            currentStatus.addFatal("errors.field.required").withPath(level.getFieldPath());
            return null;
        }

        CampaignLevelCsv campaignLevelCsv = CampaignBulkHelper.parseLevel(levelStr);
        if (campaignLevelCsv == null) {
            currentStatus.addFatal("errors.field.invalid").withPath(level.getFieldPath());
            return null;
        }

        if (TGTType.CHANNEL == tgtType && CampaignLevelCsv.Keyword == campaignLevelCsv) {
            currentStatus.addFatal("errors.field.invalid").withPath(level.getFieldPath());
            return null;
        }

        return campaignLevelCsv;
    }

    private String readString(CampaignFieldCsv column) {
        return readString(column, false);
    }

    private String readString(CampaignFieldCsv column, boolean required) {
        return readString(column, currentStatus, required);
    }

    private String readString(CampaignFieldCsv column, UploadContext context, boolean required) {
        return getStringValue(column, required, context);
    }

    private String getStringValue(CampaignFieldCsv column) {
        return getStringValue(column, false);
    }

    private String getStringValue(CampaignFieldCsv column, boolean required) {
        return getStringValue(column, required, currentStatus);
    }

    private String getStringValue(CampaignFieldCsv column, boolean required, UploadContext context) {
        int index = metaData.getColumns().indexOf(column);
        if (index == -1) {
            return null;
        }

        String value = currentRow.getStringValue(index);
        if (required && value == null) {
            context.addError("errors.field.required").withPath(column.getFieldPath());
        }

        return value;
    }

    private Date readDateTime(CampaignFieldCsv column, boolean required) {
        try {
            Date value = getDateValue(column);
            if (required && value == null) {
                addError(column, "errors.field.required");
            }
            return value;
        } catch (ParseException e) {
            String value = getStringValue(column);
            String notSetPhrase = CampaignBulkHelper.getNotSetPhrase(column);
            if (StringUtil.compareToIgnoreCase(notSetPhrase, value) != 0) {
                addError(column, "errors.field.date");
            }
            return null;
        }
    }

    private Date getDateValue(CampaignFieldCsv column) throws ParseException {
        int index = metaData.getColumns().indexOf(column);
        if (index == -1) {
            return null;
        }
        return currentRow.getDateValue(index);
    }

    private BigDecimal readBigDecimal(CampaignFieldCsv column) {
        try {
            return getNumericValue(column);
        } catch (ParseException e) {
            addError(column, "errors.field.number");
            return null;
        }
    }

    private BigDecimal getNumericValue(CampaignFieldCsv column) throws ParseException {
        return getNumericValue(column, false);
    }

    private BigDecimal getNumericValue(CampaignFieldCsv column, boolean required) throws ParseException {
        int index = metaData.getColumns().indexOf(column);
        if (index == -1) {
            return null;
        }

        BigDecimal value = currentRow.getNumericValue(index);
        if (required && value == null) {
            addError(column, "errors.field.required");
        }

        return value;
    }

    private Long readLong(CampaignFieldCsv column) {
        BigDecimal bd = readBigDecimal(column);

        if (bd == null) {
            return null;
        }

        try {
            return bd.toBigIntegerExact().longValue();
        } catch (ArithmeticException ex) {
            addError(column, "errors.field.integer");
            return null;
        }
    }

    private Integer readInteger(CampaignFieldCsv column) {
        Long number = readLong(column);
        return convertLongToInt(number, column);
    }

    private Integer convertLongToInt(Long number, CampaignFieldCsv column) {
        if (number == null) {
            return null;
        }
        if (number > Integer.MAX_VALUE) {
            addError(column, "errors.field.tooLarge");
            return null;
        }
        return number.intValue();
    }

    private void addError(CampaignFieldCsv field, String key) {
        currentStatus.addError(key).withPath(field.getFieldPath());
    }

    private void assignOriginalValues(EntityBase entity) {
        Object[] record = new Object[CampaignFieldCsv.TOTAL_COLUMNS_COUNT];
        for (Column column : columns) {
            int index = metaData.getColumns().indexOf(column);
            if (currentRow.getColumnCount() > index) {
                record[((CampaignFieldCsv) column).ordinal()] = currentRow.getValue(index);
            }
        }
        entity.setProperty(ORIGINAL_VALUES, record);
    }

    private List<Creative> getCreatives() {
        List<Creative> result = new ArrayList<>(existedCreatives.size() + createdCreatives.size());
        result.addAll(existedCreatives.values());
        result.addAll(createdCreatives.values());
        return result;
    }

    private void setLastCreativeToCC() {
        for (CampaignCreative cc : campaignCreatives) {
            Long creativeId = cc.getCreative().getId();
            if (creativeId != null) {
                cc.setCreative(existedCreatives.get(creativeId));
            }
        }
    }

    private static interface ILinkMarker {
    }

    private static class CampaignLink implements ILinkMarker {
        protected String campaignName;

        private CampaignLink() {
        }

        public String getCampaignName() {
            return campaignName;
        }

        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            if (StringUtil.isPropertyEmpty(campaignName)) {
                return false;
            }

            CampaignLink that = (CampaignLink) o;

            return campaignName == null ? that.campaignName == null : campaignName.equals(that.campaignName);
        }

        @Override
        public int hashCode() {
            return campaignName != null ? campaignName.hashCode() : 0;
        }
    }

    private static class GroupLink implements ILinkMarker {

        private String groupName;

        private CampaignLink campaignLink;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public CampaignLink getCampaignLink() {
            return campaignLink;
        }

        public void setCampaignLink(CampaignLink campaignLink) {
            this.campaignLink = campaignLink;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GroupLink groupLink = (GroupLink) o;

            if (campaignLink == null ? groupLink.campaignLink != null : !campaignLink.equals(groupLink.campaignLink)) {
                return false;
            }

            return groupName == null ? groupLink.groupName == null : groupName.equals(groupLink.groupName);
        }

        @Override
        public int hashCode() {
            int result = groupName != null ? groupName.hashCode() : 0;
            result = 31 * result + (campaignLink != null ? campaignLink.hashCode() : 0);
            return result;
        }
    }
}
