package com.foros.session.channel.targeting;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.util.PersistenceUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "TargetingChannelService")
public class TargetingChannelService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    public void addToBulkLink(CampaignCreativeGroup ccg) {
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(em);
        TargetingChannelsHibernateHandler handler = hi.getTargetingChannelsHibernateHandler();
        handler.initialize(jdbcTemplate, em);
        handler.addToBulkLink(ccg);
    }
}
