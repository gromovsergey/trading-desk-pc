package com.foros.action.site;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptGroupState;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.DiscoverTemplate;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;


@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "width", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "height", key = "errors.field.number")
    }
)
public class SaveWDTagAction extends EditWDTagActionBase implements Invalidable, BreadcrumbsSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("optedInUrls", "'optedInUrls'", "urlError(violation.invalidValue, violation.message)")
            .add("optedOutUrls", "'optedOutUrls'", "urlError(violation.invalidValue, violation.message)")
            .rules();

    public String create() {
        prepareSave();
        wdTagService.create(wdTag);
        return SUCCESS;
    }

    public String update() {
        prepareSave();
        if (wdTag.getTemplate().isChanged("id")) {
            wdTag.registerChange("template");
        }
        wdTagService.update(wdTag);
        return SUCCESS;
    }

    public String[] getOptedInUrlsArray() {
        return StringUtil.splitAndTrim(getOptedInUrls());
    }

    public String[] getOptedOutUrlsArray() {
        return StringUtil.splitAndTrim(getOptedOutUrls());
    }

    @Override
    public void validate() {
        if (wdTag.getSite() == null || wdTag.getSite().getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        wdTag.setSite(siteService.view(wdTag.getSite().getId()));
    }

    private void prepareSave() {
        wdTag.setOptedInFeeds(WDTagActionHelper.convertUrls(getOptedInUrlsArray()));
        wdTag.setOptedOutFeeds(WDTagActionHelper.convertUrls(getOptedOutUrlsArray()));

        wdTag.setOptions(new LinkedHashSet<WDTagOptionValue>(getOptionValues().values()));
        wdTag.setGroupStates(new LinkedHashSet<WDTagOptGroupState>(getGroupStateValues().values()));
    }

    @Override
    public void invalid() throws Exception {
        if (hasFieldErrors()) {
            if ((wdTag.getTemplate() != null) &&(wdTag.getTemplate().getId() != null)) {
                wdTag.setTemplate((DiscoverTemplate) templateService.findById(wdTag.getTemplate().getId()));
            }
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (wdTag.getId() != null) {
            final WDTag persistent = wdTagService.find(wdTag.getId());
            breadcrumbs.add(new SiteBreadcrumbsElement(persistent.getSite())).add(new WDTagBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(new SiteBreadcrumbsElement(wdTag.getSite())).add("site.breadcrumbs.createWdTag");

        }
        return breadcrumbs;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String urlError(String value, String message) {
        if (StringUtil.isPropertyNotEmpty(value)) {
            return StringUtil.getLocalizedString("errors.urlError", value, message);
        } else {
            return message;
        }
    }
}
