package com.foros.session.creative;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.RTBConnector;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class CreativeCategoryValidations {
    public static final Pattern REGEXP = Pattern.compile("^([\\p{L}\\p{M}*\\p{Nd}\\.\\-& ]+)$");

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CreativeCategoryService categoryService;

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) CreativeCategoryEditTO ccTO) {

        Set<String> validatedCategories = new HashSet<String>();
        Set<Long> validatedCategoryIds = new HashSet<Long>();
        List<RTBConnector> rtbConnectors = categoryService.getRTBConnectors();

        for (CreativeCategoryTO category : ccTO.getCategories()) {
            if (category.getName().length() > 100) {
                context
                        .addConstraintViolation("CreativeCategory.errors.tooLong")
                        .withParameters(category.getName())
                        .withPath("categories")
                        .withValue(category.getName());
            }


            for (int i = 0; i < category.getRtbCategories().size(); i++) {
                String value = category.getRtbCategories().get(i);
                if (value != null && value.length() > 50) {
                    context
                        .addConstraintViolation("CreativeCategory.errors.rtbTooLong")
                        .withParameters(rtbConnectors.get(i).getName(), category.getName())
                        .withPath("categories")
                        .withValue(value);
                }
            }

            if (!REGEXP.matcher(category.getName()).matches()) {
                context
                        .addConstraintViolation("CreativeCategory.errors.invalidSymbols")
                        .withParameters(category.getName())
                        .withPath("categories")
                        .withValue(category.getName());
            }

            if (ccTO.getType() == CreativeCategoryType.TAG && !category.getName().equals(category.getName().toLowerCase())) {
                context
                        .addConstraintViolation("errors.invalid")
                        .withParameters(category.getName())
                        .withPath("categories")
                        .withValue(category.getName());
            }

            if (validatedCategories.contains(category.getName())) {
                context
                        .addConstraintViolation("CreativeCategory.errors.duplicateCategory")
                        .withParameters(category.getName())
                        .withPath("categories")
                        .withValue(category.getName());
            }

            if (ccTO.getType() != CreativeCategoryType.TAG && category.getId() != null) {
                CreativeCategory creativeCategory = em.find(CreativeCategory.class, category.getId());
                if (creativeCategory == null) {
                    context
                            .addConstraintViolation("CreativeCategory.errors.categoryIdNotFound")
                            .withParameters(category.getId())
                            .withPath("categories")
                            .withValue(category.getId());
                }

                if (creativeCategory != null && creativeCategory.getType() != ccTO.getType()) {
                    context
                            .addConstraintViolation("CreativeCategory.errors.categoryIdWrongType")
                            .withParameters(category.getId())
                            .withPath("categories")
                            .withValue(category.getId());
                }

                if (validatedCategoryIds.contains(category.getId())) {
                    context
                            .addConstraintViolation("CreativeCategory.errors.duplicateCategoryId")
                            .withParameters(category.getId())
                            .withPath("categories")
                            .withValue(category.getId());
                }

                validatedCategoryIds.add(category.getId());
            }

            validatedCategories.add(category.getName());
        }
    }
}
