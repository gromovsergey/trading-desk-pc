package com.foros.session.channel.targeting;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.LoggingJdbcTemplate;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;

public class TargetingChannelsHibernateHandler {
    private Set<Long> ccgIds = new HashSet<>();
    private LoggingJdbcTemplate jdbcTemplate;

    public void initialize(LoggingJdbcTemplate jdbcTemplate, EntityManager em) {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = jdbcTemplate;
        }
    }

    private void clear() {
        jdbcTemplate = null;
        ccgIds.clear();
    }

    public boolean isInitialized() {
        return jdbcTemplate != null;
    }

    public void handle() {
        if (!isInitialized()) {
            return;
        }

        updateCCGs();

        clear();
    }

    private void updateCCGs() {
        final Long[] ids = ccgIds.toArray(new Long[ccgIds.size()]);
        jdbcTemplate.execute("select targetingchannels.bulk_link_targeting_channels(?)", jdbcTemplate.createArray("int", ids));
        jdbcTemplate.scheduleEviction();
    }

    public void addToBulkLink(CampaignCreativeGroup ccg) {
        ccgIds.add(ccg.getId());
    }
}
