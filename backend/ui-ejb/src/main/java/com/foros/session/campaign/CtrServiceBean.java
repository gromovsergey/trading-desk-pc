package com.foros.session.campaign;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.security.AuditService;

import java.sql.Timestamp;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless(name = "CtrService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class})
public class CtrServiceBean implements CtrService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;


    @Override
    @Restrict(restriction = "AdvertiserEntity.resetCtr",parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    public void resetCtr(Long id, Timestamp version) {
        CampaignCreativeGroup group = new CampaignCreativeGroup(id);
        CampaignCreativeGroup existingGroup = em.find(CampaignCreativeGroup.class, id);
        group.setVersion(version);
        group.setCtrResetId(existingGroup.getCtrResetId() + 1);
        existingGroup = em.merge(group);
        auditService.audit(existingGroup, ActionType.UPDATE);
    }
}
