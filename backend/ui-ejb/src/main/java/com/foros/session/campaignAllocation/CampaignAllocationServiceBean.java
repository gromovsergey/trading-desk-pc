package com.foros.session.campaignAllocation;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationStatus;
import com.foros.model.campaign.CampaignAllocationTO;
import com.foros.model.campaign.CampaignAllocationsTotalTO;
import com.foros.model.campaign.CampaignCreditAllocationTO;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.EntityUtils;
import com.foros.util.JpaCollectionMerger;
import com.foros.util.VersionCollisionException;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "CampaignAllocationService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CampaignAllocationServiceBean implements CampaignAllocationService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private CampaignService campaignService;

    @EJB
    private AuditService auditService;

    @EJB
    private HibernateWorkExecutorService executor;

    @EJB
    private DisplayStatusService displayStatusService;

    @Override
    public List<InvoiceOpportunityTO> findInvoiceOpportunities(Long invoiceId, boolean includeCampaigns) {
        String query = "select io.io_id, io.io_number, io.po_number" + (includeCampaigns ? ", ca.campaign_id, c.name " : " ") +
                ", sum(iio.adv_amount) as sum_adv_amount " +
                "from " +
                "  campaign_util.InvoiceInsertionOrder iio " +
                "  inner join CAMPAIGNALLOCATION ca on iio.campaign_allocation_id = ca.campaign_allocation_id " +
                "  inner join InsertionOrder io on ca.io_id = io.io_id " +
                "  inner join Campaign c on ca.campaign_id = c.campaign_id " +
                "where " +
                "  iio.invoice_id = :invoiceId " +
                "group by io.io_id, io.io_number, io.po_number" + (includeCampaigns ? ", ca.campaign_id, c.name " : " ") +
                "order by " +
                (includeCampaigns ? "c.name" : "sum_adv_amount desc");

        Query q = em.createNativeQuery(query);
        q.setParameter("invoiceId", invoiceId);

        List<Object[]> sqlResult = q.getResultList();
        List<InvoiceOpportunityTO> result = new ArrayList<InvoiceOpportunityTO>(sqlResult.size());
        for (Object[] row : sqlResult) {
            result.add(new InvoiceOpportunityTO(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    (String) row[2],
                    (includeCampaigns ? ((Number) row[3]).longValue() : null),
                    (includeCampaigns ? (String) row[4] : null),
                    (includeCampaigns ? (BigDecimal) row[5] : (BigDecimal) row[3])
            ));
        }
        return result;
    }

    private List<OpportunityTO> getOpportunities(Long accountId, Long campaignId) {
        List<String> probabilities = Arrays.asList(Probability.LIVE.toString(), Probability.AWAITING_GO_LIVE.toString(), Probability.IO_SIGNED.toString());
        return jdbcTemplate.query(
                "select io_id, name, io_number, notes, amount, spent_amount, unallocated_amount " +
                        " from campaign_util.get_opportunities(?::int, ?::int, ?)",
                new Object[]{accountId, campaignId, jdbcTemplate.createArray("varchar", probabilities)},
                new RowMapper<OpportunityTO>() {
                    @Override
                    public OpportunityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long id = rs.getLong("io_id");
                        String name = rs.getString("name");
                        String ioNumber = rs.getString("io_number");
                        String notes = rs.getString("notes");
                        BigDecimal amount = rs.getBigDecimal("amount");
                        BigDecimal spentAmount = rs.getBigDecimal("spent_amount");
                        BigDecimal unallocatedAmount = rs.getBigDecimal("unallocated_amount");

                        return new OpportunityTO(id, name, ioNumber, notes, amount, spentAmount, unallocatedAmount);
                    }
                });
    }

    @Override
    public Map<Long, OpportunityTO> getOpportunitiesMap(Long accountId) {
        List<OpportunityTO> tos = getOpportunities(accountId, null);
        return EntityUtils.mapEntityIds(tos);
    }

    @Override
    public List<OpportunityTO> getAvailableOpportunities(Long accountId) {
        List<OpportunityTO> opportunities = getOpportunities(accountId, null);

        // filter out fully spent opportunities
        List<OpportunityTO> result = new ArrayList<>();
        for (OpportunityTO to : opportunities) {
            if (to.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
                result.add(to);
            }
        }
        return result;
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public List<CampaignAllocation> getCampaignAllocations(Long campaignId) {
        Query q = em.createQuery("select a from CampaignAllocation a " +
                " where a.campaign.id = :campaignId and a.status = 'A' order by a.order");
        q.setParameter("campaignId", campaignId);
        return q.getResultList();
    }

    private List<CampaignAllocationTO> getCampaignAllocationTos(Long campaignId) {
        Campaign campaign = campaignService.find(campaignId);
        Map<Long, OpportunityTO> opportunitiesMap = getOpportunitiesMap(campaign.getAccount().getId());

        List<CampaignAllocationTO> tos = new ArrayList<>();
        for (CampaignAllocation ca : getCampaignAllocations(campaignId)) {
            CampaignAllocationTO to = new CampaignAllocationTO();

            OpportunityTO opportunityTO = opportunitiesMap.get(ca.getOpportunity().getId());
            to.setOpportunity(opportunityTO);

            to.setOrder(ca.getOrder());
            to.setAmount(ca.getAmount());
            to.setUtilizedAmount(ca.getUtilizedAmount());

            // http://confluence.ocslab.com/display/TDOCDRAFT/REQ-3588+Campaign+Allocation+Change+to+Prevent+Budget+Issues#REQ-3588CampaignAllocationChangetoPreventBudgetIssues-CampaignCreditManageScreen
            // | "Available Amount" (for this campaign)
            // |  = max(min{I/O Available Amount, <Campaign Allocation Amount> - <Campaign Spent Amount>}, 0)
            to.setAvailableAmount(BigDecimal.ZERO.max(
                    opportunityTO.getAvailableAmount().min(ca.getAmount().subtract(ca.getUtilizedAmount()))));
            tos.add(to);
        }
        return tos;
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.view", parameters = "#campaignId")
    public CampaignAllocationsTotalTO getCampaignAllocationsTotal(Long campaignId) {
        CampaignAllocationsTotalTO result = new CampaignAllocationsTotalTO();
        CampaignCreditAllocationTO creditAllocation = campaignCreditAllocationService.findCreditAllocationForCampaign(campaignId);
        List<CampaignAllocationTO> allocations = getCampaignAllocationTos(campaignId);
        result.setAllocations(allocations);

        // Allocation Amount
        BigDecimal amount = (creditAllocation == null) ? BigDecimal.ZERO : creditAllocation.getAllocatedAmount();
        for (CampaignAllocationTO ca : allocations) {
            amount = amount.add(ca.getAmount());
        }
        result.setAmount(amount);

        // Utilised Amount
        BigDecimal utilisedAmount = (creditAllocation == null) ? BigDecimal.ZERO : creditAllocation.getUsedAmount();
        for (CampaignAllocationTO ca : allocations) {
            utilisedAmount = utilisedAmount.add(ca.getUtilizedAmount());
        }
        result.setUtilisedAmount(utilisedAmount);

        // Available Amount
        // http://confluence.ocslab.com/display/TDOCDRAFT/REQ-3588+Campaign+Allocation+Change+to+Prevent+Budget+Issues#REQ-3588CampaignAllocationChangetoPreventBudgetIssues-CampaignCreditManageScreen
        // | Add "Totals" row in the end, with sum for all "Amount" columns
        // But we can't just summarize this column, as the sum can be ridiculous
        // (for example, 4 allocations with full allocation each, gives "Available Amount" 4 times more then IO available amount)
        BigDecimal availableAmount = (creditAllocation == null) ? BigDecimal.ZERO : creditAllocation.getAvailableAmount();
        Map<Long, BigDecimal> availableAmounts = new HashMap<>();
        for (CampaignAllocationTO ca : allocations) {
            BigDecimal value = availableAmounts.get(ca.getOpportunity().getId());
            if (value == null) {
                value = BigDecimal.ZERO;
            }
            value = value.add(ca.getAvailableAmount());

            // do not go over the opportunity's available amount
            value = value.min(BigDecimal.ZERO.max(ca.getOpportunity().getAvailableAmount()));
            availableAmounts.put(ca.getOpportunity().getId(), value);
        }
        for (Long opportunityId : availableAmounts.keySet()) {
            availableAmount = availableAmount.add(availableAmounts.get(opportunityId));
        }
        result.setAvailableAmount(availableAmount);

        return result;
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.createUpdate", parameters = "find('Campaign', #campaignId)")
    public List<Long> findRemovableAllocationIds(Long campaignId) {
        return jdbcTemplate.queryForList("select a.campaign_allocation_id " +
                " from CAMPAIGNALLOCATION a " +
                " where a.campaign_id = ? and a.status = 'A' and a.utilized_amount = 0 " +
                " and not exists (" +
                "      select 1 from campaign_util.InvoiceInsertionOrder iio where iio.campaign_allocation_id = a.campaign_allocation_id" +
                " )", new Object[] { campaignId }, Long.class);
    }

    @Override
    @Restrict(restriction = "CampaignAllocation.createUpdate", parameters = "find('Campaign', #campaign.id)")
    @Validate(validation = "CampaignAllocations.createUpdate", parameters = "#campaign")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public void updateCampaignAllocations(final Campaign campaign) {
        final Campaign existingCampaign = campaignService.find(campaign.getId());

        if(!existingCampaign.getVersion().equals(campaign.getVersion())) {
            throw new VersionCollisionException();
        }

        final Collection<CampaignAllocation> newAllocations = new ArrayList<CampaignAllocation>();
        final Collection<CampaignAllocation> existingAllocations = new ArrayList<CampaignAllocation>();
        campaign.unregisterChange("allocations");

        (new JpaCollectionMerger<CampaignAllocation>(existingCampaign.getAllocations(), campaign.getAllocations()) {
            @Override
            protected boolean add(CampaignAllocation updated) {
                newAllocations.add(updated);

                Opportunity opportunity = em.find(Opportunity.class, updated.getOpportunity().getId());
                updated.setOpportunity(opportunity);
                updated.setCampaign(existingCampaign);
                updated.setStatus(CampaignAllocationStatus.ACTIVE);
                updated.setUtilizedAmount(BigDecimal.ZERO);

                if (updated.getOrder().intValue() == 1) {
                    updated.setStartDate(new Date(System.currentTimeMillis()));
                } else {
                    updated.setStartDate(null);
                }
                updated.setEndDate(null);

                campaign.registerChange("allocations");
                return super.add(updated);
            }

            @Override
            protected void update(CampaignAllocation persistent, CampaignAllocation updated) {
                existingAllocations.add(persistent);

                if (CampaignAllocationStatus.ACTIVE.equals(persistent.getStatus())) {
                    updated.retainChanges("amount");
                    if (getEM().merge(updated).isChanged()) {
                        campaign.registerChange("allocations");
                    }
                } else {
                    throw new VersionCollisionException();
                }
            }

            @Override
            protected boolean delete(CampaignAllocation persistent) {
                if (CampaignAllocationStatus.ACTIVE.equals(persistent.getStatus())) {
                    campaign.registerChange("allocations");
                    return super.delete(persistent);
                }
                return false;
            }

            @Override
            protected EntityManager getEM() {
                return em;
            }
        }).merge();

        if (!campaign.isChanged("allocations")) {
            return;
        }

        updateOrders(existingAllocations, newAllocations);

        auditService.audit(existingCampaign, ActionType.UPDATE);

        em.flush();

        runProcOnCampaignAllocationChanged(campaign.getId());
        displayStatusService.update(campaign);
    }

    private void runProcOnCampaignAllocationChanged(final Long campaignId) {
        jdbcTemplate.execute("select campaign_util.oncampaignallocationchanged(?::integer)", campaignId);
        jdbcTemplate.scheduleEviction();
    }

    private void updateOrders(Collection<CampaignAllocation> existingAllocations, Collection<CampaignAllocation> newAllocations) {
        for (int i = 1; i <= existingAllocations.size() + newAllocations.size(); i++) {
            if (checkOrderExists(newAllocations, i)) {
                increaseOrders(existingAllocations, i);
            } else if (!checkOrderExists(existingAllocations, i)) {
                decreaseOrders(existingAllocations, i);
            }
        }
    }

    private boolean checkOrderExists(Collection<CampaignAllocation> allocations, int order) {
        for (CampaignAllocation allocation : allocations) {
            if (allocation.getOrder().intValue() == order) {
                return true;
            }
        }
        return false;
    }

    private void increaseOrders(Collection<CampaignAllocation> allocations, int order) {
        for (CampaignAllocation allocation : allocations) {
            if (allocation.getOrder().intValue() == order) {
                increaseOrders(allocations, order + 1);
                allocation.setOrder(allocation.getOrder() + 1);
                if (order == 1) {
                    allocation.setEndDate(new Date(System.currentTimeMillis()));
                }
                return;
            }
        }
    }

    private void decreaseOrders(Collection<CampaignAllocation> allocations, int order) {
        boolean changed = false;
        for (CampaignAllocation allocation : allocations) {
            if (allocation.getOrder().intValue() > order) {
                allocation.setOrder(allocation.getOrder() - 1);
                if (allocation.getOrder().intValue() == 1) {
                    allocation.setStartDate(new Date(System.currentTimeMillis()));
                }
                changed = true;
            }
        }
        if (!checkOrderExists(allocations, order) && changed) {
            decreaseOrders(allocations, order);
        }
    }

    @Override
    public BigDecimal getEndedBudget(Long campaignId) {
       return jdbcTemplate.queryForObject("select coalesce(sum(utilized_amount), 0) " +
               " from CAMPAIGNALLOCATION ca " +
               " where ca.campaign_id = ? and ca.status = 'E' ",
               new Object [] { campaignId }, BigDecimal.class);
    }
}
