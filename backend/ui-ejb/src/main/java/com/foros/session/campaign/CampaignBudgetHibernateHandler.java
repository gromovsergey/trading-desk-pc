package com.foros.session.campaign;

import com.foros.session.LoggingJdbcTemplate;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;

public class CampaignBudgetHibernateHandler {

    private Set<Long> campaignIds = new HashSet<>();
    private LoggingJdbcTemplate jdbcTemplate;
    private EntityManager em;

    public void initialize(LoggingJdbcTemplate jdbcTemplate, EntityManager em) {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = jdbcTemplate;
        }
        if (this.em == null) {
            this.em = em;
        }
    }

    public boolean isInitialized() {
        return jdbcTemplate != null && em != null;
    }

    public void registerCampaignId(Long id) {
        campaignIds.add(id);
    }

    public void handle() {
        if (!isInitialized() || campaignIds.isEmpty()) {
            return;
        }

        jdbcTemplate.execute("select campaign_util.bulkupdatecampaignbudget(?::int[]) ", jdbcTemplate.createArray("int", campaignIds));
        jdbcTemplate.scheduleEviction();

        clear();
    }

    private void clear() {
        jdbcTemplate = null;
        em = null;
        campaignIds.clear();
    }
}
