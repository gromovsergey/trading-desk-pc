package com.foros.session.admin.walledGarden;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.RateType;
import com.foros.model.security.AccountType;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.security.ActionType;
import com.foros.model.site.Tag;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessServiceBean;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.CacheService;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.session.security.AccountTO;
import com.foros.session.security.AccountTOConverter;
import com.foros.session.security.AuditService;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.IdNameComparator;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Stateless(name = "WalledGardenService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class WalledGardenServiceBean extends BusinessServiceBean<WalledGarden> implements WalledGardenService {
    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private CacheService cacheService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AuditService auditService;

    public WalledGardenServiceBean() {
        super(WalledGarden.class);
    }
    
    @Override
    public WalledGarden findById(Long id) {
        WalledGarden result = super.findById(id);
        
        // initialize lazy fields
        result.getAgency().getName();
        result.getPublisher().getName();
        return result;
    }
    
    @Override
    @Restrict(restriction = "WalledGarden.view")
    public List<WalledGarden> findAll() {
        return super.findAll();
    }

    @Override
    @Restrict(restriction = "WalledGarden.view")
    public WalledGarden view(Long id) {
        return findById(id);
    }

    @Override
    @Restrict(restriction = "WalledGarden.create", parameters = "#walledGarden")
    @Validate(validation = "WalledGarden.create", parameters = "#walledGarden")
    @Interceptors(CaptureChangesInterceptor.class)
    public void create(WalledGarden walledGarden) throws IllegalWalledGardenAgencyTypeException {
        AgencyAccount agency = em.find(AgencyAccount.class, walledGarden.getAgency().getId());
        walledGarden.setAgency(agency);

        PublisherAccount publisher = em.find(PublisherAccount.class, walledGarden.getPublisher().getId());
        walledGarden.setPublisher(publisher);

        auditService.audit(walledGarden, ActionType.CREATE);

        super.create(walledGarden);


        jdbcTemplate.execute(
                "select siteutil.update_walled_garden_for_tags(?::int,?::varchar)",
                publisher.getId(), walledGarden.getPublisherMarketplaceType().name()
        );
        jdbcTemplate.execute(
                "select campaign_util.updatewalledgardenforcampaigns(?::int,?::varchar)",
                agency.getId(), walledGarden.getAgencyMarketplaceType().name()
        );

        cacheService.evictRegionNonTransactional(Tag.class);
        cacheService.evictRegionNonTransactional(Campaign.class);
    }
    
    @Override
    @Restrict(restriction = "WalledGarden.update")
    @Validate(validation = "WalledGarden.update", parameters = "#walledGarden")
    @Interceptors(CaptureChangesInterceptor.class)
    public WalledGarden update(WalledGarden walledGarden) {
        WalledGarden existingWalledGarden = em.find(WalledGarden.class, walledGarden.getId());

        walledGarden.setAgency(existingWalledGarden.getAgency());
        walledGarden.setPublisher(existingWalledGarden.getPublisher());

        WalledGarden persistent = super.update(walledGarden);

        auditService.audit(persistent, ActionType.UPDATE);

        return persistent;
    }
    
    @Override
    public WalledGarden findByAdvertiser(Long advertiserAccountId) {
        Query q = em.createNamedQuery("WalledGarden.findByAdvertiser");
        q.setParameter("advertiserAccountId", advertiserAccountId);
        try {
            return (WalledGarden) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public WalledGarden findByPublisher(Long publisherAccountId) {
        Query q = em.createNamedQuery("WalledGarden.findByPublisher");
        q.setParameter("publisherAccountId", publisherAccountId);
        try {
            return (WalledGarden) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<WalledGarden> findAllWithDependencies() {
        List<WalledGarden> walledGardens = em.createNamedQuery("WalledGarden.findAllWithDependencies").getResultList();
        restrictWalledGardens(walledGardens);
        return walledGardens;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Restrict(restriction = "WalledGarden.view")
    public List<WalledGarden> findWithDependancesByCountryCode(String countryCode) {
        Query q = em.createNamedQuery("WalledGarden.findWithDependancesByCountryCode");
        q.setParameter("countryCode", countryCode);
        List<WalledGarden> walledGardens = q.getResultList();
        restrictWalledGardens(walledGardens);
        return walledGardens;
    }

    private void restrictWalledGardens(List<WalledGarden> walledGardens) {
        CollectionUtils.filter(walledGardens, new Filter<WalledGarden>() {
            @Override
            public boolean accept(WalledGarden walledGarden) {
                AgencyAccount agency = walledGarden.getAgency();
                PublisherAccount publisher = walledGarden.getPublisher();
                if (!entityRestrictions.canView(agency) || !entityRestrictions.canView(publisher)) {
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public List<AccountTO> findFreeAgencyAccounts(String countryCode) {
        return findAccounts(WalledGarden.FIND_FREE_AGENCY_ACCOUNTS, countryCode);
    }
    
    @Override
    public List<AccountTO> findFreePublisherAccounts(String countryCode) {
        return findAccounts(WalledGarden.FIND_FREE_PUBLISHER_ACCOUNTS, countryCode);
    }
    
    @SuppressWarnings("unchecked")
    private List<AccountTO> findAccounts(String sql, String countryCode) {
        if (currentUserService.isInternalWithRestrictedAccess()) {
             sql += " and a.internal_account_id in (:accessAccountIds)";
        }
        if (currentUserService.isAccountManager()) {
            sql += " and a.account_manager_id = :userId";
        }

        Query query = em.createNativeQuery(sql).setParameter("countryCode", countryCode);

        if (currentUserService.isInternalWithRestrictedAccess()) {
            query.setParameter("accessAccountIds", currentUserService.getAccessAccountIds());
        }
        if (currentUserService.isAccountManager()) {
            query.setParameter("userId", currentUserService.getUserId());
        }
        List<Object[]> sqlResult = query.getResultList();
        
        List<AccountTO> result = new ArrayList<AccountTO>(CollectionUtils.convert(new AccountTOConverter(), sqlResult));
        Collections.sort(result, new IdNameComparator());
        
        return result;
    }
    
    @Override
    public boolean isPublisherWalledGarden(Long publisherAccountId) {
        Query q = em.createNamedQuery("WalledGarden.getPublishersCount");
        q.setParameter("publisherAccountId", publisherAccountId);

        Number count = (Number) q.getSingleResult();

        return count.intValue() > 0;
    }

    @Override
    public boolean isAdvertiserWalledGarden(Long advertiserAccountId) {
        Query q = em.createNamedQuery("WalledGarden.getAdvertisersCount");
        q.setParameter("advertiserId", advertiserAccountId);

        Number count = (Number) q.getSingleResult();

        return count.intValue() > 0;
    }

    @Override
    public boolean isAgencyWalledGarden(Long agencyAccountId) {
        Query q = em.createNamedQuery("WalledGarden.getAgencyCount");
        q.setParameter("agencyAccountId", agencyAccountId);

        Number count = (Number) q.getSingleResult();

        return count.intValue() > 0;
    }
    
    @Override
    public boolean isAgencyAccountTypeWalledGarden(Long agencyAccountTypeId) {
        Query q = em.createNamedQuery("WalledGarden.getAgencyAccountTypeCount");
        q.setParameter("agencyAccountTypeId", agencyAccountTypeId);

        Number count = (Number) q.getSingleResult();

        return count.intValue() > 0;
    }

    @Override
    public boolean validateAgencyAccountType(AccountType agencyAccountType) {
        Set<AccountTypeCCGType> ccgTypes = agencyAccountType.getCcgTypes();
        for (AccountTypeCCGType ccgType : ccgTypes) {
            if (ccgType.getCcgType() != CCGType.DISPLAY) {
                return false;
            }
            if (ccgType.getRateType() != RateType.CPM && ccgType.getRateType() != RateType.CPC) {
                return false;
            }
        }
        return true;
    }
}
