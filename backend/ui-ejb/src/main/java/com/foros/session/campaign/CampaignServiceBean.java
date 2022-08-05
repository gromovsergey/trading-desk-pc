package com.foros.session.campaign;

import com.foros.cache.generic.interceptor.CacheInterceptor;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignSchedule;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.TGTType;
import com.foros.model.campaign.WeekSchedule;
import com.foros.model.channel.Channel;
import com.foros.model.finance.Invoice;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatusAction;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.TreeFilterElementTOConverter;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.UtilityService;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.campaign.CampaignStatsTO.Builder;
import com.foros.session.campaign.ChartStats.ChartHelper;
import com.foros.session.campaign.bulk.CampaignSelector;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.targeting.TargetingChannelService;
import com.foros.session.db.DBConstraint;
import com.foros.session.frequencyCap.FrequencyCapMerger;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignQuery;
import com.foros.session.query.campaign.CampaignQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.ApprovalAction;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.session.workflow.WorkflowService;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.UploadUtils;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;


@Stateless(name = "CampaignService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class, CacheInterceptor.class})
public class CampaignServiceBean implements CampaignService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private UtilityService utilityService;

    @EJB
    private CampaignCreativeGroupService groupService;

    @EJB
    private AuditService auditService;

    @EJB
    private WorkflowService workflowService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private AccountService accountService;

    @EJB
    private UserService userService;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private StatusService statusService;

    @EJB
    private ValidationService validationService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CampaignValidations campaignValidations;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private TargetingChannelService targetingChannelService;

    private FrequencyCapMerger<Campaign> frequencyCapMerger = new FrequencyCapMerger<Campaign>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    private ScheduleMerger<CampaignSchedule> campaignScheduleMerger = new ScheduleMerger<CampaignSchedule>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    public CampaignServiceBean() {
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<Campaign> get(CampaignSelector campaignSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(campaignSelector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(campaignSelector.getCampaigns())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.campaign");
        }

        PartialList<Campaign> campaigns = createCampaignQuery()
                .advertisers(campaignSelector.getAdvertiserIds())
                .campaigns(campaignSelector.getCampaigns())
                .statuses(campaignSelector.getStatuses())
                .type(campaignSelector.getCampaignType())
                .addDefaultOrder()
                .executor(executorService)
                .partialList(campaignSelector.getPaging());

        em.clear();

        boolean external = currentUserService.isExternal();
        for (Campaign campaign : campaigns) {
            SortedSet<CampaignSchedule> sortedSchedules = new TreeSet<CampaignSchedule>(new ScheduleComparator());
            sortedSchedules.addAll(campaign.getCampaignSchedules());
            campaign.setCampaignSchedules(sortedSchedules);
            if (campaign.getAccount().getAccountType().getIoManagement()) {
                campaign.setBudget(null);
            }
            if (external) {
                campaign.setMaxPubShare(null);
            }
            FrequencyCap frequencyCap = campaign.getFrequencyCap();
            if (frequencyCap != null) {
                frequencyCap.setVersion(null);
            }
        }

        return new Result<>(campaigns);
    }

    private CampaignQuery createCampaignQuery() {
        return new CampaignQueryImpl().restrict();
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    @Validate(validation = "Operations.integrity", parameters = {"#campaignOperations", "'campaign'"})
    public OperationsResult perform(Operations<Campaign> campaignOperations) {
        // to prevent Hibernate doing auto-flush
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        fetch(campaignOperations);

        // validate
        validationService.validate("Campaign.merge", campaignOperations).throwIfHasViolations();

        List<Long> result = new ArrayList<>();

        for (Operation<Campaign> campaignMergeOperation : campaignOperations.getOperations()) {
            result.add(processMergeOperation(campaignMergeOperation));
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.CAMPAIGN_NAME.match(e)) {
                validationService.validateInNewTransaction("Campaign.nameConstraintViolations", campaignOperations).throwIfHasViolations();
            }

            throw e;
        }

        // let's Hibernate do rest of the job
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);

        return new OperationsResult(result);
    }

    private void fetch(Operations<Campaign> campaignOperations) {
        List<Long> campaignIds = new ArrayList<>();
        for (Operation<Campaign> operation : campaignOperations) {
            campaignIds.add(operation.getEntity().getId());
        }

        if (!campaignIds.isEmpty()) {
            new CampaignQueryImpl()
                    .campaigns(campaignIds)
                    .executor(executorService)
                    .list();
        }

        for (Operation<Campaign> operation : campaignOperations) {
            Campaign campaign = operation.getEntity();
            if (operation.getOperationType() == OperationType.UPDATE && campaign.getId() != null) {
                Campaign existingCampaign = em.find(Campaign.class, campaign.getId());
                if (existingCampaign != null) {
                    campaign.setAccount(existingCampaign.getAccount());
                }
            }
        }
    }

    private Long processMergeOperation(Operation<Campaign> mergeOperation) {
        Campaign campaign = mergeOperation.getEntity();

        switch (mergeOperation.getOperationType()) {
            case CREATE:
                campaign.setId(null);
                return createInternal(campaign);
            case UPDATE:
                updateInternal(campaign);
                return campaign.getId();
        }

        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

    private void prePersist(Campaign campaign, AdvertiserAccount account) {
        // check daily budget
        if (campaign.isChanged("deliveryPacing") && campaign.getDeliveryPacing() != DeliveryPacing.FIXED) {
            campaign.setDailyBudget(null);
        }

        // check commission
        if (campaign.getCommission() == null) {
            campaign.setCommission(BigDecimal.ZERO);
        }

        // sales manager
        if (campaign.getSalesManager() != null) {
            User salesManager = campaign.getSalesManager();
            if (salesManager.getId() == null) {
                campaign.setSalesManager(null);
            } else {
                campaign.setSalesManager(em.getReference(User.class, salesManager.getId()));
            }
        }

        // sold to user
        if (campaign.getSoldToUser() != null) {
            User soldToUser = campaign.getSoldToUser();
            if (soldToUser.getId() == null) {
                campaign.setSoldToUser(null);
            } else {
                campaign.setSoldToUser(em.getReference(User.class, soldToUser.getId()));
            }
        }

        // bill to user
        if (campaign.getBillToUser() != null) {
            User billToUser = campaign.getBillToUser();
            if (billToUser.getId() == null) {
                campaign.setBillToUser(null);
            } else {
                campaign.setBillToUser(em.getReference(User.class, billToUser.getId()));
            }
        }

        // set default value for marketplace type
        if (campaign.getId() == null && !campaign.isChanged("marketplaceType")) {
            WalledGarden wg = walledGardenService.findByAdvertiser(account.getId());
            campaign.setMarketplaceType(wg == null ? MarketplaceType.NOT_SET : wg.getAgencyMarketplaceType());
        }

        // set delivery period
        if (campaign.isChanged("campaignSchedules") && campaign.getCampaignSchedules() != null) {
            for (CampaignSchedule campaignSchedule : campaign.getCampaignSchedules()) {
                campaignSchedule.setCampaign(campaign);
            }
        }

        // set frequency caps
        FrequencyCap frequencyCap = campaign.getFrequencyCap();
        if (frequencyCap != null) {
            if (frequencyCap.isEmpty()) {
                campaign.setFrequencyCap(null);
            } else {
                if (frequencyCap.getPeriodSpan() != null && frequencyCap.getPeriodSpan().getValue() == null) {
                    frequencyCap.setPeriodSpan(null);
                }
                if (frequencyCap.getWindowLengthSpan() != null && frequencyCap.getWindowLengthSpan().getValue() == null) {
                    frequencyCap.setWindowLengthSpan(null);
                }
            }
        }

        // excluded channels
        if (campaign.isChanged("excludedChannels")) {
            Set<Channel> channels = new LinkedHashSet<>();
            for (Channel channel : campaign.getExcludedChannels()) {
                channel = em.getReference(Channel.class, channel.getId());
                channels.add(channel);
            }
            campaign.setExcludedChannels(channels);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createCopy", parameters = "find('Campaign', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long createCopy(Long id) {
        Campaign existingCampaign = em.find(Campaign.class, id);

        Campaign newCampaign = EntityUtils.clone(existingCampaign);

        BigDecimal commission = getCommission(existingCampaign.getAccount());
        newCampaign.setCommission(commission);

        String name = utilityService.calculateNameForCopy(existingCampaign, 100);
        newCampaign.setName(name);

        Collection<CampaignCreativeGroup> groups = newCampaign.getCreativeGroups();
        newCampaign.setCreativeGroups(new LinkedHashSet<CampaignCreativeGroup>());

        if (!CampaignUtil.canCreateBudget(existingCampaign.getAccount())) {
            newCampaign.setBudget(BigDecimal.ZERO);
        }

        createInternal1(newCampaign);

        for (CampaignCreativeGroup group : groups) {
            groupService.createCopyWithNewCampaign(group.getId(), newCampaign.getId());
        }

        return newCampaign.getId();
    }

    private BigDecimal getCommission(AdvertiserAccount advAccount) {
        BigDecimal commission = null;

        if(advAccount.isInAgencyAdvertiser()){
            if (advAccount.getAccountType().isAgencyFinancialFieldsFlag() && advAccount.getAgency().getFinancialSettings()!= null) {
                commission = advAccount.getAgency().getFinancialSettings().getCommission();
            } else if(advAccount.getFinancialSettings()!= null) {
                commission = advAccount.getFinancialSettings().getCommission();
            }
        } else if(advAccount.getFinancialSettings()!= null) {
                commission = advAccount.getFinancialSettings().getCommission();
        }
        return commission;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Account', #accountId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long accountId, Collection<Campaign> campaigns) {
        for (Campaign campaign : campaigns) {
            boolean isNotLink = !UploadUtils.isLink(campaign);
            if (isNotLink) {
                UploadUtils.throwIfErrors(campaign);

                if (campaign.getId() == null) {
                    createInternal(campaign);
                } else {
                    updateInternal(campaign);
                }
            }

            groupService.createOrUpdateAll(campaign.getId(), campaign.getCreativeGroups());

            PersistenceUtils.flushAndClear(em, new Filter<Integer>() {
                @Override
                public boolean accept(Integer entitiesSize) {
                    return entitiesSize > 1000;
                }
            });
        }

        PersistenceUtils.flushAndClear(em);
    }

    @Override
    @Restrict(restriction = "Campaign.create", parameters = "#campaign")
    @Validate(validation = "Campaign.create", parameters = "#campaign")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public Long create(Campaign campaign) {
        campaign.unregisterChange("status");
        Long id = createInternal(campaign);
        executeUpdateBudget();
        return id;
    }

    private Long createInternal(Campaign campaign) {
        // use own account for advertisers
        if (SecurityContext.isAdvertiser()) {
            AdvertiserAccount account = new AdvertiserAccount(SecurityContext.getPrincipal().getAccountId());
            campaign.setAccount(account);
        }

        AdvertiserAccount advertiserAccount = accountService.findAdvertiserAccount(campaign.getAccount().getId());
        campaign.setAccount(advertiserAccount);

        BigDecimal commission = getCommission(campaign.getAccount());
        campaign.setCommission(commission);

        prePersist(campaign, advertiserAccount);

        if (!CampaignUtil.canCreateBudget(campaign.getAccount())) {
            campaign.setBudget(BigDecimal.ZERO);
        }

        if (currentUserService.isExternal()) {
            campaign.setSalesManager(null);
        }

        return createInternal1(campaign);
    }

    private Long createInternal1(Campaign campaign) {
        if (!campaign.isChanged("status")) {
            campaign.setStatus(Status.ACTIVE);
        }

        campaign.setDisplayStatus(Campaign.NO_ACTIVE_GROUPS);

        auditService.audit(campaign, ActionType.CREATE);
        em.persist(campaign);

        prepareUpdateBudget(campaign);
        displayStatusService.update(campaign);

        return campaign.getId();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Campaign', #campaign.id)")
    @Validate(validation = "Campaign.update", parameters = "#campaign")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public Campaign update(Campaign campaign) {
        campaign.unregisterChange("status");
        Campaign c = updateInternal(campaign);
        executeUpdateBudget();
        return c;
    }

    private Campaign updateInternal(Campaign campaign) {
        Campaign existingCampaign = find(campaign.getId());

        prePersist(campaign, existingCampaign.getAccount());

        campaign.unregisterChange("id", "account", "commission", "creativeGroups", "campaignType");
        if (currentUserService.isExternal()) {
            campaign.unregisterChange("salesManager");
        }

        if (!campaign.isChanged("version")) {
            campaign.setVersion(existingCampaign.getVersion());
        }

        frequencyCapMerger.merge(campaign, existingCampaign);
        campaignScheduleMerger.merge(campaign, existingCampaign.getCampaignSchedules(), campaign.getCampaignSchedules(), "campaignSchedules");

        if (campaign.isChanged("excludedChannels")) {
            new CollectionMerger<>(existingCampaign.getExcludedChannels(), campaign.getExcludedChannels()).merge();
            campaign.unregisterChange("excludedChannels");
        }

        campaign = em.merge(campaign);

        if (campaign.isChanged("budget")) {
            prepareUpdateBudget(campaign);
        }

        if (campaign.isChanged("excludedChannels")) {
            for (CampaignCreativeGroup group : campaign.getCreativeGroups()) {
                targetingChannelService.addToBulkLink(group);
            }
        }
        displayStatusService.update(campaign);
        auditService.audit(campaign, ActionType.UPDATE);

        return campaign;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update",parameters = "find('Campaign', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        delete(find(id));
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undelete",parameters = "find('Campaign', #id)")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public void undelete(Long id) {
        undelete(find(id));
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.activate", parameters = "find('Campaign', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activate(Long id) {
        activate(find(id));
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.inactivate", parameters = "find('Campaign', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivate(Long id) {
        inactivate(find(id));
    }

    private void activate(Campaign campaign) {
        statusService.activate(campaign);
    }

    private void inactivate(Campaign campaign) {
        statusService.inactivate(campaign);
    }

    private void delete(Campaign campaign) {
        statusService.delete(campaign);
    }

    private void undelete(Campaign campaign) {
        statusService.undelete(campaign);
    }

    @Override
    public Campaign find(Long id) {
        Campaign res = em.find(Campaign.class, id);
        if (res == null) {
            throw new EntityNotFoundException("Campaign with id=" + id + " not found");
        }
        PersistenceUtils.initialize(res.getCampaignSchedules());
        return res;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Campaign', #id)")
    public Campaign findForEdit(Long id) {
        Campaign campaign = find(id);
        PersistenceUtils.initializeCollection(campaign.getExcludedChannels());
        return campaign;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Campaign', #id)")
    public Campaign view(Long id) {
        return find(id);
    }

    @Override
    public void refresh(Long id) {
        Campaign campaign = find(id);
        em.refresh(campaign);

        if (campaign.getInvoices() != null) {
            for (Invoice invoice : campaign.getInvoices()) {
                em.refresh(invoice);
            }
        }
    }

    private List<EntityTO> getCampaignsByAccount(Long accountId, boolean onlyWithTextGroups) {
        Account account = em.find(Account.class, accountId);
        User currUser = null;

        StringBuilder qs = new StringBuilder(
                "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) " +
                "FROM Campaign c " +
                "WHERE ");

        if (account instanceof AgencyAccount) {
            qs.append("c.account.id IN (SELECT a.id FROM AdvertiserAccount a WHERE a.agency.id = :accountId) ");

            currUser = em.find(User.class, SecurityContext.getPrincipal().getUserId());
            if (currUser.isAdvLevelAccessFlag()) {
                qs.append("AND c.account.id IN (SELECT a.id FROM User u JOIN u.advertisers a WHERE u.id = :userId) ");
            } else {
                currUser = null;
            }
        } else {
            qs.append("c.account.id = :accountId ");
        }

        if (onlyWithTextGroups) {
            qs.append("AND c.id IN (SELECT c.id FROM CampaignCreativeGroup g WHERE g.campaign.id = c.id AND g.ccgType = 'T') ");
        }

        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            qs.append("AND c.status <> 'D' ");
        }

        qs.append("ORDER BY c.name ");

        Query q = em.createQuery(qs.toString());
        q.setParameter("accountId", accountId);
        if (currUser != null) {
            q.setParameter("userId", currUser.getId());
        }

        @SuppressWarnings("unchecked")
        List<EntityTO> result = q.getResultList();
        Collections.sort(result, new StatusNameTOComparator<EntityTO>());

        return result;
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public List<EntityTO> getCampaignsByAccount(Long accountId) {
        return getCampaignsByAccount(accountId, false);
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public List<EntityTO> getTextCampaignsByAccount(Long accountId) {
        return getCampaignsByAccount(accountId, true);
    }

    @Override
    public List<Object[]> getPendingCCGTreeRawDataForAccount(AdvertiserAccount account) {
        String sqlQuery = "select campaign_id, campaign_name, campaign_display_status_id, ccg_id, ccg_name, "
                + "ccg_type, ccg_display_status_id , imps, date_start, date_end from statqueries.ccg_total_imps_by_acct(?::int, ?::int)";

        List<Object[]> rawDataResult = jdbcTemplate.withAuthContext().query(
            sqlQuery,
            new Object[] {
                    account.getId(),
                    CampaignCreativeGroup.getDisplayStatusPA_User().getId()
            },
            new RowMapper<Object[]>() {
                @Override
                public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Object[] results = new Object[10];
                    for (int i = 0; i <= 9; i++) {
                        results[i] = rs.getObject(i + 1);
                    }
                    return results;
                }
            });
        return rawDataResult;
    }

    @Override
    public CCGStatsTOList getCCGStats(Long campaignId, final LocalDate fromDate, final LocalDate toDate) {
        Campaign campaign = find(campaignId);
        final CCGType ccgType = campaign.getCampaignType() == CampaignType.TEXT ? CCGType.TEXT : CCGType.DISPLAY;
        final CCGStatsTOList result = new CCGStatsTOList();
        final boolean isExternal = currentUserService.isExternal();

        jdbcTemplate.query("select * from statqueries.ccg_stats_for_campaign(?::int, ?, ?, ?, ?)",
                new Object[] {
                    campaignId,
                    fromDate,
                    toDate,
                    userService.getMyUser().isDeletedObjectsVisible(),
                    campaign.getAccount().getAccountType().isInputRatesAndAmountsFlag()
                },
                new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long ccgId = rs.getLong("ccg_id");
                        String name = rs.getString("name");
                        Long displayStatusId = rs.getLong("display_status_id");
                        TGTType tgtType = TGTType.valueOf(rs.getString("tgt_type").charAt(0));
                        BigDecimal inventoryCost = rs.getBigDecimal("adv_amount");
                        BigDecimal targetingCost = rs.getBigDecimal("adv_amount_cmp");
                        BigDecimal creditUsed = rs.getBigDecimal("campaign_credit_used");
                        long uniqueUsers = rs.getLong("unique_users");
                        char target = rs.getString("channel_target").charAt(0);

                        ImpClickStatsTO.Builder builder = new ImpClickStatsTO.Builder()
                        .imps(rs.getLong("imps"))
                        .clicks(rs.getLong("clicks"))
                        .postClickConv(rs.getLong("click_conv"));
                        if (!rs.wasNull()) {
                            result.setShowPostClickConv(true);
                        }

                        builder.postImpConv(rs.getLong("imp_conv"));
                        if (!rs.wasNull()) {
                            result.setShowPostImpConv(true);
                        }

                        boolean isTargetViewable = false;
                        ChannelTO channelTarget = null;
                        long channelId = rs.getLong("channel_id");
                        if (!rs.wasNull()) {
                            channelTarget = new ChannelTO();
                            channelTarget.setId(channelId);
                            channelTarget.setName(rs.getString("channel_name"));
                            channelTarget.setDisplayStatus(Channel.getDisplayStatus(rs.getLong("channel_display_status_id")));

                            isTargetViewable = isExternal && rs.getBoolean("channel_visible") ||
                                advertisingChannelRestrictions.canView(AccountRole.valueOf(rs.getInt("channel_account_role_id")));
                        }

                        result.add(new CCGStatsTO(ccgId, name, displayStatusId, ccgType, tgtType, builder,
                        uniqueUsers, inventoryCost, targetingCost, creditUsed, channelTarget, ChannelTarget.valueOf(target), isTargetViewable));
                        return null;
                    }
                });

        return result;
    }

    @Override
    public CampaignStatsTO getStats(Long campaignId) {
        Campaign campaign = find(campaignId);

        final BigDecimal availableBudget = jdbcTemplate.queryForObject(
                "select * from campaign_util.get_available_budget(" + campaign.getId() + ")", BigDecimal.class);

        CampaignStatsTO result = jdbcTemplate.queryForObject("select * from statqueries.campaign_total_stats(?, ?)",
                new Object[] {
                    campaignId,
                    campaign.getAccount().getAccountType().isInputRatesAndAmountsFlag()
                },
                new int[] {
                    Types.INTEGER,
                    Types.BOOLEAN
                },
                new RowMapper<CampaignStatsTO>() {
                    @Override
                    public CampaignStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Builder builder = new Builder()
                                .availableBudget(availableBudget)
                                .imps(rs.getLong("imps"))
                                .clicks(rs.getLong("clicks"))
                                .postClickConv(rs.getLong("click_conv"))
                                .isShowPostClickConv(!rs.wasNull())
                                .postImpConv(rs.getLong("imp_conv"))
                                .isShowPostImpConv(!rs.wasNull())
                                .totalUniqueUsers(rs.getLong("total_reach"));

                        BigDecimal spentBudget = rs.getBigDecimal("adv_amount");
                        BigDecimal targetingCost = rs.getBigDecimal("adv_amount_cmp");
                        builder.spentBudget(spentBudget.add(targetingCost));
                        return builder.build();
                    }
                });

        return result;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Campaign', #campaignId)")
    public ChartStats getChartStats(Long campaignId, String xspec, final String y1spec, final String y2spec) {
        AdvertiserAccount campaignAccount = em.find(Campaign.class, campaignId).getAccount();
        TimeZone campaignTimeZone = TimeZone.getTimeZone(campaignAccount.getTimezone().getKey());
        ChartStats.ChartHelper helper = new ChartHelper(campaignTimeZone, xspec, y1spec, y2spec);

        String sql = "select adv_sdate, " + helper.getY1spec() + ", " + helper.getY2spec()
                + " from statqueries.campaign_stats_daily(?, ?, ?)";

        List<ChartStats.ChartEntry> result = jdbcTemplate.query(
                sql,
                new Object[]{
                        campaignId,
                        helper.getFromDate(),
                        helper.getToDate()
                },
                new int[]{
                        Types.INTEGER,
                        Types.DATE,
                        Types.DATE,
                },
                helper.getRowMapper()
        );
        fillChartGaps(result);
        String currencyCode = campaignAccount.getCurrency().getCurrencyCode();
        return new ChartStats(result, TimeZone.getDefault(), y1spec, y2spec, currencyCode);
    }

    private void fillChartGaps(List<ChartStats.ChartEntry> result) {
        ChartStats.ChartEntry prev = null;
        for (ListIterator<ChartStats.ChartEntry> it = result.listIterator(); it.hasNext();) {
            if (prev == null) {
                prev = it.next();
                continue;
            }
            ChartStats.ChartEntry cur = it.next();
            LocalDate d1 = new LocalDate(prev.getDate());
            LocalDate d2 = new LocalDate(cur.getDate());
            if (Days.daysBetween(d1, d2).getDays() > 1) {
                for (LocalDate date = d1; date.isBefore(d2); date = date.plusDays(1)) {
                    it.add(new ChartStats.ChartEntry(date.toDate(), 0, 0));
                }
            }
            prev = cur;
        }
    }
    @Override
    public Collection<Campaign> findRecentlyChanged(int maxCampaigns, int maxCcgsInCampaign) {
        return jdbcTemplate.withAuthContext().query(
                "select * from entityqueries.get_recently_changed_campaigns(?, ?, ?)",
                new Object[]{
                        maxCampaigns,
                        maxCcgsInCampaign,
                        userService.getMyUser().isDeletedObjectsVisible()
                },
                new RowMapper<Campaign>() {
                    @Override
                    public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long agencyAccountId = rs.getLong("agency_account_id");
                        if (rs.wasNull()) {
                            agencyAccountId = null;
                        }
                        String agencyName = rs.getString("agency_name");
                        Long agencyDisplayStatusId = rs.getLong("agency_display_status_id");
                        Long agencyFlags = rs.getLong("agency_flags");
                        String agencyStatus = rs.getString("agency_status");
                        Long advAccountId = rs.getLong("adv_account_id");
                        String advName = rs.getString("adv_name");
                        Long advDisplayStatusId = rs.getLong("adv_display_status_id");
                        Long advFlags = rs.getLong("adv_flags");
                        String advStatus = rs.getString("adv_status");
                        Long campaignId = rs.getLong("campaign_id");
                        String campaignName = rs.getString("campaign_name");
                        String campaignStatus = rs.getString("campaign_status");
                        Long campaignDisplayStatusId = rs.getLong("campaign_display_status_id");
                        Timestamp campaignVersion = rs.getTimestamp("max_version");
                        List<CampaignCreativeGroup> ccgs = convertArrayToList(rs.getArray("ccgs"));

                        AgencyAccount agency = null;
                        if (agencyAccountId != null) {
                            agency = new AgencyAccount();
                            agency.setId(agencyAccountId);
                            agency.setName(agencyName);
                            agency.setDisplayStatusId(agencyDisplayStatusId);
                            agency.setFlags(agencyFlags);
                            agency.setStatus(Status.valueOf(agencyStatus.charAt(0)));
                        }

                        AdvertiserAccount advertiser = new AdvertiserAccount();
                        advertiser.setId(advAccountId);
                        advertiser.setName(advName);
                        advertiser.setDisplayStatusId(advDisplayStatusId);
                        advertiser.setFlags(advFlags);
                        advertiser.setStatus(Status.valueOf(advStatus.charAt(0)));

                        if (agency != null) {
                            advertiser.setAgency(agency);
                        }

                        Campaign campaign = new Campaign();
                        campaign.setId(campaignId);
                        campaign.setName(campaignName);
                        campaign.setDisplayStatusId(campaignDisplayStatusId);
                        campaign.setStatus(Status.valueOf(campaignStatus.charAt(0)));
                        campaign.setVersion(campaignVersion);
                        campaign.setAccount(advertiser);

                        for (CampaignCreativeGroup ccg : ccgs) {
                            ccg.setCampaign(campaign);
                            campaign.getCreativeGroups().add(ccg);
                        }

                        return campaign;
                    }

                    private List<CampaignCreativeGroup> convertArrayToList(Array array) throws SQLException {
                        if (array == null) {
                            return Collections.emptyList();
                        }

                        return PGArray.read(array, new Converter<PGRow, CampaignCreativeGroup>() {
                            @Override
                            public CampaignCreativeGroup item(PGRow row) {
                                CampaignCreativeGroup ccg = new CampaignCreativeGroup();
                                ccg.setId(row.getLong(0));
                                ccg.setName(row.getString(1));
                                ccg.setDisplayStatusId(row.getLong(2));
                                ccg.setStatus(Status.valueOf(row.getCharacter(3)));
                                ccg.setCcgType(CCGType.valueOf(row.getCharacter(4)));
                                ccg.setVersion(row.getTimestamp(5));
                                return ccg;
                            }
                        });
                    }
                }
        );
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Campaign', #campaignId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activateGroups(Long campaignId, Collection<Long> ccgIds) {
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = groupService.find(ccgId);

            if (workflowService.getStatusWorkflow(group).isActionAvailable(StatusAction.ACTIVATE)) {
                groupService.activate(ccgId);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Campaign', #campaignId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivateGroups(Long campaignId, Collection<Long> ccgIds) {
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = groupService.find(ccgId);

            if (workflowService.getStatusWorkflow(group).isActionAvailable(StatusAction.INACTIVATE)) {
                groupService.inactivate(ccgId);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.approveChildren", parameters = "find('Campaign', #campaignId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void approveGroups(Long campaignId, Collection<Long> ccgIds) {
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = groupService.find(ccgId);

            if (group.getInheritedStatus() != Status.DELETED && workflowService.getApprovalWorkflow(group).isActionAvailable(ApprovalAction.APPROVE)) {
                groupService.approve(ccgId);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.approveChildren", parameters = "find('Campaign', #campaignId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void declineGroups(Long campaignId, Collection<Long> ccgIds, String reason) {
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = groupService.find(ccgId);

            if (group.getInheritedStatus() != Status.DELETED && workflowService.getApprovalWorkflow(group).isActionAvailable(ApprovalAction.DECLINE)) {
                groupService.decline(ccgId, reason);
            }
        }
    }

    @Override
    public boolean isEndDateCleanAllowed(Long campaignId) {
        String query = "SELECT ccg.id FROM CampaignCreativeGroup ccg " +
                " WHERE ccg.campaign.id = :campaignId " +
                    " AND ccg.deliveryPacing = 'D' " +
                    " AND bitand(ccg.flags, " + CampaignCreativeGroup.LINKED_TO_CAMPAIGN_END_DATE + ") <> 0";

        Query q = em.createQuery(query);
        q.setParameter("campaignId", campaignId);

        return q.getResultList().isEmpty();
    }

    @Override
    public void validateAll(Long accountId, TGTType tgtType, Collection<Campaign> campaigns) {
        if (campaigns.isEmpty()) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Campaign> existingCampaigns =
                em.createNamedQuery("Campaign.findUndeletedByAccountId").setParameter("accountId", accountId).getResultList();

        Map<String, Campaign> existingMap = new HashMap<>();
        for (Campaign existing : existingCampaigns) {
            existingMap.put(existing.getName(), existing);
        }

        AdvertiserAccount account = accountService.findAdvertiserAccount(accountId);

        DuplicateChecker.<Campaign>createNameDuplicateChecker()
                .check(campaigns)
                .updateUploadStatus("name");


        Map<String, User> accountUsers = createUsersByEmail(account);

        //noinspection unchecked
        Map<String, User> salesManagers = LazyMap.decorate(new HashMap(), new Transformer() {
            @Override
            public User transform(Object email) {
                return userService.findByEmail((String) email);
            }
        });

        for (Campaign campaign : campaigns) {
            UploadContext uploadStatus = UploadUtils.getUploadContext(campaign);

            Campaign existing = existingMap.get(campaign.getName());

            // update entity
            campaign.setAccount(new AdvertiserAccount(account.getId()));
            if (existing != null) {
                campaign.setId(existing.getId());
                campaign.setVersion(existing.getVersion());
                uploadStatus.mergeStatus(UploadStatus.UPDATE);
            } else {
                uploadStatus.mergeStatus(UploadStatus.NEW);
            }

            if (!CampaignUtil.canChangeBudget(account, existing) && campaign.getBudget() == null) {
                campaign.unregisterChange("budget");
            }

            // validate fields
            if (!uploadStatus.isFatal()) {
                if (UploadUtils.isLink(campaign)) {
                    UploadUtils.setErrors(campaign, validationService.validate("Campaign.link", campaign).getConstraintViolations());
                } else {

                    prepareUser(salesManagers, campaign.getSalesManager(), uploadStatus, "salesManager");
                    prepareUser(accountUsers, campaign.getSoldToUser(), uploadStatus, "soldToUser");
                    prepareUser(accountUsers, campaign.getBillToUser(), uploadStatus, "billToUser");

                    ValidationContext context = validationService.validate(
                            ValidationStrategies.exclude(uploadStatus.getWrongPaths()), "Campaign.createOrUpdate", campaign);
                    UploadUtils.setErrors(campaign, context.getConstraintViolations());
                }
            }

            if (existing != null && UploadUtils.isLink(campaign)) {
                groupService.validateAll(existing, tgtType, campaign.getCreativeGroups());
            } else {
                groupService.validateAll(campaign, tgtType, campaign.getCreativeGroups());
            }
        }
    }

    private Map<String, User> createUsersByEmail(AdvertiserAccount account) {
        Set<User> accountUsers = account.getAgency() != null ? account.getAgency().getUsers() : account.getUsers();
        HashMap<String,User> result = new HashMap<>();
        for (User accountUser : accountUsers) {
            result.put(accountUser.getEmail(), accountUser);
        }
        return result;
    }

    private void prepareUser(Map<String, User> usersByEmail, User user, UploadContext uploadStatus, String path ) {
        if (user == null) {
            return;
        }
        User existing = usersByEmail.get(user.getEmail());
        if (existing != null) {
            user.setId(existing.getId());
        } else {
            uploadStatus.addError("errors.field.invalid").withPath(path);
        }
    }

    @Override
    public Set<String> getAffectedCCGForCampaignDelivery(Long campaignId, final Collection<? extends WeekSchedule> campaignScheduleSet) {
        String sql;

        if (currentUserService.isExternal()) {
            sql = "select  new com.foros.session.campaign.CampaignScheduleTO(ccg.name, ccgs.timeFrom, ccgs.timeTo, ccg.status) from CCGSchedule ccgs join ccgs.campaignCreativeGroup ccg " +
                    " where ccg.campaign.id = :campaignId and ccg.status <> 'D'";
        } else {
            sql = "select  new com.foros.session.campaign.CampaignScheduleTO(ccg.name, ccgs.timeFrom, ccgs.timeTo, ccg.status) from CCGSchedule ccgs join ccgs.campaignCreativeGroup ccg " +
                    " where ccg.campaign.id = :campaignId ";
        }

        Query query = em.createQuery(sql);
        query.setParameter("campaignId", campaignId);

        //noinspection unchecked
        Collection<CampaignScheduleTO> ccgList = new ArrayList<CampaignScheduleTO>(query.getResultList());

        CollectionUtils.filter(ccgList, new Filter<CampaignScheduleTO>() {
            @Override
            public boolean accept(CampaignScheduleTO scheduleTO) {
                for (WeekSchedule campaignSchedule : campaignScheduleSet) {
                    if (scheduleTO.getTimeFrom() >= campaignSchedule.getTimeFrom() && scheduleTO.getTimeTo() <= campaignSchedule.getTimeTo()) {
                        return false;
                    }
                }
                return true;
            }
        });

        EntityUtils.applyStatusRules(ccgList, null, true);

        return new HashSet<>(CollectionUtils.convert(new Converter<CampaignScheduleTO, String>() {
            @Override
            public String item(CampaignScheduleTO value) {
                return value.getName();
            }
        }, ccgList));
    }

    @Override
    @Restrict(restriction = "Entity.access", parameters = "find('AdvertiserAccount', #advertiserId)")
    public List<TreeFilterElementTO> searchCampaigns(Long advertiserId, Boolean display, Boolean withDeletedGroups) {
        if (advertiserId == null) {
            return new ArrayList<>();
        }

        Object[] params = display != null ? new Object[2] : new Object[1];
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        queryString.append("  c.campaign_id id, c.name, c.status, c.display_status_id, ");
        queryString.append("  EXISTS ( ");
        queryString.append("    SELECT * FROM CampaignCreativeGroup ");
        queryString.append("    WHERE campaign_id = c.campaign_id ");
        if (Boolean.FALSE.equals(withDeletedGroups) || !userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND status <> 'D'");
        }
        queryString.append("  ) hasChildren ");
        queryString.append("FROM Campaign c ");
        queryString.append("WHERE c.account_id = ? ");
        params[0] = advertiserId;
        if (display != null) {
            queryString.append(" AND c.campaign_type = ? ");
            params[1] = display ? CampaignType.DISPLAY.getLetter() : CampaignType.TEXT.getLetter();

        }
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND c.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                queryString.toString(),
                params,
                new TreeFilterElementTOConverter(Campaign.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<TreeFilterElementTO>());
        return result;
    }

    @Override
    public List<TreeFilterElementTO> searchCampaignsBySizeType(Long advertiserId, Long sizeTypeId, Boolean withDeletedGroups) {
        if (advertiserId == null || sizeTypeId == null) {
            return new ArrayList<>();
        }

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT ");
        queryString.append("  c.campaign_id id, c.name, c.status, c.display_status_id, ");
        queryString.append("  EXISTS ( ");
        queryString.append("    SELECT * FROM CampaignCreativeGroup ");
        queryString.append("    WHERE campaign_id = c.campaign_id ");

        if ((withDeletedGroups != null && !withDeletedGroups) || !userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND status <> 'D'");
        }
        queryString.append("  ) hasChildren ");
        queryString.append("FROM Campaign c ");
        queryString.append("WHERE c.account_id = ? ");
        queryString.append("AND EXISTS (SELECT 1 FROM CampaignCreativeGroup ccg " +
                "  WHERE ccg.campaign_id = c.campaign_id AND EXISTS (SELECT 1 FROM CampaignCreative cc " +
                "      WHERE cc.ccg_id = ccg.ccg_id AND EXISTS (SELECT 1 FROM creative cr " +
                "          WHERE cr.creative_id = cc.creative_id AND EXISTS (SELECT 1 FROM CreativeSize cz " +
                "              WHERE cz.size_type_id=? AND cr.size_id = cz.size_id))))");


        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND c.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                queryString.toString(),
                new Object[]{advertiserId, sizeTypeId},
                new TreeFilterElementTOConverter(Campaign.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    private void executeUpdateBudget() {
        em.flush();
        CampaignBudgetHibernateHandler handler = getCampaignBudgetHibernateHandler();
        handler.handle();
    }

    private void prepareUpdateBudget(Campaign campaign) {
        CampaignBudgetHibernateHandler handler = getCampaignBudgetHibernateHandler();
        handler.registerCampaignId(campaign.getId());
    }

    private CampaignBudgetHibernateHandler getCampaignBudgetHibernateHandler() {
        Session session = PersistenceUtils.getHibernateSession(em);
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(session);
        CampaignBudgetHibernateHandler handler = hi.getCampaignBudgetHibernateInterceptor();
        handler.initialize(jdbcTemplate, em);
        return handler;
    }

    @Override
    public BigDecimal calculateDailyBudget(Long campaignId) {
        Campaign campaign = find(campaignId);
        switch (campaign.getDeliveryPacing()) {
            case UNRESTRICTED: {
                return null;
            }
            case FIXED: {
                return campaign.getDailyBudget();
            }
            case DYNAMIC: {
                return calculateDynamicDailyBudget(campaignId);
            }
        }
        throw new IllegalStateException("Unknown delivery pacing = " + campaign.getDeliveryPacing() + " for campaign id = " + campaignId);
    }

    private BigDecimal calculateDynamicDailyBudget(Long campaignId) {
        Campaign campaign = find(campaignId);
        BigDecimal totalBudget = campaign.getTotalBudget();
        Date startDate = campaign.getDateStart();
        Date endDate = campaign.getDateEnd();
        TimeZone timeZone = TimeZone.getTimeZone(campaign.getAccount().getTimezone().getKey());
        String accountCurrency = campaign.getAccount().getCurrency().getCurrencyCode();
        if (endDate == null) {
            return null;
        }
        Calendar c = Calendar.getInstance(timeZone);
        c.setTime(new Date());
        Date today = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();
        c.setTime(startDate);
        startDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();
        c.setTime(endDate);
        endDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();
        if (today.before(startDate)) {
            today = startDate;
        }
        if (endDate.before(today)) {
            return BigDecimal.ZERO;
        }

        long daysLeft = (endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) + 1;

        if (totalBudget == null) {
            totalBudget = BigDecimal.ZERO;
        }

        BigDecimal result = BigDecimal.ZERO;
        if (totalBudget.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal remainingBudget = totalBudget.subtract(getSpentCampaignBudget(campaignId, timeZone, today));
            int defaultFractionDigits = Currency.getInstance(accountCurrency).getDefaultFractionDigits();
            // Scaling of totalBudget is explicitly increased if necessary to provide precise division result
            if (remainingBudget.scale() < defaultFractionDigits) {
                remainingBudget = remainingBudget.setScale(defaultFractionDigits, BigDecimal.ROUND_UNNECESSARY);
            }
            result = remainingBudget.divide(BigDecimal.valueOf(daysLeft), BigDecimal.ROUND_HALF_UP);
        }
        return result;
    }

    @Override
    public BigDecimal getSpentCampaignBudget(Long campaignId, TimeZone timeZone, Date today) {

        Calendar c = Calendar.getInstance(timeZone);
        c.setTime(today);
        String sql = "select * from statqueries.campaign_spent_budget(?, ?)";
        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{campaignId, c},
                new int[]{Types.INTEGER, Types.DATE},
                BigDecimal.class
        );
        return result;
    }

    @Override
    public boolean hasNoFreqCapWarningForCampaign(Campaign campaign) {
        if (campaign.getInheritedStatus() != Status.ACTIVE) {
            return false;
        }
        return jdbcTemplate.queryForObject("select * from entityqueries.get_no_freq_cap_warning_for_campaign(?::int)",
                new Object[] { campaign.getId() }, Boolean.class);
    }

    @Override
    public boolean isBatchActionPossible(Collection<Long> ids, String action) {
        String restrinctionName = "AdvertiserEntity." + action.toLowerCase();
        Collection<Campaign> campaigns = new JpaQueryWrapper<Campaign>(em, "select c from Campaign c where c.id in :ids")
                .setPrimitiveArrayParameter("ids", ids)
                .getResultList();

        for (Campaign campaign : campaigns) {
            if (!restrictionService.isPermitted(restrinctionName, campaign)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void activateAll(Collection<Long> ids) {
        for (Long id : ids) {
            Campaign campaign = find(id);
            if (workflowService.getStatusWorkflow(campaign).isActionAvailable(StatusAction.ACTIVATE) &&
                    restrictionService.isPermitted("AdvertiserEntity.activate", campaign)) {
                activate(id);
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivateAll(Collection<Long> ids) {
        for (Long id : ids) {
            Campaign campaign = find(id);
            if (workflowService.getStatusWorkflow(campaign).isActionAvailable(StatusAction.INACTIVATE) &&
                    restrictionService.isPermitted("AdvertiserEntity.inactivate", campaign)) {
                inactivate(id);
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void deleteAll(Collection<Long> ids) {
        for (Long id : ids) {
            Campaign campaign = find(id);
            if (workflowService.getStatusWorkflow(campaign).isActionAvailable(StatusAction.DELETE) &&
                    restrictionService.isPermitted("AdvertiserEntity.delete", campaign)) {
                delete(id);
            }
        }
    }

    @Override
    public Collection<Channel> getExcludedChannels(Long campaignId) {
        List<Channel> res = new ArrayList<>(find(campaignId).getExcludedChannels());
        Collections.sort(res, new IdNameComparator());
        return res;
    }
}
