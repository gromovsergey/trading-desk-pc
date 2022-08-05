package com.foros.session.campaign;

import com.foros.config.ConfigService;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.GenericAccount;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GenericChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.fileman.FileInfo;
import com.foros.model.security.User;
import com.foros.model.template.Option;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.TooManyRowsException;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignStatsTO.Builder;
import com.foros.session.campaign.bulk.BulkParseResult;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.UploadUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.apache.commons.io.IOUtils;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "BulkCampaignToolsService")
@Interceptors({ RestrictionInterceptor.class, PersistenceExceptionInterceptor.class })
public class BulkCampaignToolsServiceBean implements BulkCampaignToolsService {

    private static final String CAMPAIGNS_AND_CREATIVES = "campaignsAndCreatives";

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AccountService accountService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ConfigService config;

    @EJB
    private TemplateService templateService;

    @EJB
    protected DisplayCreativeService creativeService;

    private static class FreeEntrySpace {
        private int space;

        private FreeEntrySpace(int size) {
            this.space = size;
        }

        private void reserve(int size) {
            if (this.space - size < 0) {
                throw new TooManyRowsException();
            }
            this.space -= size;
        }
    }

    @Override
    @Restrict(restriction = "BulkTextCampaignUpload.export", parameters = "find('AdvertiserAccount', #accountId)")
    public Collection<Campaign> findForExport(Long accountId, TGTType tgtType, Collection<Long> ids, int maxResultSize)
            throws TooManyRowsException {
        FreeEntrySpace freeSpace = new FreeEntrySpace(maxResultSize);

        AdvertiserAccount account = accountService.findAdvertiserAccount(accountId);
        boolean isGross = account.getAccountType().isInputRatesAndAmountsFlag();

        Collection<Campaign> campaigns = findCampaigns(ids, account, isGross);
        freeSpace.reserve(campaigns.size());
        if (campaigns.size() > 0) {
            List<Long> foundIds = new ArrayList<>(campaigns.size());
            for (Campaign campaign : campaigns) {
                foundIds.add(campaign.getId());
            }

            Map<Long, Set<CampaignCreativeGroup>> ccgsMap = findCCGs(tgtType, foundIds, isGross, freeSpace);
            Map<Long, Set<CCGKeyword>> keywordsMap = findKeywords(tgtType, foundIds, isGross, freeSpace);
            Map<Long, Set<CampaignCreative>> ccsMap = findCampaignCreatives(tgtType, foundIds, isGross, account, freeSpace);

            for (Campaign campaign : campaigns) {
                Set<CampaignCreativeGroup> ccgs = ccgsMap.get(campaign.getId());
                campaign.setCreativeGroups(ccgs != null ? ccgs : Collections.<CampaignCreativeGroup> emptySet());
                for (CampaignCreativeGroup ccg : campaign.getCreativeGroups()) {
                    ccg.setCampaign(campaign);
                    if (TGTType.KEYWORD == tgtType) {
                        Set<CCGKeyword> keywords = keywordsMap.get(ccg.getId());
                        ccg.setCcgKeywords(keywords != null ? keywords : Collections.<CCGKeyword> emptySet());
                        for (CCGKeyword keyword : ccg.getCcgKeywords()) {
                            keyword.setCreativeGroup(ccg);
                        }
                    }
                    Set<CampaignCreative> ccs = ccsMap.get(ccg.getId());
                    ccg.setCampaignCreatives(ccs != null ? ccs : Collections.<CampaignCreative> emptySet());
                    for (CampaignCreative cc : ccg.getCampaignCreatives()) {
                        cc.setCreativeGroup(ccg);
                    }
                }
            }
        }
        return campaigns;
    }

    private Collection<Campaign> findCampaigns(Collection<Long> campaignIds, final AdvertiserAccount account, final boolean isGross) {
        return jdbcTemplate.query(
            "select * from statqueries.campaigns_for_export(?::int, ?::int[])",
            new Object[] { account.getId(), jdbcTemplate.createArray("int", campaignIds) },
            new RowMapper<Campaign>() {
                @Override
                public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Campaign campaign = new Campaign();
                    campaign.setId(rs.getLong("id"));
                    campaign.setName(rs.getString("name"));

                    BigDecimal budget = rs.getBigDecimal("budget_manual");
                    if (CampaignUtil.canUpdateBudget(account, budget)) {
                        campaign.setBudget(budget);
                    }

                    campaign.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                    campaign.setDateStart(rs.getTimestamp("date_start"));
                    campaign.setDateEnd(rs.getTimestamp("date_end"));
                    campaign.setDeliveryPacing(DeliveryPacing.valueOf(rs.getString("delivery_pacing").charAt(0)));
                    campaign.setDailyBudget(rs.getBigDecimal("daily_budget"));

                    User soldToUser = new User();
                    soldToUser.setEmail(rs.getString("sold_to_email"));
                    campaign.setSoldToUser(soldToUser);

                    User billToUser = new User();
                    billToUser.setEmail(rs.getString("bill_to_email"));
                    campaign.setBillToUser(billToUser);

                    FrequencyCap fc = new FrequencyCap();
                    fc.setPeriod(SQLUtil.nullSafeGet(rs, "period", Integer.class));
                    fc.setWindowLength(SQLUtil.nullSafeGet(rs, "window_length", Integer.class));
                    fc.setWindowCount(SQLUtil.nullSafeGet(rs, "window_count", Integer.class));
                    fc.setLifeCount(SQLUtil.nullSafeGet(rs, "life_count", Integer.class));
                    campaign.setFrequencyCap(fc);

                    BigDecimal inventoryCost = rs.getBigDecimal("adv_amount");
                    BigDecimal commission = rs.getBigDecimal("adv_comm_amount");
                    BigDecimal targetingCost = rs.getBigDecimal("adv_amount_cmp");

                    CampaignStatsTO stats = new Builder()
                            .spentBudget(isGross, inventoryCost.add(targetingCost), commission)
                            .imps(rs.getLong("imps"))
                            .clicks(rs.getLong("clicks"))
                            .build();
                    campaign.setProperty(CAMPAIGN_STATS, stats);

                    User salesManager = new User();
                    salesManager.setEmail(rs.getString("sales_manager_email"));
                    campaign.setSalesManager(salesManager);

                    return campaign;
                }
            }
        );
    }

    private Map<Long, Set<CampaignCreativeGroup>> findCCGs(TGTType tgtType, Collection<Long> campaignIds, final boolean isGross, FreeEntrySpace freeSpace)
            throws TooManyRowsException {

        Object[] params = new Object[] {tgtType.getLetter(), jdbcTemplate.createArray("int", campaignIds) };
        int count = jdbcTemplate.queryForObject("select * from entityqueries.ccgs_for_export_count(?::varchar, ?::int[])", params, Integer.class);
        freeSpace.reserve(count);

        return jdbcTemplate.query(
                "select * from statqueries.ccgs_for_export(?::varchar, ?::int[])",
                params,
                new MapExtractor<Long, CampaignCreativeGroup>() {
                    @Override
                    public Long getIndex(ResultSet rs) throws SQLException {
                        return rs.getLong("campaign_id");
                    }
                    @Override
                    public CampaignCreativeGroup mapRow(ResultSet rs) throws SQLException {
                        CampaignCreativeGroup ccg = new CampaignCreativeGroup();
                        ccg.setId(rs.getLong("ccg_id"));
                        ccg.setName(rs.getString("name"));

                        ccg.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                        ccg.setBudget(rs.getBigDecimal("budget"));
                        ccg.setDeliveryPacing(DeliveryPacing.valueOf(rs.getString("delivery_pacing").charAt(0)));
                        ccg.setDailyBudget(rs.getBigDecimal("daily_budget"));
                        ccg.setDateStart(rs.getTimestamp("date_start"));
                        ccg.setDateEnd(rs.getTimestamp("date_end"));
                        ccg.setCountry(new Country(rs.getString("country_code")));
                        ccg.setDeviceChannels(new HashSet<>(parseDeviceChannelIds(rs.getArray("device_channel_ids"))));

                        String channelTargetFlag = rs.getString("channel_target");
                        ccg.setChannelTarget(channelTargetFlag != null ? ChannelTarget.valueOf(channelTargetFlag.charAt(0)) : null);
                        Long channelTargetId = SQLUtil.nullSafeGet(rs, "channel_id", Long.class);
                        if (channelTargetId != null) {
                            GenericChannel channelTarget = new GenericChannel();
                            channelTarget.setId(channelTargetId);
                            channelTarget.setName(rs.getString("channel_name"));
                            GenericAccount channelTargetAccount = new GenericAccount();
                            channelTargetAccount.setName(rs.getString("channel_account_name"));
                            channelTarget.setAccount(channelTargetAccount);
                            ccg.setChannel(channelTarget);
                        }

                        CcgRate rate = new CcgRate();
                        rate.setRateType(RateType.valueOf(rs.getString("rate_type")));
                        rate.setCpa(rs.getBigDecimal("cpa"));
                        rate.setCpc(rs.getBigDecimal("cpc"));
                        rate.setCpm(rs.getBigDecimal("cpm"));
                        ccg.setCcgRate(rate);

                        FrequencyCap fc = new FrequencyCap();
                        fc.setPeriod(SQLUtil.nullSafeGet(rs, "period", Integer.class));
                        fc.setWindowLength(SQLUtil.nullSafeGet(rs, "window_length", Integer.class));
                        fc.setWindowCount(SQLUtil.nullSafeGet(rs, "window_count", Integer.class));
                        fc.setLifeCount(SQLUtil.nullSafeGet(rs, "life_count", Integer.class));
                        ccg.setFrequencyCap(fc);

                        BigDecimal spentBudget = rs.getBigDecimal("amount");
                        BigDecimal commission = rs.getBigDecimal("adv_comm_amount");
                        CampaignStatsTO stats = new Builder()
                                .spentBudget(isGross,  spentBudget, commission)
                                .imps(rs.getLong("imps"))
                                .clicks(rs.getLong("clicks"))
                                .build();
                        ccg.setProperty(CAMPAIGN_STATS, stats);

                        return ccg;
                    }

                    List<DeviceChannel> parseDeviceChannelIds(Array array) throws SQLException {
                        return PGArray.read(array, new PGRow.Converter<DeviceChannel>() {
                            @Override
                            public DeviceChannel item(PGRow row) {
                                Long id = row.getLong(0);
                                if (id == null) {
                                    return null;
                                }
                                DeviceChannel channel = new DeviceChannel();
                                channel.setId(id);
                                return channel;
                            }
                        });
                    }
                });
    }

    private Map<Long, Set<CCGKeyword>> findKeywords(TGTType tgtType, Collection<Long> campaignIds, final boolean isGross, FreeEntrySpace freeSpace)
            throws TooManyRowsException {
        if (TGTType.KEYWORD != tgtType) {
            return null;
        }

        Object[] params = new Object[] { jdbcTemplate.createArray("int", campaignIds) };
        int count = jdbcTemplate.queryForObject("select * from entityqueries.keywords_for_export_count(?::int[])", params, Integer.class);
        freeSpace.reserve(count);

        return jdbcTemplate.query(
                "select * from statqueries.keywords_for_export(?::int[])",
                params,
                new MapExtractor<Long, CCGKeyword>() {
                    @Override
                    public Long getIndex(ResultSet rs) throws SQLException {
                        return rs.getLong("ccg_id");
                    }
                    @Override
                    public CCGKeyword mapRow(ResultSet rs) throws SQLException {
                        CCGKeyword keyword = new CCGKeyword();

                        keyword.setOriginalKeyword(rs.getString("original_keyword"));
                        keyword.setTriggerType(KeywordTriggerType.byLetter(rs.getString("trigger_type").charAt(0)));
                        keyword.setMaxCpcBid(rs.getBigDecimal("max_cpc_bid"));
                        keyword.setClickURL(rs.getString("click_url"));
                        keyword.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                        BigDecimal spentBudget = rs.getBigDecimal("adv_amount");
                        BigDecimal commission = rs.getBigDecimal("adv_comm_amount");
                        CampaignStatsTO stats = new Builder()
                                .spentBudget(isGross, spentBudget, commission)
                                .imps(rs.getLong("imps"))
                                .clicks(rs.getLong("clicks"))
                                .build();
                        keyword.setProperty(CAMPAIGN_STATS, stats);

                        return keyword;
                    }
                });
    }

    private Map<Long, Set<CampaignCreative>> findCampaignCreatives(TGTType tgtType, Collection<Long> campaignIds,
            final boolean isGross, final AdvertiserAccount account, FreeEntrySpace freeSpace) throws TooManyRowsException {
        Object[] params = new Object[] { tgtType.getLetter(), jdbcTemplate.createArray("int", campaignIds) };
        int count = jdbcTemplate.queryForObject("select * from entityqueries.ccs_for_export_count(?::varchar, ?::int[])", params, Integer.class);
        freeSpace.reserve(count);

        Collection<Option> textOptions = templateService.findTextTemplate().getAdvertiserOptions();
        final Map<String, String> defaultOptionValues = new HashMap<>();
        for (Option o : textOptions) {
            defaultOptionValues.put(o.getToken(), o.getDefaultValue());
        }

        return jdbcTemplate.query(
                "select * from statqueries.ccs_for_export(?::varchar, ?::int[])",
                params,
                new MapExtractor<Long, CampaignCreative>() {
                    @Override
                    public Long getIndex(ResultSet rs) throws SQLException {
                        return rs.getLong("ccg_id");
                    }
                    @Override
                    public CampaignCreative mapRow(ResultSet rs) throws SQLException {
                        // text ad
                        Creative creative = new Creative(rs.getLong("creative_id"));
                        CampaignCreative campaignCreative = new CampaignCreative(rs.getLong("cc_id"));
                        campaignCreative.setCreative(creative);
                        campaignCreative.setStatus(Status.valueOf(rs.getString("cc_status").charAt(0)));
                        creative.setStatus(Status.valueOf(rs.getString("status").charAt(0)));
                        creative.setQaStatus(ApproveStatus.valueOf(rs.getString("qa_status").charAt(0)));

                        creative.getOptions().add(parseOptionValue(rs, "oh1v", TextCreativeOption.HEADLINE));
                        creative.getOptions().add(parseOptionValue(rs, "od1v", TextCreativeOption.DESCRIPTION_LINE_1));
                        creative.getOptions().add(parseOptionValue(rs, "od2v", TextCreativeOption.DESCRIPTION_LINE_2));
                        creative.getOptions().add(parseOptionValue(rs, "od3v", TextCreativeOption.DESCRIPTION_LINE_3));
                        creative.getOptions().add(parseOptionValue(rs, "od4v", TextCreativeOption.DESCRIPTION_LINE_4));
                        creative.getOptions().add(parseOptionValue(rs, "durlv", TextCreativeOption.DISPLAY_URL));
                        creative.getOptions().add(parseOptionValue(rs, "curlv", TextCreativeOption.CLICK_URL));
                        creative.getOptions().add(parseOptionValue(rs, "imgv", TextCreativeOption.IMAGE_FILE));

                        FrequencyCap fc = new FrequencyCap();
                        fc.setPeriod(SQLUtil.nullSafeGet(rs, "period", Integer.class));
                        fc.setWindowLength(SQLUtil.nullSafeGet(rs, "window_length", Integer.class));
                        fc.setWindowCount(SQLUtil.nullSafeGet(rs, "window_count", Integer.class));
                        fc.setLifeCount(SQLUtil.nullSafeGet(rs, "life_count", Integer.class));
                        campaignCreative.setFrequencyCap(fc);

                        // stats
                        BigDecimal spentBudget = rs.getBigDecimal("adv_amount");
                        BigDecimal commission = rs.getBigDecimal("adv_comm_amount");
                        CampaignStatsTO stats = new Builder()
                                .spentBudget(isGross, spentBudget, commission)
                                .imps(rs.getLong("imps"))
                                .clicks(rs.getLong("clicks"))
                                .build();
                        campaignCreative.setProperty(CAMPAIGN_STATS, stats);

                        return campaignCreative;
                    }
                    private CreativeOptionValue parseOptionValue(ResultSet rs, String index, TextCreativeOption creativeOption) throws SQLException {
                        String rsValue = rs.getString(index);
                        String value = rsValue != null ? rsValue : defaultOptionValues.get(creativeOption.getToken());

                        CreativeOptionValue optionValue = new CreativeOptionValue();
                        optionValue.setOption(new Option(creativeOption.getToken()));

                        if (TextCreativeOption.IMAGE_FILE.equals(creativeOption)) {
                            value = TextAdImageUtil.getSourceFilePath(config, account, value);
                        }
                        optionValue.setValue(value);

                        return optionValue;
                    }
                });
    }

    @Override
    @Restrict(restriction = "BulkTextCampaignUpload.upload", parameters = "find('AdvertiserAccount', #accountId)")
    public ValidationResultTO validateAll(Long accountId, TGTType tgtType, BulkParseResult result) {
        campaignService.validateAll(accountId, tgtType, result.getCampaigns());
        creativeService.validateAll(accountId, result.getCreatives());

        String validationId = saveResults(accountId, result);
        return fillValidationResult(result, validationId);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public BulkParseResult getValidatedResults(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        ObjectInputStream ois = null;
        BulkParseResult result = null;
        try {
            InputStream is = fs.readFile(getFileName(validationResultId, CAMPAIGNS_AND_CREATIVES));
            ois = new ObjectInputStream(is);
            result = (BulkParseResult) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
        return result;
    }

    @Override
    @Restrict(restriction = "BulkTextCampaignUpload.upload", parameters = "find('AdvertiserAccount', #accountId)")
    public void createOrUpdateAll(Long accountId, String validationResultId) {
        try {
            startProcessing(accountId, validationResultId);

            // to prevent Hibernate doing auto-flush
            Session hibernateSession = PersistenceUtils.getHibernateSession(em);
            hibernateSession.setFlushMode(FlushMode.MANUAL);
            // temporarily disable second level cache
            CacheMode formerCacheMode = hibernateSession.getCacheMode();
            hibernateSession.setCacheMode(CacheMode.IGNORE);

            BulkParseResult result = getValidatedResults(accountId, validationResultId);
            creativeService.createOrUpdateAll(accountId, result.getCreatives());
            campaignService.createOrUpdateAll(accountId, result.getCampaigns());

            // let's Hibernate do rest of the job
            hibernateSession.setFlushMode(FlushMode.AUTO);
            hibernateSession.setCacheMode(formerCacheMode);
            markProcessed(accountId, validationResultId);

        } catch (OptimisticLockException | VersionCollisionException | BusinessException e) {
            throwReuploadRequired(e);
        } finally {
            endProcessing(accountId, validationResultId);
        }
    }

    private void throwReuploadRequired(RuntimeException e) {
        throw ConstraintViolationException.newBuilder("TextAd.upload.version")
                .withValue(e)
                .build();
    }

    private void endProcessing(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String lock = getFileName(validationResultId, "lock");
        fs.delete(lock);
    }

    private void markProcessed(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String submitted = getFileName(validationResultId, "submitted");
        fs.lock(submitted);
    }

    private void startProcessing(Long accountId, String validationResultId) {
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String lock = getFileName(validationResultId, "lock");
        if (!fs.lock(lock)) {
            throw ConstraintViolationException.newBuilder("TextAd.upload.inProgress").build();
        }

        String submitted = getFileName(validationResultId, "submitted");
        if (fs.checkExist(submitted)) {
            throw ConstraintViolationException.newBuilder("TextAd.upload.alreadySubmitted").build();
        }
    }

    private ValidationResultTO fillValidationResult(BulkParseResult result, String validationId) {
        ValidationResultTO validationResult = new ValidationResultTO();
        validationResult.setId(validationId);
        for (Creative creative : result.getCreatives()) {
            validationResult.add(creative);
        }

        for (Campaign campaign : result.getCampaigns()) {
            validationResult.add(campaign);
            for (CampaignCreativeGroup group : campaign.getCreativeGroups()) {
                validationResult.add(group);
                for (CampaignCreative cc : group.getCampaignCreatives()) {
                    UploadContext context = UploadUtils.getUploadContext(cc);

                    if (UploadUtils.getUploadContext(cc.getCreative()).getStatus() == UploadStatus.REJECTED) {
                        context.mergeStatus(UploadStatus.REJECTED);
                        validationResult.addLineWithErrors(UploadUtils.getRowNumber(cc));
                        continue;
                    }

                    validationResult.add(cc);
                }
                for (CCGKeyword keyword : group.getCcgKeywords()) {
                    validationResult.add(keyword);
                }
            }
        }
        return validationResult;
    }

    private String saveResults(Long accountId, BulkParseResult result) {
        UUID uuid = UUID.randomUUID();
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        String fileName = getFileName(uuid, CAMPAIGNS_AND_CREATIVES);
        try (ObjectOutputStream oos = new ObjectOutputStream(fs.openFile(fileName))) {
            oos.writeObject(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uuid.toString();
    }

    private String getFileName(String validationResultId, String suffix) {
        return getFileName(UUID.fromString(validationResultId), suffix);
    }

    private String getFileName(UUID uuid, String suffix) {
        return uuid.toString() + "." + suffix;
    }

    private PathProvider getBulkPP(Long accountId) {
        return pathProviderService.getBulkUpload().getNested(accountId.toString(), OnNoProviderRoot.AutoCreate);
    }

    @Override
    public void deleteObsoleteValidatedResults() {
        long currentTime = new Date().getTime();
        FileSystem fs = pathProviderService.getBulkUpload().createFileSystem();
        try {
            for (FileInfo dirInfo : fs.getFileList("")) {
                if (dirInfo.isDirectory()) {
                    for (FileInfo fileInfo : fs.getFileList(dirInfo.getName())) {
                        // delete files which were last modified more than 1 day ago
                        if (fileInfo.getTime() < currentTime - 1000 * 60 * 60 * 24) {
                            fs.delete(dirInfo.getName(), fileInfo.getName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private abstract class MapExtractor<E, T> implements ResultSetExtractor<Map<E, Set<T>>> {
        @Override
        public Map<E, Set<T>> extractData(ResultSet rs) throws SQLException {
            Map<E, Set<T>> results = new HashMap<>();
            while (rs.next()) {
                E index = getIndex(rs);
                Set<T> collection = results.get(index);
                if (collection == null) {
                    collection = new LinkedHashSet<T>();
                    results.put(index, collection);
                }
                collection.add(mapRow(rs));
            }
            return results;
        }

        abstract E getIndex(ResultSet rs) throws SQLException;
        abstract T mapRow(ResultSet rs) throws SQLException;
    }
}
