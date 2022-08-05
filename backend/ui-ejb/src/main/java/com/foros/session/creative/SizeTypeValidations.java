package com.foros.session.creative;

import com.foros.model.creative.SizeType;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class SizeTypeValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) SizeType sizeType) {
        //@ValidateBean do everything we need
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) SizeType sizeType) {
        SizeType existing = em.find(SizeType.class, sizeType.getId());
        validateMultipleSizes(context, sizeType, existing);
        validateAdvertiserSizeSelection(context, sizeType, existing);
    }

    private void validateMultipleSizes(ValidationContext context, SizeType sizeType, SizeType existing) {
        if (context.hasViolation("multipleSizes")) {
            return;
        }

        if (existing.getMultipleSizes() == sizeType.getMultipleSizes()) {
            return;
        }

        if (existing.getMultipleSizes() == SizeType.MultipleSizes.ONE_SIZE) {
            // no restrictions on switching ONE_SIZE -> MULTIPLE_SIZES
            return;
        }

        boolean hasBadTags = (boolean) em.createNativeQuery(
                "select exists (" +
                "  select t.tag_id, count(ts.size_id) " +
                "  from Tags t left join Tag_TagSize ts on t.tag_id = ts.tag_id " +
                "  where t.size_type_id = :size_type_id " +
                "  group by t.tag_id " +
                "  having count(ts.size_id) <> 1 " +
                ")")
                .setParameter("size_type_id", sizeType.getId())
                .getSingleResult();

        if (hasBadTags) {
            context.addConstraintViolation("SizeType.errors.cantChangeMultipleSizes")
                .withPath("multipleSizes");
        }
    }

    private void validateAdvertiserSizeSelection(ValidationContext context, SizeType sizeType, SizeType existing) {
        if (context.hasViolation("advertiserSizeSelection")) {
            return;
        }

        if (existing.getAdvertiserSizeSelection() == sizeType.getAdvertiserSizeSelection()) {
            return;
        }

        if (existing.getAdvertiserSizeSelection() == SizeType.AdvertiserSizeSelection.TYPE_LEVEL) {
            // no restrictions on switching TYPE_LEVEL -> TYPE_AND_SIZE_LEVEL
            return;
        }

        boolean hasBadCreatives = (boolean) em.createNativeQuery(
                "select exists ( " +
                "  select 1 from Creative_TagSize cts " +
                "  where cts.size_id in ( " +
                "    select cs.size_id from CreativeSize cs where cs.size_type_id = :size_type_id " +
                "  ) " +
                ")")
                .setParameter("size_type_id", sizeType.getId())
                .getSingleResult();

        if (hasBadCreatives) {
            context.addConstraintViolation("SizeType.errors.cantAdvertiserSizeSelection")
                    .withPath("advertiserSizeSelection");

        }
    }
}
