package com.foros.action.admin.template.file;

import com.foros.action.admin.template.creative.CreativeTemplateBreadcrumbsElement;
import com.foros.action.admin.template.creative.CreativeTemplatesBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.session.EntityTO;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.EntityUtils;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.util.List;
import java.util.Set;
import javax.ejb.EJB;

public class SaveCreativeTemplateFileAction extends EditCreativeTemplateFileActionSupport implements BreadcrumbsSupport {

    @EJB
    private DisplayCreativeService displayCreativeService;

    private List<EntityTO> sizeLinkedCreatives;

    private List<EntityTO> appFormatLinkedCreatives;

    public SaveCreativeTemplateFileAction() {
        file.setTemplate(new CreativeTemplate());
    }

    public String create() {
        try {
            templateService.createTemplateFile(file);
        } catch (ConstraintViolationException e) {
            handleLinkedUpdate(e.getConstraintViolations());
            throw e;
        }
        return SUCCESS;
    }

    public String update() {
        try {
            templateService.updateTemplateFile(file);
        } catch (ConstraintViolationException e) {
            handleLinkedUpdate(e.getConstraintViolations());
            throw e;
        }
        return SUCCESS;
    }

    private void handleLinkedUpdate(Set<ConstraintViolation> constraintViolations) {
        boolean sizeLinkedUpdate = false;
        boolean appFormatLinkedUpdate = false;
        for (ConstraintViolation constraintViolation : constraintViolations) {
            ForosError error = constraintViolation.getError();
            String path = constraintViolation.getPropertyPath().toString();

            if (error == BusinessErrors.FIELD_CAN_NOT_BE_CHANGED && "creativeSize".equals(path)) {
                sizeLinkedUpdate = true;
            } else if (error == BusinessErrors.FIELD_CAN_NOT_BE_CHANGED && "applicationFormat".equals(path)) {
                appFormatLinkedUpdate = true;
            }
        }
        if (sizeLinkedUpdate || appFormatLinkedUpdate) {
            TemplateFile persistentFile = templateService.findTemplateFileById(file.getId());
            file.setCreativeSize(persistentFile.getCreativeSize());
            file.setApplicationFormat(persistentFile.getApplicationFormat());
            List<EntityTO> linkedCreatives = displayCreativeService.getCreativeSizeOrApplicationFormatLinkedCreatives(persistentFile);
            EntityUtils.applyStatusRules(linkedCreatives, null, false);
            if (sizeLinkedUpdate) {
                sizeLinkedCreatives = linkedCreatives;
            }
            if (appFormatLinkedUpdate) {
                appFormatLinkedCreatives = linkedCreatives;
            }
        }
    }

    public List<EntityTO> getSizeLinkedCreatives() {
        return sizeLinkedCreatives;
    }

    public List<EntityTO> getAppFormatLinkedCreatives() {
        return appFormatLinkedCreatives;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Template template = templateService.findById(file.getTemplate().getId());
        return new Breadcrumbs()
                .add(new CreativeTemplatesBreadcrumbsElement())
                .add(new CreativeTemplateBreadcrumbsElement(template))
                .add(ActionBreadcrumbs.EDIT);
    }
}
