package com.foros.session.site.creativeApproval;

import com.foros.model.site.ThirdPartyCreative;
import com.foros.session.BeanValidations;
import com.foros.session.bulk.Paging;
import com.foros.session.query.PartialList;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class ThirdPartyCreativeValidations {
    private static final int MAX_OPERATIONS = 500;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private SiteCreativeApprovalService creativeApprovalService;

    @Validation
    public void validatePerform(ValidationContext context, ThirdPartyCreativesUpdateOperations operations) {
        List<ThirdPartyCreative> thirdPartyCreatives = operations.getThirdPartyCreatives();
        if (thirdPartyCreatives == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("thirdPartyCreatives");
            return;
        }

        if (thirdPartyCreatives.size() > MAX_OPERATIONS) {
            context.addConstraintViolation("errors.operations.count.max")
                    .withParameters(MAX_OPERATIONS)
                    .withPath("thirdPartyCreatives");
            return;
        }

        DuplicateChecker<ThirdPartyCreative> duplicateChecker = DuplicateChecker
                .create(new ThirdPartyCreativeIdentifierFetcher())
                .withTemplate("errors.duplicate.id");

        Integer index = 0;

        for (ThirdPartyCreative entity : thirdPartyCreatives) {
            ValidationContext subContext = context.subContext(entity)
                    .withPath("thirdPartyCreative")
                    .withIndex(index++)
                    .build();

            if (entity == null) {
                subContext.addConstraintViolation("errors.field.required");
                continue;
            }

            if (!duplicateChecker.check(subContext, "", entity)) {
                continue;
            }

            beanValidations.validateBean(subContext, "", entity, ValidationMode.DEFAULT);
        }

        validateExistence(context, operations.getSiteId(), duplicateChecker.<Long>getCheckedIdentifiers());
    }

    private void validateExistence(ValidationContext context, Long siteId, Set<Long> creativeIds) {
        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(siteId);
        selector.setCreativeIds(creativeIds);
        selector.setPaging(new Paging(0, MAX_OPERATIONS));
        PartialList<ThirdPartyCreative> existingCreatives = creativeApprovalService.searchThirdParty(selector);

        if (existingCreatives.size() == creativeIds.size()) {
            return;
        }

        Set<Long> absentIds = new HashSet<Long>(creativeIds);
        for (ThirdPartyCreative existingCreative : existingCreatives) {
            absentIds.remove(existingCreative.getCreativeId());
        }

        context.addConstraintViolation("errors.thirdPartyCreative.notFound")
                .withPath("thirdPartyCreatives")
                .withValue(absentIds);
    }

    private static class ThirdPartyCreativeIdentifierFetcher implements DuplicateChecker.IdentifierFetcher<ThirdPartyCreative> {
        @Override
        public Object fetch(ThirdPartyCreative entity) {
            return entity.getCreativeId();
        }
    }
}
