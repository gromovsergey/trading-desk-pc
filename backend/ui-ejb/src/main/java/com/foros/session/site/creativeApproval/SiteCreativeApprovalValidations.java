package com.foros.session.site.creativeApproval;

import com.foros.model.site.Site;
import com.foros.session.bulk.Paging;
import com.foros.session.query.PartialList;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.StringValidator;
import com.foros.validation.util.DuplicateChecker;
import org.apache.commons.lang.ObjectUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@LocalBean
@Stateless
@Validations
public class SiteCreativeApprovalValidations {
    private static final int MAX_OPERATIONS = 500;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private SiteCreativeApprovalService creativeApprovalService;

    @Validation
    public void validatePerform(ValidationContext context, SiteCreativeApprovalOperations operations) {
        Site site = em.find(Site.class, operations.getSiteId());

        if (operations.getOperations() == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("operations");
            return;
        }

        if (operations.getOperations().size() > MAX_OPERATIONS) {
            context.addConstraintViolation("errors.operations.count.max")
                    .withParameters(MAX_OPERATIONS)
                    .withPath("operations");
            return;
        }

        DuplicateChecker<SiteCreativeApprovalOperation> duplicateChecker = DuplicateChecker
                .create(new SiteCreativeApprovalOperationIdentifierFetcher())
                .withTemplate("errors.duplicate.id");

        int index = 0;
        HashMap<Integer, SiteCreativeApprovalOperation> toCheckConstraints = new HashMap<>(operations.getOperations().size());
        for (SiteCreativeApprovalOperation o : operations.getOperations()) {
            ValidationContext subContext = context.subContext(o)
                    .withPath("operation")
                    .withIndex(index++)
                    .build();

            if (o == null) {
                subContext.addConstraintViolation("errors.field.required");
                continue;
            }

            if (!validateCreative(subContext, o)) {
                continue;
            }

            if (!duplicateChecker.check(subContext, "creative.id", o)) {
                continue;
            }

            validateApprovalFields(subContext, o);
            toCheckConstraints.put(index, o);
        }

        Set<Long> creativeIds = duplicateChecker.getCheckedIdentifiers();

        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(site.getId());
        selector.setCreativeIds(creativeIds);
        selector.setPaging(new Paging(0, MAX_OPERATIONS));
        PartialList<SiteCreativeApprovalTO> existingApprovals = creativeApprovalService.searchCreativeApprovals(selector);

        HashMap<Long, SiteCreativeApprovalTO> existingApprovalsById = new HashMap<>(creativeIds.size());
        for (SiteCreativeApprovalTO existingApproval : existingApprovals) {
            existingApprovalsById.put(existingApproval.getCreative().getId(), existingApproval);
        }

        for (Map.Entry<Integer, SiteCreativeApprovalOperation> entry : toCheckConstraints.entrySet()) {
            SiteCreativeApprovalOperation o = entry.getValue();
            ValidationContext subContext = context.subContext(o)
                    .withPath("operation")
                    .withIndex(entry.getKey())
                    .build();

            SiteCreativeApprovalTO existingApproval = existingApprovalsById.get(o.getCreative().getId());
            validateVersion(subContext, o, existingApproval);
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, Long siteId, SiteCreativeApprovalOperation o) {
        Site site = em.find(Site.class, siteId);

        if (!validateCreative(context, o)) {
            return;
        }
        validateApprovalFields(context, o);

        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(site.getId());
        selector.setCreativeIds(Collections.singleton(o.getCreative().getId()));
        selector.setPaging(new Paging(0, MAX_OPERATIONS));
        PartialList<SiteCreativeApprovalTO> existingApprovals = creativeApprovalService.searchCreativeApprovals(selector);

        validateVersion(context, o, existingApprovals.isEmpty() ? null : existingApprovals.get(0));
    }

    private void validateVersion(ValidationContext context, SiteCreativeApprovalOperation o, SiteCreativeApprovalTO existingApproval) {
        if (existingApproval == null) {
            context.addConstraintViolation("site.creativesApproval.error.noApproveOrReject")
                    .withPath("version")
                    .withValue(o.getCreativeId());
            return;
        }

        if (o.getPreviousStatus() != null && o.getPreviousStatus() != existingApproval.getApprovalStatus()) {
            context.addConstraintViolation("errors.field.version")
                    .withPath("version");
            return;
        }

        if (o.getVersion() != null) {
            if (!ObjectUtils.equals(o.getVersion(), existingApproval.getVersion())) {
                context.addConstraintViolation("errors.field.version")
                        .withPath("version");
            }
        }
    }

    private boolean validateCreative(ValidationContext context, SiteCreativeApprovalOperation o) {
        if (o.getCreative() == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("creative");
            return false;
        }

        if (o.getCreative().getId() == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("creative.id");
            return false;
        }
        return true;
    }

    private void validateApprovalFields(ValidationContext subContext, SiteCreativeApprovalOperation o) {
        if (o.getType() == null) {
            subContext.addConstraintViolation("errors.field.required")
                    .withPath("type");
        } else {
            switch (o.getType()) {
                case APPROVE:
                    if (o.getFeedback() != null) {
                        subContext.addConstraintViolation("errors.field.null")
                                .withPath("feedback");
                    }
                    if (o.getRejectReason() != null) {
                        subContext.addConstraintViolation("errors.field.null")
                                .withPath("rejectReason");
                    }
                    break;
                case REJECT:
                    if (o.getRejectReason() == null) {
                        subContext.addConstraintViolation("errors.field.required")
                                .withPath("rejectReason");
                    }
                    subContext.validator(StringValidator.class)
                            .withSize(2000)
                            .withPath("feedback")
                            .validate(o.getFeedback());
                    break;
                case RESET:
                    if (o.getFeedback() != null) {
                        subContext.addConstraintViolation("errors.field.null")
                                .withPath("feedback");
                    }
                    if (o.getRejectReason() != null) {
                        subContext.addConstraintViolation("errors.field.null")
                                .withPath("rejectReason");
                    }
                    break;
            }
        }
    }

    private static class SiteCreativeApprovalOperationIdentifierFetcher implements DuplicateChecker.IdentifierFetcher<SiteCreativeApprovalOperation> {
        @Override
        public Object fetch(SiteCreativeApprovalOperation operation) {
            return operation.getCreative().getId();
        }
    }
}
