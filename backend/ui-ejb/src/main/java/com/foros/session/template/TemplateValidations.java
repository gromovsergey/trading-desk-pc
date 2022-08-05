package com.foros.session.template;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.session.BusinessException;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class TemplateValidations {
    @EJB
    private TemplateService service;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private TemplateFileValidations templateFileValidations;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Template template) {
        if (template instanceof DiscoverTemplate) {
            checkTemplateFiles(context, (DiscoverTemplate) template);
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Template template) {
        if (template instanceof DiscoverTemplate) {
            checkTemplateFiles(context, (DiscoverTemplate)template);
        }
        if (template instanceof CreativeTemplate) {
            if (service.isCreativeTemplateLinkedToCreatives(template.getId())) {
                checkVisualCategoriesNotRemoved(context, (CreativeTemplate) template);
            }
            validateExpandable(context, (CreativeTemplate)template);
        }
    }

    private void validateExpandable(ValidationContext context, CreativeTemplate template) {
        if (template.isChanged("expandable") && !template.isExpandable() && service.countExpandableCreatives(template.getId()) > 0L) {
            context.addConstraintViolation("CreativeTemplate.error.expandable").withPath("expandable");
        }
    }

    private void checkVisualCategoriesNotRemoved(ValidationContext context, CreativeTemplate template) {
        if (template.getCategories().size() == 0 ) {
            Set<CreativeCategory> existingCategories = em.getReference(CreativeTemplate.class, template.getId()).getCategories();
            if (existingCategories.size() > 0) {
                context
                        .addConstraintViolation("CreativeTemplate.linked.visualCategory").
                        withPath("categories");
            }
        }
    }

    private void checkTemplateFiles(ValidationContext context, DiscoverTemplate template) {
        boolean tagTemplateFileExists = false;
        boolean customizationTemplateFileExists = false;
        boolean previewTemplateFileExists = false;

        if (template.getTemplateFiles() != null) {
            for (TemplateFile file : template.getTemplateFiles()) {
                int i;

                file.setApplicationFormat(em.getReference(ApplicationFormat.class, file.getApplicationFormat().getId()));

                String appFormatName = file.getApplicationFormat().getName();

                if (appFormatName.equals(ApplicationFormat.DISCOVER_TAG_FORMAT)) {
                    if (!tagTemplateFileExists) {
                        tagTemplateFileExists = true;
                        i = 0;
                    } else {
                        throw new BusinessException("Duplicate files with same application format");
                    }
                } else if (appFormatName.equals(ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT)) {
                    if (!customizationTemplateFileExists) {
                        customizationTemplateFileExists = true;
                        i = 1;
                    } else {
                        throw new BusinessException("Duplicate files with same application format");
                    }
                } else if (appFormatName.equals(ApplicationFormat.PREVIEW_FORMAT)) {
                    if (!previewTemplateFileExists) {
                        previewTemplateFileExists = true;
                        i = 2;
                    } else {
                        throw new BusinessException("Duplicate files with same application format");
                    }
                } else {
                    throw new BusinessException("Unexpected application format: " + appFormatName);
                }

                if (StringUtil.isPropertyEmpty(file.getTemplateFile())) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("files[" + i + "].templateFile");
                } else {
                    templateFileValidations.validateFileContents(context, file, "files[" + i + "].templateFile");
                }
            }
        }

        if (!tagTemplateFileExists || !customizationTemplateFileExists || !previewTemplateFileExists) {
            throw new BusinessException("Not all required template files specified");
        }
    }

}
