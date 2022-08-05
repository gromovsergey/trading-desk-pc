package com.foros.session.support;

import com.foros.session.StatsJobJdbcTemplate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
public class DBJobsExecutorService {

    @EJB
    private StatsJobJdbcTemplate statsJobJdbcTemplate;

    public void checkPendingInactivation() {
        execute("cmpchannels.check_pending_inactivation");
    }

    public void checkThresholdChannelByUsers() {
        execute("displaystatus.update_channel_status_by_stats");
    }

    public void checkBillingDate() {
        execute("billing.generate_invoices_and_bills");
    }

    public void calcCTR() {
        execute("ctr.init");
        execute("ctr.pub_tag_adjustments");
        execute("ctr.keyword_targeted_text_groups");
        execute("ctr.keyword_targeted_text_groups_tow");
        execute("ctr.cpc_groups");
    }

    private void execute(String procedureName) {
        statsJobJdbcTemplate.execute("select from " + procedureName + "()");
    }
}
