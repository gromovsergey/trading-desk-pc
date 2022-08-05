package com.foros.session.creative;

import static com.foros.model.creative.CreativeSizeExpansion.DOWN;
import static com.foros.model.creative.CreativeSizeExpansion.LEFT;
import static com.foros.model.creative.CreativeSizeExpansion.RIGHT;
import static com.foros.model.creative.CreativeSizeExpansion.UP;

import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.util.CollectionUtils;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.Collection;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class CreativeSizeValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CreativeSizeService creativeSizeService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) CreativeSize creativeSize) {
        validateSizes(context, creativeSize);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) CreativeSize creativeSize) {
        validateSizes(context, creativeSize);
    }

    private void validateSizes(ValidationContext context, CreativeSize creativeSize) {
        boolean allNull = (creativeSize.getMaxHeight() == null && creativeSize.getMaxWidth() == null && creativeSize.getHeight() == null && creativeSize.getWidth() == null);

        if (!allNull) {
            validateRequiredField(context, creativeSize.getWidth(), "{CreativeSize.width}", "width");
            validateRequiredField(context, creativeSize.getMaxWidth(), "{CreativeSize.maxWidth}", "maxWidth");
            validateRequiredField(context, creativeSize.getHeight(), "{CreativeSize.height}", "height");
            validateRequiredField(context, creativeSize.getMaxHeight(), "{CreativeSize.maxHeight}", "maxHeight");

            if (!(context.hasViolation("width") || context.hasViolation("maxWidth"))) {
                if (creativeSize.getMaxWidth() < creativeSize.getWidth()) {
                    context.addConstraintViolation("errors.less")
                            .withParameters("{CreativeSize.maxWidth}", "{CreativeSize.width}")
                            .withPath("maxWidth");
                }
            }

            if (!(context.hasViolation("height") || context.hasViolation("maxHeight"))) {
                if (creativeSize.getMaxHeight() < creativeSize.getHeight()) {
                    context.addConstraintViolation("errors.less")
                            .withParameters("{CreativeSize.maxHeight}", "{CreativeSize.height}")
                            .withPath("maxHeight");
                }
            }
        } else if (!creativeSize.getExpansions().isEmpty()) {
            context.addConstraintViolation("errors.invalid").withPath("expansions").withParameters("{CreativeSize.expansions.label}");
        }

        if (context.hasViolations()) {
            return;
        }

        if (!allNull) {

            long height = creativeSize.getHeight();
            long width = creativeSize.getWidth();
            long maxHeight = creativeSize.getMaxHeight();
            long maxWidth = creativeSize.getMaxWidth();

            Set<CreativeSizeExpansion> directions = creativeSize.getExpansions();

            boolean fixedHeight = maxHeight == height;
            boolean fixedWidth = maxWidth == width;

            // if expandable and there is no directions
            if (directions.isEmpty() && (!fixedHeight || !fixedWidth)) {
                context.addConstraintViolation("errors.field.required").withPath("expansions");
            }
            for (CreativeSizeExpansion dir : directions) {
                // if expandable vertically and dir not in (UP, DOWN) or
                // expandable horizontally and dir not in (UP, DOWN) or
                // not expandable and dir is not empty
                if (fixedWidth && !fixedHeight && dir != UP && dir != DOWN || !fixedWidth && fixedHeight && dir != LEFT
                        && dir != RIGHT || fixedHeight && fixedWidth) {
                    if (!context.hasViolation("expansions")) {
                        context.addConstraintViolation("CreativeSize.error.expansions").withPath("expansions");
                    }
                }
            }
        }

        // Check for tags and creatives which have positions related to size expansions
        if (creativeSize.getId() != null ) {
            CreativeSize existing = em.find(CreativeSize.class, creativeSize.getId());

            if (!context.hasViolation("expansions")) {
                if (!existing.getExpansions().equals(creativeSize.getExpansions())) {
                    Collection<CreativeSizeExpansion> removed = CollectionUtils.subtract(existing.getExpansions(), creativeSize.getExpansions());

                    if (!removed.isEmpty()) {
                        long usedTags = creativeSizeService.countExpandableTags(creativeSize.getId());
                        if (usedTags > 0) {
                            context.addConstraintViolation("CreativeSize.error.tagPosition").withPath("expansions").withParameters(usedTags);
                        }

                        long usedCreatives = creativeSizeService.countCreativesByExpansions(creativeSize.getId(), removed);
                        if (usedCreatives > 0) {
                            context.addConstraintViolation("CreativeSize.error.creativeExpansion").withPath("expansions").withParameters(usedCreatives);
                        }
                    }
                }
            }

            if (!allNull && context.props("maxHeight", "maxWidth").reachableAndNoViolations() && creativeSizeService.countExpandableCreatives(creativeSize.getId()) > 0L) {
                if (existing.getMaxHeight() > creativeSize.getMaxHeight()) {
                    context.addConstraintViolation("errors.less").withParameters("{CreativeSize.maxHeight}", existing.getMaxHeight()).withPath("maxHeight");
                }

                if (existing.getMaxWidth() > creativeSize.getMaxWidth()) {
                    context.addConstraintViolation("errors.less").withParameters("{CreativeSize.maxWidth}", existing.getMaxWidth()).withPath("maxWidth");
                }
            }
        }
    }

    private void validateRequiredField(ValidationContext context, Long value, String parameterResource, String path) {
        if (value == null) {
            context.addConstraintViolation("errors.required")
                    .withParameters(parameterResource)
                    .withPath(path);
        }
    }

}
