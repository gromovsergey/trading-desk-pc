package com.foros.session.campaignCredit;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.campaign.CampaignService;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "CampaignCreditAllocationService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CampaignCreditAllocationServiceBean implements CampaignCreditAllocationService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignCreditService campaignCreditService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private HibernateWorkExecutorService executor;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    @Restrict(restriction = "CampaignCredit.editAllocations", parameters = "find('CampaignCredit', #allocation.campaignCredit.id)")
    @Validate(validation = "CampaignCreditAllocation.create", parameters = "#allocation")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public Long create(CampaignCreditAllocation allocation) {
        CampaignCredit campaignCredit = em.find(CampaignCredit.class, allocation.getCampaignCredit().getId());
        allocation.setCampaignCredit(campaignCredit);

        Campaign campaign = em.find(Campaign.class, allocation.getCampaign().getId());
        allocation.setCampaign(campaign);
        allocation.setUsedAmount(BigDecimal.ZERO);
        em.persist(allocation);

        campaign.getCreditAllocations().add(allocation);
        campaignCredit.getAllocations().add(allocation);

        auditService.audit(campaign, ActionType.UPDATE);

        em.flush();

        updateCampaignBudget(campaign.getId());
        displayStatusService.scheduleStatusEvictionOnCampaignCreditChange(campaign.getAccount());

        return allocation.getId();
    }

    @Override
    @Restrict(restriction = "CampaignCredit.editAllocations", parameters = "find('CampaignCreditAllocation', #allocation.id)")
    @Validate(validation = "CampaignCreditAllocation.update", parameters = "#allocation")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public CampaignCreditAllocation update(CampaignCreditAllocation allocation) {
        CampaignCreditAllocation existingAllocation = find(allocation.getId());

        if (!existingAllocation.getVersion().equals(allocation.getVersion())) {
            throw new OptimisticLockException(existingAllocation);
        }

        existingAllocation.setAllocatedAmount(allocation.getAllocatedAmount());

        Campaign campaign = existingAllocation.getCampaign();
        auditService.audit(campaign, ActionType.UPDATE);

        em.flush();

        updateCampaignBudget(campaign.getId());
        displayStatusService.scheduleStatusEvictionOnCampaignCreditChange(campaign.getAccount());

        return existingAllocation;
    }

    private void updateCampaignBudget(Long campaignId) {
        final Long[] ids = new Long[] { campaignId };
        jdbcTemplate.execute("select campaign_util.bulkupdatecampaignbudget(?)", jdbcTemplate.createArray("int", ids));
        jdbcTemplate.scheduleEviction();
    }

    @Override
    public CampaignCreditAllocation find(Long id) {
        CampaignCreditAllocation entity = em.find(CampaignCreditAllocation.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("Campaign Credit Allocation with id=" + id + " not found");
        }
        return entity;
    }

    @Override
    @Restrict(restriction = "CampaignCredit.view", parameters = "find('CampaignCredit', #campaignCreditId)")
    public List<CampaignCreditAllocationTO> findCreditAllocations(Long campaignCreditId) {
        Query q = em.createNamedQuery("CampaignCreditAllocation.findByCampaignCreditId");
        q.setParameter("campaignCreditId", campaignCreditId);
        List<CampaignCreditAllocationTO> result = fillTOs(q.getResultList());
        return result;
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public CampaignCreditAllocationTO findCreditAllocationForCampaign(Long campaignId) {
        Query q = em.createNamedQuery("CampaignCreditAllocation.findByCampaignId");
        q.setParameter("campaignId", campaignId);
        List<CampaignCreditAllocationTO> result = fillTOs(q.getResultList());

        // look for an allocation with NON-ZERO available amount
        for (CampaignCreditAllocationTO to : result) {
            if (to.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
                return to;
            }
        }
        return null;
    }

    private List<CampaignCreditAllocationTO> fillTOs(List campaignCreditAllocations) {
        List<CampaignCreditAllocationTO> result = new ArrayList<>();
        List<CampaignCreditAllocation> allocations = (List<CampaignCreditAllocation>)campaignCreditAllocations;
        if (allocations.isEmpty()) {
            return result;
        }

        CampaignCredit campaignCredit = allocations.get(0).getCampaignCredit();
        CampaignCreditStatsTO stats = campaignCreditService.getStats(campaignCredit.getId());

        for (CampaignCreditAllocation cca : allocations) {
            CampaignCreditAllocationTO to = new CampaignCreditAllocationTO();
            to.setId(cca.getId());
            to.setVersion(cca.getVersion());
            to.setAllocatedAmount(cca.getAllocatedAmount());
            to.setUsedAmount(cca.getUsedAmount());
            to.setCampaign(cca.getCampaign());
            to.setCampaignCredit(cca.getCampaignCredit());

            /** https://confluence.ocslab.com/display/TDOCDRAFT/REQ-3588+Campaign+Allocation+Change+to+Prevent+Budget+Issues#REQ-3588CampaignAllocationChangetoPreventBudgetIssues-CampaignViewScreen
             *
             *   = max(min{ Campaign Credit Available Amount , <Campaign Allocation Amount> - <Campaign Spent Amount> }, 0)
             */
            BigDecimal allocatedAmount = to.getAllocatedAmount(); // <Campaign Allocation Amount>
            BigDecimal usedAmount = to.getUsedAmount(); // <Campaign Spent Amount>
            BigDecimal availableAmount = allocatedAmount.subtract(usedAmount);
            to.setAvailableAmount(BigDecimal.ZERO.max(stats.getAvailableAmount().min(availableAmount)));

            result.add(to);
        }

        return result;
    }

    @Override
    public boolean hasAllocations(Long campaignId) {
        Query q = em.createNamedQuery("CampaignCreditAllocation.countByCampaignId");
        q.setParameter("campaignId", campaignId);
        Number count = (Number) q.getSingleResult();
        return count.intValue() > 0;
    }
}
