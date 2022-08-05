package com.foros.action.campaign.campaignGroup;

import com.foros.action.admin.geoChannel.GeoChannelHelper;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Country;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.time.TimeSpan;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.NameTOComparator;
import com.foros.session.admin.country.CountryService;
import com.foros.session.campaign.CCGLightWeightStatsTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.campaign.ChannelRatesTO;
import com.foros.session.campaign.CombinedCCGKeywordTO;
import com.foros.session.campaign.ISPColocationTO;
import com.foros.session.campaign.LinkedConversionTO;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.session.channel.targeting.CCGTargetingStatsTO;
import com.foros.session.creative.BaseLinkedTO;
import com.foros.session.creative.CreativeSetTO;
import com.foros.session.query.PartialList;
import com.foros.session.reporting.ReportHelper;
import com.foros.util.CCGChannelRatesUtil;
import com.foros.util.CurrencyHelper;
import com.foros.util.DateHelper;
import com.foros.util.EntityUtils;
import com.foros.util.PairUtil;
import com.foros.util.RegularChecksUtil;
import com.foros.util.Schema;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;
import com.foros.util.context.RequestContexts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

public class ViewCampaignGroupAction extends CampaignGroupActionSupport implements ServletRequestAware, RequestContextsAware, BreadcrumbsSupport {
    private static final int PAGE_SIZE = 50;
    private static final int MAX_PAGE = Integer.MAX_VALUE / PAGE_SIZE + 1;
    private static final String ACTION_SERVER_ID_PARAM = "cid*eql*";

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @EJB
    private CountryService countryService;

    @EJB
    private ConfigService configService;

    @EJB
    private GeoChannelService geoChannelService;

    private HttpServletRequest request;

    private Long id;

    private String xSelect = "30days";
    private String y1Select = "imps";
    private String y2Select = "clicks";

    private String fastChangeIdCreatives = "MTD";
    private String fastChangeIdKeywords = "MTD";
    private String fastChangeIdConversions = "TOT";

    private String agencyPair;
    private Long totalImpressions;
    private Boolean isInternal;
    private List<EntityTO> groupSites;
    private List<ISPColocationTO> groupColocations;
    private String totalRateDescription;
    private ChannelRatesTO rates;
    private CCGLightWeightStatsTO ccgStats;
    private BigDecimal calculatedDailyBudget;
    private String targetingRate;
    private Boolean availableCreditUsed;
    private PartialList<CreativeSetTO> linkedCreatives;
    private List<LinkedConversionTO> linkedConversions;
    private List<CombinedCCGKeywordTO> keywords;
    private Integer negativeKeywordsNumber;
    private boolean isViewDeliverySchedule;

    private CCGTargetingStatsTO targetingStats;
    private boolean isShowUniqueUsers;
    private boolean showPostImpConv;
    private boolean showPostClickConv;

    public boolean isShowPostImpConv() {
        return showPostImpConv;
    }

    public boolean isShowPostClickConv() {
        return showPostClickConv;
    }

    private boolean isNoFreqCapsWarning;

    private Country country;

    private Integer page = 1;
    private List<Integer> creativeSets;
    private Timestamp creativesMaxVersion;

    @ReadOnly
    public String showTargeting() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        targetingStats = campaignCreativeGroupService.fetchTargetingStats(id, getGroupSites().size() <= 5);

        prepareDeviceChannels();
        prepareGeoTarget();
        return SUCCESS;
    }

    @ReadOnly
    public String showSites() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        targetingStats = campaignCreativeGroupService.fetchTargetingStats(id, true);

        return SUCCESS;
    }


    @ReadOnly
    public String view() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        setNoFreqCapsWarning(campaignCreativeGroupService.hasNoFreqCapWarningForCcg(campaignCreativeGroup));

        if (!checkGroupType(campaignCreativeGroup.getCcgType())) {
            throw new EntityNotFoundException("Entity with id = " + id + " not found");
        }

        isViewDeliverySchedule = campaignCreativeGroupService.isFallingOutOfCampaignSchedule(campaignCreativeGroup.getId());

        return SUCCESS;
    }

    private Country getCountry() {
        if (country == null) {
            country = countryService.find(campaignCreativeGroup.getCountry().getCountryCode());
        }
        return country;
    }

    private void prepareDeviceChannels() {
        TreeSet<DeviceChannel> sorted = new TreeSet<>(new Comparator<DeviceChannel>() {
            @Override
            public int compare(DeviceChannel o1, DeviceChannel o2) {
                return StringUtil.lexicalCompare(fullName(o1), fullName(o2));
            }

            private String fullName(DeviceChannel channel) {
                StringBuilder sb = new StringBuilder(100);
                List<DeviceChannel> path = deviceChannelPath(channel);
                for (int i = 0; i < path.size(); i++) {
                    if (i > 0) {
                        sb.append('\n');
                    }
                    sb.append(path.get(i).getName());
                }
                return sb.toString();
            }
        });
        sorted.addAll(campaignCreativeGroup.getDeviceChannels());
        campaignCreativeGroup.setDeviceChannels(sorted);
    }

    public List<DeviceChannel> deviceChannelPath(DeviceChannel channel) {
        LinkedList<DeviceChannel> path = new LinkedList<>();
        deviceChannelPath(path, channel);
        return path;
    }

    private void deviceChannelPath(LinkedList<DeviceChannel> path, DeviceChannel channel) {
        if (channel.getParentChannel() != null) {
            deviceChannelPath(path, channel.getParentChannel());
        }
        path.add(channel);
    }

    private void prepareGeoTarget() {
        Set<GeoChannel> channels;
        if (!campaignCreativeGroup.getGeoChannels().isEmpty()) {
            channels = GeoChannelHelper.appendStatusSuffixAndSortForGeoTarget(campaignCreativeGroup.getGeoChannels());
        } else {
            String countryCode = campaignCreativeGroup.getCountry().getCountryCode();
            GeoChannel countryChannel = geoChannelService.findCountryChannel(countryCode);
            channels = Collections.singleton(countryChannel);
        }
        campaignCreativeGroup.setGeoChannels(channels);
    }

    @ReadOnly
    public String commonView() {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        campaignCreativeGroup = campaignCreativeGroupService.find(id);

        switch (campaignCreativeGroup.getCcgType()) {
            case DISPLAY:
                return "success.display";
            case TEXT:
                return "success.text";
            default:
                throw new IllegalArgumentException("Invalid CCG Type: " + campaignCreativeGroup.getCcgType());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getXSelect() {
        return xSelect;
    }

    public void setXSelect(String xSelect) {
        this.xSelect = xSelect;
    }

    public String getY1Select() {
        return y1Select;
    }

    public void setY1Select(String y1Select) {
        this.y1Select = y1Select;
    }

    public String getY2Select() {
        return y2Select;
    }

    public void setY2Select(String y2Select) {
        this.y2Select = y2Select;
    }

    public String getFastChangeIdCreatives() {
        return fastChangeIdCreatives;
    }

    public void setFastChangeIdCreatives(String fastChangeIdCreatives) {
        this.fastChangeIdCreatives = fastChangeIdCreatives;
    }

    public String getFastChangeIdKeywords() {
        return fastChangeIdKeywords;
    }

    public void setFastChangeIdKeywords(String fastChangeIdKeywords) {
        this.fastChangeIdKeywords = fastChangeIdKeywords;
    }

    public String getAgencyPair() {
        if (agencyPair != null) {
            return agencyPair;
        }

        AdvertiserAccount account = campaignCreativeGroup.getAccount();

        if (!account.isInAgencyAdvertiser()) {
            agencyPair = "";
        } else {
            agencyPair = PairUtil.createAsString(account.getAgency().getId(), account.getAgency().getName());
        }

        return agencyPair;
    }

    public Long getTotalImpressions() {
        if (totalImpressions != null) {
            return totalImpressions;
        }
        totalImpressions = campaignCreativeGroupService.getTotalImpressions(id);
        return totalImpressions;
    }

    @Override
    public boolean isInternal() {
        if (isInternal != null) {
            return isInternal;
        }
        isInternal = currentUserService.isInternal();
        return isInternal;
    }

    public boolean isShowExpressionPerformance() {
        if (!ChannelTarget.TARGETED.equals(campaignCreativeGroup.getChannelTarget())) {
            return false;
        }

        Channel targetChannel = campaignCreativeGroup.getChannel();
        if (targetChannel == null || !Channel.CHANNEL_TYPE_EXPRESSION.equals(targetChannel.getChannelType())) {
            return false;
        }

        if (getTotalImpressions() < 1) {
            return false;
        }
        return true;
    }

    public List<EntityTO> getGroupSites() {
        if (groupSites != null) {
            return groupSites;
        }
        groupSites = new ArrayList<>(campaignCreativeGroupService.fetchLinkedSites(id, false));
        Collections.sort(groupSites, new NameTOComparator<EntityTO>());
        EntityUtils.applyStatusRules(groupSites, null, true);
        return groupSites;
    }

    public List<ISPColocationTO> getGroupColocations() {
        if (groupColocations == null) {
            groupColocations = new ArrayList<>(campaignCreativeGroupService.findLinkedColocations(id));
            Collections.sort(groupColocations, new NameTOComparator<EntityTO>());
            EntityUtils.applyStatusRules(groupColocations, null, true);
        }
        return groupColocations;
    }

    public String getTotalRateDescription() {
        if (totalRateDescription != null) {
            return totalRateDescription;
        }
        if (campaignCreativeGroup.getChannel() == null) {
            totalRateDescription = "";
        } else {
            totalRateDescription = CCGChannelRatesUtil.getPopulatedTotalRates(getRates(), campaignCreativeGroup.getAccount().getCurrency().getCurrencyCode());
        }
        return totalRateDescription;
    }

    private ChannelRatesTO getRates() {
        if (rates != null) {
            return rates;
        }
        if (campaignCreativeGroup.getChannel() == null) {
            return null;
        }
        rates = campaignCreativeGroupService.getCcgTargetingRates(id, campaignCreativeGroup.getChannel().getId());
        return rates;
    }

    public CCGLightWeightStatsTO getCcgStats() {
        if (ccgStats != null) {
            return ccgStats;
        }

        boolean isGross = campaignCreativeGroup.getAccount().getAccountType().isInputRatesAndAmountsFlag();
        ccgStats = campaignCreativeGroupService.getLightWeightStats(id, isGross);
        return ccgStats;
    }

    public BigDecimal getCalculatedDailyBudget() {
        if (calculatedDailyBudget != null) {
            return calculatedDailyBudget;
        }
        switch (campaignCreativeGroup.getDeliveryPacing()) {
            case UNRESTRICTED:
                return null;
            case FIXED:
                calculatedDailyBudget = campaignCreativeGroup.getDailyBudget();
                break;
            case DYNAMIC:
                calculatedDailyBudget =
                        campaignCreativeGroupService.calculateDynamicDailyBudget(
                                id,
                                campaignCreativeGroup.getCampaign().getId(),
                                null,
                                campaignCreativeGroup.getBudget(),
                                campaignCreativeGroup.getDateStart(),
                                campaignCreativeGroup.getDateEnd());
                break;
        }

        return calculatedDailyBudget;
    }

    public String getTargetingRate() {
        if (targetingRate != null) {
            return targetingRate;
        }
        if (campaignCreativeGroup.getChannel() == null) {
            targetingRate = "";
        } else {
            targetingRate = CCGChannelRatesUtil.getPopulatedTargetingRates(getRates());
        }
        return targetingRate;
    }

    public String getPixelCode() {
        String adDomain = getCountry().getAdservingDomainOrDefault(configService.get(ConfigParameters.DEFAULT_ADSERVING_DOMAIN));
        adDomain = UrlUtil.concat(adDomain, "services/ActionServer");
        String src = Schema.DEFAULT.getValue() + UrlUtil.stripSchema(adDomain);

        StringBuilder result = new StringBuilder("<img width=&quot;0&quot; height=&quot;0&quot;&#10;  src=&quot;");
        result.append(src);
        result.append('/');
        result.append(ACTION_SERVER_ID_PARAM + getId());
        result.append("&quot;>");
        return result.toString();
    }

    @ReadOnly
    public String creativesStats() throws ParseException {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        getCreativeSets();
        return SUCCESS;
    }

    @ReadOnly
    public String conversionStats() throws ParseException {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        getLinkedConversions();
        return SUCCESS;
    }


    @ReadOnly
    public String keywordsStats() throws ParseException {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        getKeywords();

        return SUCCESS;
    }

    public boolean isAvailableCreditUsed() throws Exception {
        if (availableCreditUsed == null) {
            availableCreditUsed = false;
            for (CreativeSetTO to : getCreativeSets()) {
                for (BaseLinkedTO linkedTO : to.getLinkedTOs()) {
                    if (linkedTO.getCreditUsed() != null && linkedTO.getCreditUsed().compareTo(BigDecimal.ZERO) != 0) {
                        availableCreditUsed = true;
                        break;
                    }
                }
            }
        }
        return availableCreditUsed;
    }

    public List<Integer> getCreativeSetCount() {
        if (creativeSets == null) {
            creativeSets = new ArrayList<>();
            int creativeSetCount = campaignCreativeService.getCreativeSetCountByCcgId(id);
            for (int i = 1; i <= creativeSetCount; i++) {
                creativeSets.add(i);
            }
        }
        return creativeSets;
    }

    public Timestamp getCreativesMaxVersion() {
        if (creativesMaxVersion == null) {
            creativesMaxVersion = campaignCreativeService.getCreativesMaxVersionByCcgId(id);
        }
        return creativesMaxVersion;
    }

    public List<LinkedConversionTO> getLinkedConversions() throws ParseException {
        if (linkedConversions == null) {
            String range = fastChangeIdConversions;
            if (StringUtil.isPropertyEmpty(range)) {
                range = (String) request.getAttribute("fastChangeIdConversions");
            }
            // date parameters are already in account time zone
            LinkedEntitiesParams conversionParams = new LinkedEntitiesParams(request, "Conversions", TimeZone.getTimeZone("GMT"), range);
            linkedConversions = campaignCreativeGroupService.getLinkedConversions(id, conversionParams.getFromDate(), conversionParams.getToDate());
        }
        return linkedConversions;
    }

    public PartialList<CreativeSetTO> getCreativeSets() throws ParseException {
        if (linkedCreatives != null) {
            return linkedCreatives;
        }
        String range = fastChangeIdCreatives;
        if (StringUtil.isPropertyEmpty(range)) {
            range = (String) request.getAttribute("fastChangeIdCreatives");
        }
        // date parameters are already in account time zone
        LinkedEntitiesParams creativesParams = new LinkedEntitiesParams(request, "Creatives", TimeZone.getTimeZone("GMT"), range);
        setShowUniqueUsers(creativesParams);

        CampaignCreativeGroupService.PartialCreativeSetTOList linkedCreativesTmp = campaignCreativeGroupService.getLinkedCreatives(
                id, creativesParams.getFromDate(), creativesParams.getToDate(), checkConvertPageToOffset(), getPageSize());
        showPostClickConv = linkedCreativesTmp.isShowPostClickConv();
        showPostImpConv = linkedCreativesTmp.isShowPostImpConv();
        linkedCreatives = linkedCreativesTmp;
        return linkedCreatives;
    }

    private void setShowUniqueUsers(LinkedEntitiesParams creativesParams) {
        LocalDate toDate = creativesParams.getToDate();
        LocalDate fromDate = creativesParams.getFromDate();

        if (toDate == null || fromDate == null) {
            isShowUniqueUsers = true;
            return;
        }

        isShowUniqueUsers = ReportHelper.isLessThanMonth(fromDate.toDateTimeAtStartOfDay(), toDate.toDateTimeAtStartOfDay());
    }

    public List<CombinedCCGKeywordTO> getKeywords() throws ParseException {
        if (keywords != null) {
            return keywords;
        }

        String range = fastChangeIdKeywords;

        if (StringUtil.isPropertyEmpty(range)) {
            range = (String) request.getAttribute("fastChangeIdCreatives");
        }

        // date parameters are already in account time zone
        LinkedEntitiesParams keywordsParams = new LinkedEntitiesParams(request, "Keywords", TimeZone.getTimeZone("GMT"), range);
        keywords = campaignCreativeGroupService.getLinkedKeywords(campaignCreativeGroup.getId(), keywordsParams.getFromDate(), keywordsParams.getToDate());

        if (keywords != null) {
            List<CombinedCCGKeywordTO> nonNegativeKeywords = new ArrayList<>(keywords.size());
            negativeKeywordsNumber = 0;

            for (CombinedCCGKeywordTO kwd: keywords) {
                if (!kwd.isNegative()) {
                    nonNegativeKeywords.add(kwd);
                } else if (kwd.getDisplayStatus() != CCGKeyword.DELETED) {
                    negativeKeywordsNumber++;
                }
            }

            keywords = nonNegativeKeywords;
        }
        return keywords;
    }

    public int getNegativeKeywordsNumber() throws ParseException {
        if (negativeKeywordsNumber == null) {
            getKeywords();
        }
        return negativeKeywordsNumber;
    }

    private static class LinkedEntitiesParams {
        private LocalDate fromDate;
        private LocalDate toDate;

        public LinkedEntitiesParams(HttpServletRequest request, String extension, TimeZone timeZone, String range) throws ParseException {
            if ("TOT".equals(range)) return;

            Locale locale = CurrentUserSettingsHolder.getLocale();

            if (request.getParameter("fastChangeId" + extension) != null) {
                String fromDate = request.getParameter("fromDate" + extension);
                if (fromDate != null) {
                    this.fromDate = DateHelper.parseLocalDate(fromDate, locale);
                }

                String toDate = request.getParameter("toDate" + extension);
                if (toDate != null) {
                    this.toDate = DateHelper.parseLocalDate(toDate, locale);
                }
            } else {
                request.setAttribute("fastChangeId" + extension, "MTD");
                Calendar firstDayOfMonth = Calendar.getInstance(timeZone, locale);
                firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
                firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
                firstDayOfMonth.set(Calendar.MINUTE, 0);
                firstDayOfMonth.set(Calendar.SECOND, 0);
                firstDayOfMonth.set(Calendar.MILLISECOND, 0);
                fromDate = LocalDate.fromCalendarFields(firstDayOfMonth);
                toDate = LocalDate.fromCalendarFields(Calendar.getInstance(timeZone, locale));
                request.setAttribute("fromDate" + extension, fromDate);
                request.setAttribute("toDate" + extension, toDate);
            }
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }
    }

    public CampaignCreativeGroup getExistingGroup() {
        return campaignCreativeGroup;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(campaignCreativeGroup.getAccount());
    }

    public boolean isViewDeliverySchedule() {
        return isViewDeliverySchedule;
    }

    public boolean isShowUniqueUsers() {
        return isShowUniqueUsers;
    }

    public int getAuctionEcpmPrecision() {
        return CurrencyHelper.getCurrencyFractionDigits(getModel().getAccount().getCurrency().getCurrencyCode()) + 2;
    }

    public String getCheckStatusCaption() {
        boolean hourlyCheck = true;
        if (campaignCreativeGroup.getInterval() != null) {
            TimeSpan checkInterval = campaignCreativeGroup.getAccount().getAccountType().getCampaignCheckByNum(campaignCreativeGroup.getInterval());
            hourlyCheck = checkInterval.getValueInSeconds() / 3600 / 24 < 1;
        }
        return RegularChecksUtil.getCheckStatusCaption(campaignCreativeGroup, CurrentUserSettingsHolder.getLocale(), hourlyCheck);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign())).add(new CampaignGroupBreadcrumbsElement(campaignCreativeGroup));
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotal() {
        return linkedCreatives.getTotal();
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    private int checkConvertPageToOffset() {
        if (page < 1) {
            page = 1;
        }
        if (page > MAX_PAGE) {
            page = MAX_PAGE;
        }
        return (page - 1) * PAGE_SIZE;
    }

    public CCGTargetingStatsTO getTargetingStats() {
        return targetingStats;
    }

    public boolean isInRandomMode() {
        return getCcgStats().getAuctionEcpm() == null;
    }

    public boolean isNoFreqCapsWarning() {
        return isNoFreqCapsWarning;
    }

    public void setNoFreqCapsWarning(boolean isNoFreqCapsWarning) {
        this.isNoFreqCapsWarning = isNoFreqCapsWarning;
    }

    public String getFastChangeIdConversions() {
        return fastChangeIdConversions;
    }

    public void setFastChangeIdConversions(String fastChangeIdConversions) {
        this.fastChangeIdConversions = fastChangeIdConversions;
    }

    public Collection<Channel> getExcludedChannels() {
        return campaignService.getExcludedChannels(campaignCreativeGroup.getCampaign().getId());
    }
}
