package com.foros.session.status;

import com.foros.model.Approvable;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.model.site.Site;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.security.AuditService;
import com.foros.session.site.PublisherEntityRestrictions;
import com.foros.session.workflow.ApprovalWorkflow;
import com.foros.session.workflow.WorkflowService;
import com.foros.util.xml.QADescriptionText;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;
import org.hibernate.Hibernate;

@Stateless(name = "ApprovalService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class})
public class ApprovalServiceBean implements ApprovalService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private WorkflowService workflowService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    private Map<Class, Predicate> approvePredicates;

    @Override
    public <T extends ApprovableEntity> T approve(T entity) {
        return setStatus(entity, ApprovalAction.APPROVE, null);
    }

    @Override
    public <T extends ApprovableEntity> T decline(T entity, String reason) {
        return setStatus(entity, ApprovalAction.DECLINE, reason);
    }

    @PostConstruct
    public void init() {

        // approve predicates
        approvePredicates = new HashMap<Class, Predicate>();
        approvePredicates.put(CampaignCreativeGroup.class, TruePredicate.INSTANCE);

        approvePredicates.put(Creative.class, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return advertiserEntityRestrictions.canApprove();
            }
        });

        approvePredicates.put(Site.class, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return publisherEntityRestrictions.canApprove();
            }
        });
    }

    private <T extends ApprovableEntity> T setStatus(T entity, ApprovalAction action, String reason) {
        ApprovalWorkflow workflow = workflowService.getApprovalWorkflow(entity);
        entity.setQaStatus(workflow.doAction(action));

        ApplicationPrincipal principal = SecurityContext.getPrincipal();

        User qaUser = null;
        Long userId = principal == null ? null : principal.getUserId();

        if (userId != null) {
            qaUser = em.getReference(User.class, userId);
        }

        entity.setQaUser(qaUser);
        entity.setQaDate(new Date());

        if (reason == null) {
            entity.setQaDescription(null);
        } else {
            entity.setQaDescriptionObject(new QADescriptionText(reason));
        }

        auditService.audit(entity, ActionType.UPDATE);

        displayStatusService.update(entity);

        return entity;
    }

    @Override
    public boolean isActionAvailable(Approvable entity, ApprovalAction action) {
        return workflowService.getApprovalWorkflow(entity).isActionAvailable(action);
    }

    @Override
    public void makePendingOnChange(ApprovableEntity entity) {
        ApproveStatus currentStatus = entity.getQaStatus();
        ApproveStatus newStatus;

        // do not check update permission here - assumes that it checked before (it's required to make "change" anyway)
        switch (currentStatus) {
            case APPROVED:
                newStatus = checkPredicate(approvePredicates, entity) ? currentStatus : ApproveStatus.HOLD;
                break;
            case DECLINED:
                newStatus = ApproveStatus.HOLD;
                break;
            case HOLD:
                newStatus = ApproveStatus.HOLD;
                break;
            default:
                throw new IllegalStateException();
        }

        if (newStatus != currentStatus) {
            entity.setQaStatus(newStatus);
        }
    }

    private boolean checkPredicate(Map<Class, Predicate> predicates, ApprovableEntity entity) {
        Class clazz = Hibernate.getClass(entity);
        Predicate predicate = predicates.get(clazz);
        if (predicate == null) {
            throw new RuntimeException("Unknown class: " + clazz);
        }
        return predicate.evaluate(entity);
    }
}
