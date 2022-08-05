package com.foros.session.campaignCredit;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditContainerWrapper;
import com.foros.model.campaign.CampaignCreditPurpose;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.security.AuditService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.EntityUtils;
import com.foros.util.SQLUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "CampaignCreditService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CampaignCreditServiceBean implements CampaignCreditService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AuditService auditService;

    @EJB
    private DisplayStatusService displayStatusService;

    @Override
    @Restrict(restriction = "CampaignCredit.edit", parameters = "find('Account', #campaignCredit.account.id)")
    @Validate(validation = "CampaignCredit.create", parameters = "#campaignCredit")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public Long create(CampaignCredit campaignCredit) {
        AdvertisingAccountBase account = em.find(AdvertisingAccountBase.class, campaignCredit.getAccount().getId());
        campaignCredit.setAccount(account);

        prePersist(campaignCredit);

        em.persist(campaignCredit);

        account.getCampaignCredits().add(campaignCredit);
        auditService.audit(new CampaignCreditContainerWrapper(account), ActionType.UPDATE);

        em.flush();
        displayStatusService.scheduleStatusEvictionOnCampaignCreditChange(account);

        return campaignCredit.getId();
    }

    @Override
    @Restrict(restriction = "CampaignCredit.edit", parameters = "find('CampaignCredit', #campaignCredit.id)")
    @Validate(validation = "CampaignCredit.update", parameters = "#campaignCredit")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public CampaignCredit update(CampaignCredit campaignCredit) {
        CampaignCredit existingCampaignCredit = find(campaignCredit.getId());

        campaignCredit.unregisterChange("id", "account");

        prePersist(campaignCredit);
        EntityUtils.copy(existingCampaignCredit, campaignCredit);

        AdvertisingAccountBase account = (AdvertisingAccountBase) existingCampaignCredit.getAccount();
        account.getCampaignCredits().add(existingCampaignCredit);
        auditService.audit(new CampaignCreditContainerWrapper(account), ActionType.UPDATE);

        em.flush();
        displayStatusService.scheduleStatusEvictionOnCampaignCreditChange(account);

        return existingCampaignCredit;
    }

    private void prePersist(CampaignCredit campaignCredit) {
        if (campaignCredit.isChanged("advertiser") && campaignCredit.getAdvertiser() != null) {
            Long advertiserAccountId = campaignCredit.getAdvertiser().getId();
            if (advertiserAccountId != null) {
                campaignCredit.setAdvertiser(em.getReference(AdvertiserAccount.class, advertiserAccountId));
            } else {
                campaignCredit.setAdvertiser(null);
            }
        }
    }

    @Override
    @Restrict(restriction = "CampaignCredit.delete", parameters = "#id")
    @Validate(validation = "CampaignCredit.delete", parameters = "#id")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public void delete(Long id) {
        CampaignCredit existingCampaignCredit = em.find(CampaignCredit.class, id);
        if (existingCampaignCredit != null) {
            AdvertisingAccountBase account = (AdvertisingAccountBase) existingCampaignCredit.getAccount();
            account.getCampaignCredits().add(existingCampaignCredit);

            auditService.audit(new CampaignCreditContainerWrapper(account), ActionType.UPDATE);

            em.remove(existingCampaignCredit);

            em.flush();
            displayStatusService.scheduleStatusEvictionOnCampaignCreditChange(account);
        }
    }

    @Override
    @Restrict(restriction = "CampaignCredit.view", parameters = "find('CampaignCredit', #id)")
    public CampaignCredit view(Long id) {
        return find(id);
    }

    @Override
    public CampaignCredit find(Long id) {
        CampaignCredit entity = em.find(CampaignCredit.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("Campaign Credit with id=" + id + " not found");
        }
        return entity;
    }

    @Override
    @Restrict(restriction = "CampaignCredit.view", parameters = "find('CampaignCredit', #campaignCreditId)")
    public CampaignCreditStatsTO getStats(Long campaignCreditId) {
        return jdbcTemplate.queryForObject(
                "SELECT cc.amount amount, " +
                " coalesce(sum(au.used_amount), 0) used_amount, " +
                " coalesce(sum(a.allocated_amount), 0) allocated_amount, " +
                " coalesce(max(a.allocated_amount), 0) max_allocation_amount " +
                " FROM CAMPAIGNCREDIT cc " +
                "  LEFT JOIN CAMPAIGNCREDITALLOCATION a ON cc.campaign_credit_id = a.campaign_credit_id " +
                "  LEFT JOIN CAMPAIGNCREDITALLOCATIONUSAGE au ON au.camp_credit_alloc_id = a.camp_credit_alloc_id " +
                " WHERE cc.campaign_credit_id = ? " +
                "GROUP BY cc.campaign_credit_id ",
                new Object[]{campaignCreditId},
                new RowMapper<CampaignCreditStatsTO>() {
                    @Override
                    public CampaignCreditStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CampaignCreditStatsTO to = new CampaignCreditStatsTO();
                        to.setAmount(rs.getBigDecimal("amount"));
                        to.setSpentAmount(rs.getBigDecimal("used_amount"));
                        to.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
                        to.setMaxAllocationAmount(rs.getBigDecimal("max_allocation_amount"));
                        BigDecimal unallocatedAmount = rs.getBigDecimal("amount").subtract(rs.getBigDecimal("allocated_amount"));
                        to.setUnallocatedAmount(unallocatedAmount.max(BigDecimal.ZERO));
                        BigDecimal availableAmount = rs.getBigDecimal("amount").subtract(rs.getBigDecimal("used_amount"));
                        to.setAvailableAmount(availableAmount.max(BigDecimal.ZERO));
                        return to;
                    }
                });
    }

    @Override
    public List<Long> getAllocationsAdvertiserIds(Long campaignCreditId) {
        return jdbcTemplate.queryForList(
                "select distinct c.account_id " +
                "from campaigncreditallocation cca " +
                "inner join campaign c on (cca.campaign_id = c.campaign_id) " +
                "where cca.campaign_credit_id = ?",
                Long.class,
                campaignCreditId
        );
    }

    @Override
    @Restrict(restriction = "CampaignCredit.view", parameters = "find('Account', #accountId)")
    public List<CampaignCreditTO> findCampaignCredits(Long accountId) {
        String sql = "SELECT " +
                " cc.campaign_credit_id, cc.version, cc.purpose, cc.description, adv.account_id adv_account_id, adv.name adv_name, " +
                " cc.amount, cc.amount - coalesce(SUM(au.used_amount), 0) balance, count(a.camp_credit_alloc_id) > 0 has_allocations " +
                " FROM CampaignCredit cc " +
                " LEFT JOIN Account adv ON (cc.adv_account_id = adv.account_id) " +
                " LEFT JOIN CampaignCreditAllocation a ON (cc.campaign_credit_id = a.campaign_credit_id) " +
                " LEFT JOIN CampaignCreditAllocationUsage au ON (au.camp_credit_alloc_id = a.camp_credit_alloc_id) " +
                " WHERE cc.account_id = ? " +
                " GROUP BY cc.campaign_credit_id, cc.version, cc.purpose, cc.description, adv.account_id, adv.name, cc.amount " +
                " ORDER BY cc.version DESC";

        return jdbcTemplate.query(sql, new Object[]{accountId}, new RowMapper<CampaignCreditTO>() {
            @Override
            public CampaignCreditTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CampaignCreditTO(
                        rs.getLong("campaign_credit_id"),
                        rs.getTimestamp("version"),
                        CampaignCreditPurpose.valueOf(rs.getString("purpose")),
                        rs.getString("description"),
                        SQLUtil.nullSafeGet(rs, "adv_account_id", Long.class),
                        rs.getString("adv_name"),
                        rs.getBigDecimal("amount"),
                        rs.getBigDecimal("balance"),
                        rs.getBoolean("has_allocations")
                );
            }
        });
    }

    @Override
    public boolean hasCampaignCredits(Long accountId) {
        return jdbcTemplate.queryForObject(
                "select exists(select 1 from CampaignCredit cc where cc.account_id = ?)",
                Boolean.class,
                accountId
        );
    }

    @Override
    public List<EntityTO> getCampaignsForCreditAllocation(Long advertiserId) {
        if (advertiserId == null) {
            return new ArrayList<>();
        }

        String query = "select c.campaign_id, c.name, c.status " +
                "from campaign c " +
                "where " +
                "  c.account_id = ? and c.status <> 'D' " +
                "  and not exists ( " +
                "    select * " +
                "    from " +
                "      campaigncreditallocation a " +
                "      left join campaigncreditallocationusage au on au.camp_credit_alloc_id = a.camp_credit_alloc_id " +
                "    where " +
                "      a.campaign_id = c.campaign_id " +
                "      and a.allocated_amount > au.used_amount " +
                "  ) " +
                "order by c.name ";

        return jdbcTemplate.query(
                query,
                new Object[] { advertiserId },
                new RowMapper<EntityTO>() {
                    @Override
                    public EntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long id = rs.getLong(1);
                        String name = rs.getString(2);
                        char status = rs.getString(3).charAt(0);

                        return new EntityTO(id, name, status);
                    }
                });
    }
}
