package com.foros.session.admin.accountType;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.Account;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.security.ActionType;
import com.foros.model.site.Tag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.NamedTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.IllegalWalledGardenAgencyTypeException;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.campaign.CCGEntityTO;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.session.security.AuditService;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.comparator.IdNameComparator;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import org.apache.commons.lang.ObjectUtils;

@Stateless(name = "AccountTypeService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class AccountTypeServiceBean extends BusinessServiceBean<AccountType> implements AccountTypeService {

    @EJB
    private HibernateWorkExecutorService executor;

    @EJB
    private AuditService auditService;

    @EJB
    private AccountService accountService;

    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    public AccountTypeServiceBean() {
        super(AccountType.class);
    }

    @Override
    public List<AccountType> findByRole(String roleName) {
        Query query = em.createNamedQuery("AccountType.findByRole");
        AccountRole role = AccountRole.byName(roleName);
        query.setParameter("role", role);

        @SuppressWarnings("unchecked")
        List<AccountType> result = query.getResultList();
        Collections.sort(result, new IdNameComparator());

        return result;
    }

    /**
     * Find all the AccountType for given Account Role
     *
     * @param roleName Account Role Name
     * @return existence of linked Campaign Creative Groups
     */
    @Override
    public List<NamedTO> findIndexByRole(String roleName) {
        Query query = em.createNamedQuery("AccountType.findIndexByRole");
        AccountRole role = AccountRole.byName(roleName);
        query.setParameter("role", role);

        @SuppressWarnings("unchecked")
        List<NamedTO> result = query.getResultList();
        Collections.sort(result, new IdNameComparator());

        return result;
    }

    /**
     * Find CreativeSizes for given AccountTypeId
     *
     * @param accountTypeId AccountType Id
     * @return Collection of CreativeSize for given AccountTypeId
     */
    @Override
    public List<EntityTO> findCreativeSizes(Long accountTypeId) {
        String query = "SELECT S.SIZE_ID, S.NAME, S.STATUS FROM CREATIVESIZE S INNER JOIN ACCOUNTTYPECREATIVESIZE A " +
                " ON A.SIZE_ID = S.SIZE_ID WHERE A.ACCOUNT_TYPE_ID = ?";

        Query q = em.createNativeQuery(query);

        q.setParameter(1, accountTypeId);

        @SuppressWarnings("unchecked")
        List<Object[]> rawResult = q.getResultList();

        List<EntityTO> result = new ArrayList<EntityTO>(rawResult.size());

        for (Object[] row : rawResult) {
            Long id = ((Number)row[0]).longValue();
            String name = (String) row[1];
            char status = (row[2].toString()).charAt(0);

            result.add(new EntityTO(id, name, status, "CreativeSize." + id));
        }

        return result;
    }

    @Override
    public AccountType findById(Long accountTypeId) {
        AccountType accountType = em.find(AccountType.class, accountTypeId);
        if (accountType == null) {
            throw new EntityNotFoundException("Account Type with id = "
                    + accountTypeId + " not found");
        }
        accountType.getCreativeSizes().size();
        accountType.getTemplates().size();
        return accountType;
    }

    @Override
    @Restrict(restriction = "AccountType.view")
    public AccountType view(Long accountTypeId) {
        AccountType accountType = findById(accountTypeId);
        PersistenceUtils.initializeCollection(accountType.getDeviceChannels());
        return accountType;
    }

    @Override
    public Set<DeviceChannel> getAccountDeviceChannels(Long accountTypeId) {
        AccountType accountType = findById(accountTypeId);
        PersistenceUtils.initializeCollection(accountType.getDeviceChannels());
        return accountType.getDeviceChannels();
    }

    /**
     * Indicates whether provided Account Type has linked Campaign Creative
     * Groups which use provided flag; Link chain is: CampaignCreativeGroup ->
     * Campaign -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param account
     *@param flag Account Type flag to be used in check  @return whether such Campaign Creative Groups exist
     */
    private boolean hasCampaignCreativeGroupsLinkedByFlag(AccountType entity, Account account, long flag) {
        StringBuilder queryBuilder = getCcgLinkedByQuery(entity, account);
        queryBuilder.append(" and bitand(ccg.flags, :flag) <> 0 ");
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        query.setParameter("flag", flag);
        Number linkedCCGCount = (Number) query.getSingleResult();
        return (linkedCCGCount.intValue() > 0);
    }

    /**
     * Find all Accounts based on Account Type that has linked Campaign Creative
     * Groups which use provided flag; Link chain is: CampaignCreativeGroup ->
     * Campaign -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param flag Account Type flag to be used in check
     * @return List of Accounts
     */
    @Override
    public List<CCGEntityTO> getCampaignCreativeGroupsLinkedByFlag(AccountType entity, long flag) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" select distinct ccg.CCG_ID, ccg.NAME, ccg.STATUS, ccg.CCG_TYPE ");
        queryBuilder.append(" from CAMPAIGNCREATIVEGROUP ccg ");
        queryBuilder.append("   inner join CAMPAIGN c on ccg.CAMPAIGN_ID = c.CAMPAIGN_ID ");
        queryBuilder.append("   inner join ACCOUNT a on c.ACCOUNT_ID = a.ACCOUNT_ID ");
        if (AccountRole.ADVERTISER.equals(entity.getAccountRole())) {
            queryBuilder.append(" where a.ACCOUNT_TYPE_ID = :accountTypeId ");
        } else {
            queryBuilder.append("   inner join ACCOUNT a1 on a.AGENCY_ACCOUNT_ID = a1.ACCOUNT_ID ");
            queryBuilder.append(" where a1.ACCOUNT_TYPE_ID = :accountTypeId ");
        }
        queryBuilder.append(" and (ccg.FLAGS & :flag) != 0 order by ccg.Name ");
        Query query = em.createNativeQuery(queryBuilder.toString());
        query.setParameter("accountTypeId", entity.getId());
        query.setParameter("flag", flag);
        List<Object[]> sqlResult = query.getResultList();

        return populateCCGTypeTO(sqlResult);
    }

    private List<CCGEntityTO> populateCCGTypeTO(List<Object[]> sqlResult) {
        List<CCGEntityTO> result = new ArrayList<CCGEntityTO>();
        CCGEntityTO to;
        for (Object[] o : sqlResult) {
            to = new CCGEntityTO(
                    new Long(o[0].toString()),
                    o[1].toString(),
                    o[2].toString().charAt(0),
                    o[3].toString().charAt(0));

            result.add(to);
        }
        return result;
    }

    private void prePersist(AccountType accountType, AccountRole role){
        if (role == AccountRole.ADVERTISER || role == AccountRole.PUBLISHER || role == AccountRole.AGENCY) {
            Set<CreativeSize> creativeSizes = new LinkedHashSet<CreativeSize>();

            for(CreativeSize creativeSize: accountType.getCreativeSizes()){
                creativeSizes.add(em.find(CreativeSize.class, creativeSize.getId()));
            }

            accountType.setCreativeSizes(creativeSizes);
        } else {
            accountType.setCreativeSizes(new LinkedHashSet<CreativeSize>());
        }

        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY
                || (role == AccountRole.PUBLISHER && accountType.isWdTagsFlag())) {
            Set<Template> templates = new LinkedHashSet<Template>();

            for(Template template : accountType.getTemplates()){
                templates.add(em.find(Template.class, template.getId()));
            }

            accountType.setTemplates(templates);
        } else {
            accountType.setTemplates(new LinkedHashSet<Template>());
        }

        if (role != AccountRole.PUBLISHER) {
            accountType.setShowIframeTag(null);
        }
    }

    @Override
    @Restrict(restriction = "AccountType.view")
    public List<AccountType> findAll() {
        return super.findAll();
    }

    @Override
    @Restrict(restriction = "AccountType.create")
    @Validate(validation = "AccountType.create", parameters = "#accountType")
    @Interceptors({CaptureChangesInterceptor.class})
    public void create(AccountType accountType) {
        prePersist(accountType, accountType.getAccountRole());
        AccountRole accountRole = accountType.getAccountRole();
        if(!(AccountRole.ADVERTISER.equals(accountRole) || AccountRole.AGENCY.equals(accountRole))) {
            accountType.getCcgTypes().clear();
        }
        auditService.audit(accountType, ActionType.CREATE);
        super.create(accountType);
    }

    @Override
    @Restrict(restriction = "AccountType.update")
    @Validate(validation = "AccountType.update", parameters = "#accountType")
    @Interceptors({CaptureChangesInterceptor.class})
    public AccountType update(AccountType accountType) {
        EntityUtils.checkEntitiesIds(Template.class, accountType.getTemplates(), em);
        EntityUtils.checkEntitiesIds(CreativeSize.class, accountType.getCreativeSizes(), em);
        AccountType existingAccountType = em.find(AccountType.class, accountType.getId());

        Set<AccountTypeCCGType> newCCGTypes = accountType.getCcgTypes();
        Set<AccountTypeCCGType> existingCCGTypes = existingAccountType.getCcgTypes();

        // delete removed AccountTypeCCGType and identify newly added AccountTypeCCGType
        for (Iterator<AccountTypeCCGType> exitIt = existingCCGTypes.iterator(); exitIt.hasNext();) {
            AccountTypeCCGType existingCCGType = exitIt.next();
            boolean found = false;
            for (Iterator<AccountTypeCCGType> newIt = newCCGTypes.iterator(); newIt.hasNext();) {
                AccountTypeCCGType newCCGType = newIt.next();
                if (ObjectUtils.equals(newCCGType.getCcgType(), existingCCGType.getCcgType())
                        && ObjectUtils.equals(newCCGType.getTgtType(), existingCCGType.getTgtType())
                        &&ObjectUtils.equals(newCCGType.getRateType(), existingCCGType.getRateType())) {
                    found = true;
                    newIt.remove();
                    break;
                }
            }
            if ( !found ) {
                em.remove(existingCCGType);
                exitIt.remove();
            }
        }

        // OUI-24950 generate all uncompleted invoices if flags were changed
        if (existingAccountType.isPerCampaignInvoicingFlag() != accountType.isPerCampaignInvoicingFlag() ||
            existingAccountType.isInvoicingFlag() != accountType.isInvoicingFlag()) {
            for (EntityTO accountTO : getAccountLinkedByAccountType(accountType)) {
                Account account = accountService.find(accountTO.getId());
                advertisingFinanceService.generateInvoicesByAccount(account);
                accountService.updateDisplayStatus(account, true);
            }
        }

        existingCCGTypes.addAll(newCCGTypes);
        accountType.setCcgTypes(new LinkedHashSet<AccountTypeCCGType>(existingCCGTypes));
        prePersist(accountType, existingAccountType.getAccountRole());
        logChecksUpdate(accountType, existingAccountType);
        accountType = super.update(accountType);
        auditService.audit(accountType, ActionType.UPDATE);
        return accountType;
    }

    private void logChecksUpdate(AccountType accountType, AccountType existingAccountType) {
        final Long accountTypeId = accountType.getId();
        final Long campaignFirstCheck = accountType.isCampaignCheck() && !existingAccountType.isCampaignCheck() ?
                accountType.getCampaignFirstCheck().getValueInSeconds() : null;
        final Long channelFirstCheck = accountType.isChannelCheck() && !existingAccountType.isChannelCheck() ?
                accountType.getChannelFirstCheck().getValueInSeconds() : null;

        jdbcTemplate.execute(
                "select accounttype.bulk_log_checks(?::int, ?::bigint, ?::bigint)",
                accountTypeId,
                campaignFirstCheck,
                channelFirstCheck
        );
        jdbcTemplate.scheduleEviction();
    }

    @Override
    public List<EntityTO> getTagsLinkedByInventoryEstimationFlag(Long accountTypeId) {
        return em.createQuery(" SELECT DISTINCT NEW com.foros.session.EntityTO(t.id, t.name, t.status) " +
                " FROM Tag t " +
                " WHERE t.site.account.accountType.id = :id AND bitand(t.flags, :flags) <> 0 " +
                " ORDER BY t.name ", EntityTO.class)
                .setParameter("id", accountTypeId)
                .setParameter("flags", Tag.INVENTORY_ESTIMATION_FLAG)
                .getResultList();
    }

    /**
     * Determines whether provided AccountType has tags linked by inventory
     * estimation flag; link chain is: Tag -> Site -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param account
     * @return whether such Tags exist
     */
    private boolean hasTagsLinkedByInventoryEstimationFlag(AccountType entity, Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT COUNT(t.id) ");
        queryBuilder.append(" FROM Tag t ");
        queryBuilder.append(" WHERE ");
        if (account != null) {
            queryBuilder.append(" t.site.account.id = :accountId ");
        } else {
            queryBuilder.append(" t.site.account.accountType.id = :accountTypeId ");
        }
        queryBuilder.append(" AND bitand(t.flags, :bitNumber) <> 0 ");
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        query.setParameter("bitNumber", Tag.INVENTORY_ESTIMATION_FLAG);
        Number linkedTagsCount = (Number) query.getSingleResult();
        return (linkedTagsCount.intValue() > 0);
    }

    private StringBuilder getSitesLinkedByQuery(Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" select count(*) ");
        queryBuilder.append(" from Site s ");
        queryBuilder.append(" where ");
        if (account != null) {
            queryBuilder.append(" s.account.id = :accountId ");
        } else {
            queryBuilder.append(" s.account.accountType.id = :accountTypeId ");
        }
        return queryBuilder;
    }

    /**
     * Determines whether provided AccountType or Account has linked Campaign Creative
     * Groups which are of text type; Link chain is: CampaignCreativeGroup ->
     * Campaign -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param account
     * @return whether referring Text Campaign Creative Groups exist
     */
    private boolean hasLinkedTextCampaignCreativeGroups(AccountType entity, Account account) {
        StringBuilder queryBuilder = getCcgLinkedByQuery(entity, account);
        queryBuilder.append(" AND ccg.ccgType = :ccgType ");
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        query.setParameter("ccgType", CCGType.TEXT.getLetter());
        Number linkedCCGCount = (Number)query.getSingleResult();
        return (linkedCCGCount.intValue() > 0);
    }

    private void applyLinkedByParams(AccountType entity, Account account, Query query) {
        if (account != null) {
            query.setParameter("accountId", account.getId());
        } else {
            query.setParameter("accountTypeId", entity.getId());
        }
    }

    private StringBuilder getCcgLinkedByQuery(AccountType entity, Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT count(ccg.id) ");
        queryBuilder.append(" FROM CampaignCreativeGroup ccg ");
        queryBuilder.append(" WHERE ");
        if (account != null) {
            queryBuilder.append(getCcgAccountPath(entity.getAccountRole())).append(".id = :accountId");
        } else {
            queryBuilder.append(getCcgAccountPath(entity.getAccountRole())).append(".accountType.id = :accountTypeId");
        }
        return queryBuilder;
    }

    private String getCcgAccountPath(AccountRole accountRole) {
        String ccgAccountPath;
        if (AccountRole.ADVERTISER.equals(accountRole) ){
            ccgAccountPath = "ccg.campaign.account";
        } else {
            ccgAccountPath = "ccg.campaign.account.agency";
        }
        return ccgAccountPath;
    }

    @Override
    public List<CCGEntityTO> getLinkedTextCampaignCreativeGroups(AccountType entity){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT DISTINCT NEW com.foros.session.campaign.CCGEntityTO( ccg.id, ccg.name, ccg.status, ccg.ccgType) ");
        queryBuilder.append(" FROM CampaignCreativeGroup ccg ");
        if (AccountRole.ADVERTISER.equals(entity.getAccountRole())) {
            queryBuilder.append(" WHERE ccg.campaign.account.accountType.id = :accountTypeId");
        } else {
            queryBuilder.append(" WHERE ccg.campaign.account.agency.accountType.id = :accountTypeId");
        }
        queryBuilder.append("     AND ccg.ccgType = :ccgType ");
        queryBuilder.append(" ORDER BY ccg.name ");

        Query query = em.createQuery(queryBuilder.toString());
        query.setParameter("accountTypeId", entity.getId());
        query.setParameter("ccgType", CCGType.TEXT.getLetter());
        List<CCGEntityTO> sqlResult = query.getResultList();

        return sqlResult;
    }

    /**
     * Determines if provided AccountType has linked Sites which use Creative
     * Exclusions; link chain is: Site -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param account
     * @return whether such Sites exist
     */
     private boolean hasSitesLinkedByAdvExclusionFlag(AccountType entity, Account account) {
        StringBuilder queryBuilder = getSitesLinkedByQuery(account);
        queryBuilder.append(" and size(s.creativeApprovals) > 0");
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        Number count = (Number) query.getSingleResult();

        if (count.intValue() == 0) {
            queryBuilder = getSitesLinkedByQuery(account);
            queryBuilder.append(" and size(s.categoryExclusions) > 0");
            query = em.createQuery(queryBuilder.toString());
            applyLinkedByParams(entity, account, query);
            count = (Number) query.getSingleResult();
        }

        return (count.intValue() > 0);
    }

    @Override
    public List<EntityTO> getSitesLinkedByAdvExclusionFlag(Long accountTypeId) {
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append(" SELECT NEW com.foros.session.EntityTO( s.id, s.name, s.status) from Site s where s.account.accountType.id = :accountTypeId and s.id in (select sca.id.siteId from SiteCreativeApproval sca)");
        Query qu = em.createQuery(querybuilder.toString());
        qu.setParameter("accountTypeId", accountTypeId);
        List<EntityTO> resultList = qu.getResultList();

        querybuilder = new StringBuilder();
        querybuilder.append(" SELECT DISTINCT NEW com.foros.session.EntityTO( s.id, s.name, s.status) ");
        querybuilder.append(" FROM Site s, IN (s.categoryExclusions) scce ");
        querybuilder.append(" WHERE s.account.accountType.id = :accountTypeId");
        Query qu1 = em.createQuery(querybuilder.toString());
        qu1.setParameter("accountTypeId", accountTypeId);
        List<EntityTO> resultList1 = qu1.getResultList();

        resultList.removeAll(resultList1);
        resultList.addAll(resultList1);

        Collections.sort(resultList, new IdNameComparator());

        return resultList;
    }

    /**
     * Determines if provided AccountType has linked Sites which have WD Tags
     * link chain is: Site -> Account -> AccountType
     *
     * @param entity Account Type to run check on
     * @param account
     * @return whether such Sites exist
     */
    private boolean hasSitesLinkedByWDTagsFlag(AccountType entity, Account account) {
        StringBuilder queryBuilder = getSitesLinkedByQuery(account);
        queryBuilder.append(" and size(s.wdTags) > 0");
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        Number count = (Number) query.getSingleResult();

        return (count.intValue() > 0);
    }

    @Override
    public List<EntityTO> getSiteListForWDTagsFlag(Long accountTypeId) {
        Query query = em.createQuery("select distinct new com.foros.session.EntityTO(s.id, s.name, s.status) from WDTag wdtag JOIN wdtag.site s where s.account.accountType.id = :accountTypeId");
        query.setParameter("accountTypeId", accountTypeId);

        return query.getResultList();
    }

    @Override
    public List<EntityTO> getTagsLinkedByAdvExclusionFlag(Long accountTypeId) {
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append(" SELECT DISTINCT NEW com.foros.session.EntityTO( tcce.tag.id, tcce.tag.name, tcce.tag.status) ");
        querybuilder.append(" FROM TagsCreativeCategoryExclusion tcce ");
        querybuilder.append(" WHERE tcce.tag.site.account.accountType.id =:accountTypeId ");
        Query qu = em.createQuery(querybuilder.toString());
        qu.setParameter("accountTypeId", accountTypeId);
        List<EntityTO> resultList = qu.getResultList();

        Collections.sort(resultList, new IdNameComparator());

        return resultList;
    }

    private boolean hasTagsLinkedByAdvExclusionFlag(AccountType entity, Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT count(tcce.tag.id) ");
        queryBuilder.append(" FROM TagsCreativeCategoryExclusion tcce ");
        if (account != null) {
            queryBuilder.append(" WHERE tcce.tag.site.account.id = :accountId");
        } else {
            queryBuilder.append(" WHERE tcce.tag.site.account.accountType.id = :accountTypeId");
        }
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        Number count = (Number)query.getSingleResult();
        return (count.intValue() > 0);
    }

    private boolean hasSitesLinkedByFrequencyCapsFlag(AccountType entity, Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT count(s.id) ");
        queryBuilder.append(" FROM Site s ");
        queryBuilder.append(" WHERE (s.frequencyCap is not null OR s.noAdsTimeout <> 0) ");
        if (account != null) {
            queryBuilder.append(" AND s.account.id = :accountId");
        } else {
            queryBuilder.append(" AND s.account.accountType.id = :accountTypeId");
        }
        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, account, query);
        Number linkedSitesCount = (Number)query.getSingleResult();
        return (linkedSitesCount.intValue() > 0);
    }

    @Override
    public List<EntityTO> getSitesLinkedByFrequencyCapsFlag(Long accountTypeId) {
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append(" SELECT DISTINCT NEW com.foros.session.EntityTO(s.id, s.name, s.status) ");
        querybuilder.append(" FROM Site s ");
        querybuilder.append(" WHERE s.account.accountType.id =:accountTypeId ");
        querybuilder.append("   AND ( s.noAdsTimeout <> 0 ");
        querybuilder.append("   OR s.frequencyCap IS NOT NULL) ");
        querybuilder.append(" ORDER BY s.name ");
        Query qu = em.createQuery(querybuilder.toString());
        qu.setParameter("accountTypeId", accountTypeId);
        List<EntityTO> resultList = qu.getResultList();

        return  resultList;
    }

    private String getAccountPath(AccountType entity) {
        String accountPath;
        if (AccountRole.ADVERTISER.equals(entity.getAccountRole())) {
            accountPath = "c.account";
        } else {
            accountPath = "c.account.agency";
        }
        return accountPath;
    }

    /**
     * Checks for existence of Creative Groups linked with provided
     * Account Type by provided rate type and returns count of such records
     *
     * @param entity Account Type to look for
     * @param rateType rate type to look for
     * @return List for linked CampaignCreativeGroup
     */
    @Override
    public List<CCGEntityTO> getCCGRateTypeListLinkedToAccountType(AccountType entity, RateType rateType, CCGType ccgType, TGTType tgtType) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT NEW com.foros.session.campaign.CCGEntityTO( ccg.id, ccg.name, ccg.status, ccg.ccgType) ");
        queryBuilder.append(" FROM CampaignCreativeGroup ccg ");
        queryBuilder.append(" WHERE ccg.ccgRate.rateType = :rateType ");
        if (AccountRole.ADVERTISER.equals(entity.getAccountRole()) ){
            queryBuilder.append("   AND ccg.campaign.account.accountType.id = :accountTypeId ");
        } else {
            queryBuilder.append("   AND ccg.campaign.account.agency.accountType.id = :accountTypeId ");
        }
        queryBuilder.append("   AND ccg.ccgType = :ccgType ");
        queryBuilder.append("   AND ccg.tgtType = :tgtType ");
        queryBuilder.append(" ORDER BY ccg.name ");
        Query query = em.createQuery(queryBuilder.toString());
        query.setParameter("rateType", rateType);
        query.setParameter("accountTypeId", entity.getId());
        query.setParameter("ccgType", ccgType.getLetter());
        query.setParameter("tgtType", tgtType.getLetter());
        List<CCGEntityTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<EntityTO> getDisplayCreativesLinkedToAccountType(AccountType entity) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) ");
        queryBuilder.append("FROM Creative c WHERE ");
        queryBuilder.append(getAccountPath(entity)).append(".accountType.id = :accountTypeId ");
        queryBuilder.append("AND c.size.defaultName <> :textSize AND c.template.defaultName <> :textTemplate");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, null, query);
        query.setParameter("textSize", CreativeSize.TEXT_SIZE);
        query.setParameter("textTemplate", CreativeTemplate.TEXT_TEMPLATE);

        @SuppressWarnings("unchecked")
        List<EntityTO> resultList = query.getResultList();

        return resultList;
    }

    @Override
    public List<EntityTO> getDisplayCampaignsLinkedToAccountType(AccountType entity) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) ");
        queryBuilder.append("FROM Campaign c WHERE ");
        queryBuilder.append(getAccountPath(entity)).append(".accountType.id = :accountTypeId ");
        queryBuilder.append("AND c.campaignType = :displayCampaignType");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, null, query);
        query.setParameter("displayCampaignType", CampaignType.DISPLAY);

        @SuppressWarnings("unchecked")
        List<EntityTO> resultList = query.getResultList();

        return resultList;
    }

    @Override
    public List<EntityTO> getTextCampaignsLinkedToAccountType(AccountType entity) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) ");
        queryBuilder.append("FROM Campaign c WHERE ");
        queryBuilder.append(getAccountPath(entity)).append(".accountType.id = :accountTypeId ");
        queryBuilder.append("AND c.campaignType = :textCampaignType");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(entity, null, query);
        query.setParameter("textCampaignType", CampaignType.TEXT);

        @SuppressWarnings("unchecked")
        List<EntityTO> resultList = query.getResultList();

        return resultList;
    }

    /**
     * Indicates whether provided Account Type or Account has linked Campaign Creative
     * Groups which use provided rateType; Link chain is: CampaignCreativeGroup ->
     * Campaign -> Account [-> AccountType]
     *
     * @param accountType Account Type to run check on
     * @param account Account to check
     * @param rateType Rate Type to be used in check
     *
     * @return whether such Campaign Creative Groups exist
     */
    private boolean hasCampaignCreativeGroupsLinkedByRateType(AccountType accountType, Account account, RateType rateType, CCGType ccgType, TGTType tgtType) {

        StringBuilder queryBuilder = getCcgLinkedByQuery(accountType, account);
        queryBuilder.append(" and ccg.ccgRate.rateType = :rateType ");
        queryBuilder.append(" and ccg.ccgType = :ccgType ");
        queryBuilder.append(" and ccg.tgtType = :tgtType ");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(accountType, account, query);
        query.setParameter("rateType", rateType);
        query.setParameter("ccgType", ccgType.getLetter());
        query.setParameter("tgtType", tgtType.getLetter());
        Number linkedCCGCount = (Number)query.getSingleResult();
        return (linkedCCGCount.intValue() > 0);
    }

    private boolean hasDisplayCreatives(AccountType accountType, Account account) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(DISTINCT c) FROM Creative c WHERE ");

        if (account != null) {
            queryBuilder.append(getAccountPath(accountType)).append(".id = :accountId ");
        } else {
            queryBuilder.append(getAccountPath(accountType)).append(".accountType.id = :accountTypeId ");
        }

        queryBuilder.append(" AND c.size.defaultName <> :textSize AND c.template.defaultName <> :textTemplate");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(accountType, account, query);
        query.setParameter("textSize", CreativeSize.TEXT_SIZE);
        query.setParameter("textTemplate", CreativeTemplate.TEXT_TEMPLATE);

        Number creativesCount = (Number) query.getSingleResult();

        return creativesCount.intValue() > 0;
    }

    private boolean hasCampaigns(AccountType accountType, Account account, CampaignType campaignType) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(DISTINCT c) FROM Campaign c WHERE ");

        if (account != null) {
            queryBuilder.append(getAccountPath(accountType)).append(".id = :accountId ");
        } else {
            queryBuilder.append(getAccountPath(accountType)).append(".accountType.id = :accountTypeId ");
        }

        queryBuilder.append(" AND c.campaignType = :campaignType");

        Query query = em.createQuery(queryBuilder.toString());
        applyLinkedByParams(accountType, account, query);
        query.setParameter("campaignType", campaignType);

        Number campaignsCount = (Number) query.getSingleResult();

        return campaignsCount.intValue() > 0;
    }

    /**
     * Checks for existence of Accounts linked with provided Account Type.
     * @param entity Account Type to look for
     * @return  true if entity is linked to any Account
     */
    @Override
    public boolean hasAccountLinkedByAccountType(AccountType entity) {
        Query query = em.createNamedQuery("Account.findCountByAccountType");
        query.setParameter("id", entity.getId());
        Long count = (Long) query.getSingleResult();
        return (count != null && count > 0);
    }

    /**
     * Checks for existence of Accounts linked with provided Account Type,
     * and returns list of linked Accounts
     * @param entity Account Type to look for
     * @return  List List of Accounts
     */
    @Override
    public List<EntityTO> getAccountLinkedByAccountType(AccountType entity) {
        Query query = em.createNamedQuery("Account.findAccountByAccountType");
        query.setParameter("id", entity.getId());
        List<EntityTO> resultList =  query.getResultList();
        return resultList;
    }

    @Override
    public AccountTypeDisabledFields getAccountTypeChangesCheck(AccountType at) {
        if (at.getId() == null) {
            return new AccountTypeDisabledFieldsTO();
        }

        if (at.getAccountRole() == null) {
            throw new BusinessException("Can't do calculation for null account type");
        }

        return new AccountTypeDisabledFieldsTO(new DisabledFieldsSource(at));
    }

    @Override
    public boolean checkAccountCanMoved(Account account, AccountType from, AccountType to) {
        List<String> wrongFields = new LinkedList<String>();

        try {
            checkFieldChanges(from, to, new DisabledFieldsSource(from, account));
        } catch (IllegalAccountTypeChangeException e) {
            wrongFields.addAll(e.getFields());
        }

        try {
            checkWalledGardenChange(account, to);
        } catch (IllegalWalledGardenAgencyTypeException e) {
            wrongFields.add("illegal move");
        }

        return wrongFields.isEmpty();
    }

    @Override
    public boolean hasSitesLinkedByExclusionApproval(AccountType accountType){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" select count(scce.site.id)");
        queryBuilder.append(" from SiteCreativeCategoryExclusion scce ");
        queryBuilder.append(" where scce.site.account.accountType.id = :id");
        queryBuilder.append(" and scce.approval = 'P'");
        Query query = em.createQuery(queryBuilder.toString());
        query.setParameter("id", accountType.getId());

        return ((Number)query.getSingleResult()).intValue() > 0;
    }

    @Override
    public void validateFieldChanges(AccountType existingAccountType, AccountType accountType) {
        // consistency checks
        checkFieldChanges(existingAccountType, accountType, new DisabledFieldsSource(existingAccountType));
        checkWalledGardenChange(null, accountType);
    }

    private void checkFieldChanges(AccountType existing, AccountType edited, AccountTypeDisabledFields changesCheck) {

        Set<String> wrongFields = new LinkedHashSet<String>();

        if (!ObjectUtils.equals(existing.isPublisherInventoryEstimationFlag(), edited.isPublisherInventoryEstimationFlag())) {
            checkChange("AccountType.inventoryEstimationFlag", changesCheck.isPublisherInventoryEstimationFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isAllowTextAdvertisingFlag(), edited.isAllowTextAdvertisingFlag())) {
            checkChange("account.textAdserving", changesCheck.isAllowTextAdvertisingFlagDisabled(), wrongFields);
        }

        if (AccountRole.PUBLISHER == edited.getAccountRole()) {
            if (!ObjectUtils.equals(existing.isAdvExclusionSiteFlag(), edited.isAdvExclusionSiteFlag())) {
                checkChange("AccountType.advertiserExclusionFlag", changesCheck.isAdvExclusionSiteFlagDisabled(), wrongFields);
            }
            if (!ObjectUtils.equals(existing.isAdvExclusionSiteTagFlag(), edited.isAdvExclusionSiteTagFlag())) {
                checkChange("AccountType.advertiserExclusionFlag", changesCheck.isAdvExclusionSiteTagFlagDisabled(), wrongFields);
            }
            if(!ObjectUtils.equals(existing.getAdvExclusionApproval(), edited.getAdvExclusionApproval())){
                checkChange("AccountType.advExclusionApprovalFlag", changesCheck.isAllowAdvExclusionApprovalDisabled(), wrongFields);
            }
        }

        if (!ObjectUtils.equals(existing.isCPAFlag(CCGType.DISPLAY), edited.isCPAFlag(CCGType.DISPLAY))) {
            checkChange("AccountType.CPA" , changesCheck.isDisplayCPADisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isCPCFlag(CCGType.DISPLAY), edited.isCPCFlag(CCGType.DISPLAY))) {
            checkChange("AccountType.CPC" , changesCheck.isDisplayCPCDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isCPMFlag(CCGType.DISPLAY), edited.isCPMFlag(CCGType.DISPLAY))) {
            checkChange("AccountType.CPM" , changesCheck.isDisplayCPMDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isFreqCapsFlag(), edited.isFreqCapsFlag())) {
            checkChange("AccountType.freqCaps", changesCheck.isFreqCapsFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isAllowTextKeywordAdvertisingFlag(), edited.isAllowTextKeywordAdvertisingFlag())) {
            checkChange("AccountType.keywordTargeted", changesCheck.isAllowTextKeywordAdvertisingFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isSiteTargetingFlag(), edited.isSiteTargetingFlag())) {
            checkChange("AccountType.siteTargetingFlag", changesCheck.isSiteTargetingFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isCPAFlag(CCGType.TEXT), edited.isCPAFlag(CCGType.TEXT))) {
            checkChange("AccountType.CPA" , changesCheck.isTextCPADisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isCPCFlag(CCGType.TEXT), edited.isCPCFlag(CCGType.TEXT))) {
            checkChange("AccountType.CPC" , changesCheck.isTextCPCDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isCPMFlag(CCGType.TEXT), edited.isCPMFlag(CCGType.TEXT))) {
            checkChange("AccountType.CPM" , changesCheck.isTextCPMDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isWdTagsFlag(), edited.isWdTagsFlag())) {
            checkChange("AccountType.wdTagsFlag", changesCheck.isWdTagsFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isAgencyFinancialFieldsFlag(), edited.isAgencyFinancialFieldsFlag())) {
            checkChange("AccountType.financialFieldsFlag", changesCheck.isAgencyFinancialFieldsFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isPerCampaignInvoicingFlag(), edited.isPerCampaignInvoicingFlag())) {
            checkChange("AccountType.invoicingFlag", changesCheck.isPerCampaignInvoicingFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isInputRatesAndAmountsFlag(), edited.isInputRatesAndAmountsFlag())) {
            checkChange("AccountType.inputRatesAndAmountsFlag", changesCheck.isInputRatesAndAmountsFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isInvoiceCommissionFlag(), edited.isInvoiceCommissionFlag())) {
            checkChange("AccountType.commissionFlag", changesCheck.isInvoiceCommissionFlagDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.getIoManagement(), edited.getIoManagement())) {
            checkChange("AccountType.ioManagement", changesCheck.isIOManagementDisabled(), wrongFields);
        }
        if (!ObjectUtils.equals(existing.isBillingModelFlag(), edited.isBillingModelFlag())) {
            checkChange("AccountType.billingModelFlag", changesCheck.isBillingModelFlagDisabled(), wrongFields);
        }
        if (wrongFields.size() > 0){
            throw new IllegalAccountTypeChangeException("Some fields have illegal changes", wrongFields);
        }

        if (((changesCheck.isDisplayCreativesExist() || changesCheck.isDisplayCampaignsExist()) &&
                !edited.isCPAFlag(CCGType.DISPLAY) && !edited.isCPCFlag(CCGType.DISPLAY) && !edited.isCPMFlag(CCGType.DISPLAY))
                || ((changesCheck.isTextCampaignsExist()) &&
                !edited.isAllowTextKeywordAdvertisingFlag() && !edited.isCPCFlag(CCGType.TEXT) && !edited.isCPCFlag(CCGType.TEXT) && !edited.isCPMFlag(CCGType.TEXT))) {
            throw new IllegalAccountTypeChangeException("All of the Display CCG rate types cannot be disabled", "AccountType.rateTypes");
        }
    }

    private void checkChange(String field, boolean allowed, Set<String> wrongFields) {
        if (allowed) {
            wrongFields.add(field);
        }
    }

    private void checkWalledGardenChange(Account account, AccountType accountType) {
        if (accountType.getAccountRole() == AccountRole.AGENCY) {
            if ((account != null ? walledGardenService.isAgencyWalledGarden(account.getId()) : walledGardenService.isAgencyAccountTypeWalledGarden(accountType.getId()))
                    && !walledGardenService.validateAgencyAccountType(accountType)) {
                throw new IllegalWalledGardenAgencyTypeException();
            }
        }
    }


    private class DisabledFieldsSource implements AccountTypeDisabledFields {
        private AccountType accountType;
        private Account account;
        private Boolean accountTypeLinked;

        private DisabledFieldsSource(AccountType accountType) {
            this(accountType, null);
        }

        private DisabledFieldsSource(AccountType accountType, Account account) {
            this.accountType = accountType;
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        public AccountType getAccountType() {
            return accountType;
        }

        @Override
        public boolean isPublisherInventoryEstimationFlagDisabled() {
            return isNotPublisher() || (accountType.isPublisherInventoryEstimationFlag() && hasTagsLinkedByInventoryEstimationFlag(accountType, account));
        }

        @Override
        public boolean isAdvExclusionSiteFlagDisabled() {
            return isNotPublisher() || (accountType.isAdvExclusionSiteFlag() && hasSitesLinkedByAdvExclusionFlag(accountType, account));
        }

        @Override
        public boolean isAdvExclusionSiteTagFlagDisabled() {
            return isNotPublisher() || (accountType.isAdvExclusionSiteTagFlag() && hasTagsLinkedByAdvExclusionFlag(accountType, account));
        }

        @Override
        public boolean isFreqCapsFlagDisabled() {
            return isNotPublisher() || (accountType.isFreqCapsFlag() && hasSitesLinkedByFrequencyCapsFlag(accountType, account));
        }

        @Override
        public boolean isWdTagsFlagDisabled() {
            return isNotPublisher() || (accountType.isWdTagsFlag() && hasSitesLinkedByWDTagsFlag(accountType, account));
        }

        @Override
        public boolean isAllowTextAdvertisingFlagDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isAllowTextAdvertisingFlag()
                && hasLinkedTextCampaignCreativeGroups(accountType, account));
        }

        @Override
        public boolean isDisplayCPADisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPAFlag(CCGType.DISPLAY)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPA, CCGType.DISPLAY, TGTType.CHANNEL));
        }

        @Override
        public boolean isDisplayCPCDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPCFlag(CCGType.DISPLAY)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPC, CCGType.DISPLAY, TGTType.CHANNEL));
        }

        @Override
        public boolean isDisplayCPMDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPMFlag(CCGType.DISPLAY)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPM, CCGType.DISPLAY, TGTType.CHANNEL));
        }

        @Override
        public boolean isDisplayCreativesExist() {
            return !isNotAdvertiserOrAgency() && hasDisplayCreatives(accountType, account);
        }

        @Override
        public boolean isDisplayCampaignsExist() {
            return !isNotAdvertiserOrAgency() && hasCampaigns(accountType, account, CampaignType.DISPLAY);
        }

        @Override
        public boolean isTextCampaignsExist() {
            return !isNotAdvertiserOrAgency() && hasCampaigns(accountType, account, CampaignType.TEXT);
        }

        @Override
        public boolean isAllowTextKeywordAdvertisingFlagDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isAllowTextKeywordAdvertisingFlag()
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPC, CCGType.TEXT, TGTType.KEYWORD));
        }

        @Override
        public boolean isSiteTargetingFlagDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isSiteTargetingFlag()
                && hasCampaignCreativeGroupsLinkedByFlag(accountType, account, CampaignCreativeGroup.INCLUDE_SPECIFIC_PUBL));
        }

        @Override
        public boolean isTextCPADisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPAFlag(CCGType.TEXT)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPA, CCGType.TEXT, TGTType.CHANNEL));
        }

        @Override
        public boolean isTextCPCDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPCFlag(CCGType.TEXT)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPC, CCGType.TEXT, TGTType.CHANNEL));
        }

        @Override
        public boolean isTextCPMDisabled() {
            return isNotAdvertiserOrAgency() || (accountType.isCPMFlag(CCGType.TEXT)
                && hasCampaignCreativeGroupsLinkedByRateType(accountType, account, RateType.CPM, CCGType.TEXT, TGTType.CHANNEL));
        }

        @Override
        public boolean isAgencyFinancialFieldsFlagDisabled() {
            return isNotAgency() || isAccountTypeLinked();
        }

        @Override
        public boolean isPerCampaignInvoicingFlagDisabled() {
            return isNotAdvertiserOrAgency();
        }

        @Override
        public boolean isInputRatesAndAmountsFlagDisabled() {
            return isNotAdvertiserOrAgency();
        }

        @Override
        public boolean isInvoiceCommissionFlagDisabled() {
            return isNotAdvertiserOrAgency() || isAccountTypeLinked();
        }

        @Override
        public boolean isAllowAdvExclusionApprovalDisabled() {
            return accountType.isAdvExclusionFlag() && hasSitesLinkedByExclusionApproval(accountType);
        }

        @Override
        public boolean isIOManagementDisabled() {
            Boolean ioManagement = accountType.getIoManagement();
            return ioManagement == null || ioManagement;
        }

        @Override
        public boolean isBillingModelFlagDisabled() {
            return isNotAdvertiserOrAgency();
        }

        public boolean isAccountTypeLinked() {
            if (accountTypeLinked == null) {
                accountTypeLinked = account != null || hasAccountLinkedByAccountType(accountType);
            }
            return accountTypeLinked;
        }

        private boolean isNotPublisher() {
            return accountType.getAccountRole() != AccountRole.PUBLISHER;
        }

        private boolean isNotAdvertiserOrAgency() {
            return isNotAdvertiser() && isNotAgency();
        }

        private boolean isNotAdvertiser() {
            return accountType.getAccountRole() != AccountRole.ADVERTISER;
        }

        private boolean isNotAgency() {
            return accountType.getAccountRole() != AccountRole.AGENCY;
        }
    }
}
