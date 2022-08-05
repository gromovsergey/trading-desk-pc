package com.foros.action.admin.accountType;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateTO;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.util.CollectionUtils;
import com.foros.util.comparator.LocalizableTOComparator;
import com.foros.util.mapper.Converter;

import java.util.ArrayList;
import java.util.Collections;

import javax.persistence.EntityNotFoundException;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "maxKeywordLength", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxUrlLength", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxKeywordsPerGroup", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxKeywordsPerChannel", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "maxUrlsPerChannel", key = "errors.field.integer")
        }
)
public class EditAccountTypeAction extends AccountTypeSupportAction implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction = "AccountType.create")
    public String create() {
        if (getEntity().getAccountRole() == null ){
            getEntity().setAccountRole(AccountRole.INTERNAL);
        }
        populateUIControls();
        setDefaults();
        breadcrumbs = new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AccountType.update")
    public String edit() {
        if (entity.getId() == null) {
            throw new EntityNotFoundException("Account Type with id = null not found");
        }
        setChannelChecks(null);
        setCampaignChecks(null);
        entity = service.view(entity.getId());
        populateUIControls();
        prepareSizeTemplateLists();
        breadcrumbs = new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(new AccountTypeBreadcrumbsElement(entity)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    private void setDefaults() {
        AccountRole role = entity.getAccountRole();
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY) {
            entity.setMaxKeywordLength(100L);
            entity.setMaxUrlLength(1000L);
            entity.setMaxKeywordsPerGroup(2000L);
            entity.setMaxKeywordsPerChannel(2000L);
            entity.setMaxUrlsPerChannel(2000L);
            entity.setIoManagement(Boolean.FALSE);
        } else if (role == AccountRole.CMP) {
            entity.setMaxKeywordLength(100L);
            entity.setMaxUrlLength(1000L);
            entity.setMaxKeywordsPerChannel(2000L);
            entity.setMaxUrlsPerChannel(2000L);
        } else if (role == AccountRole.PUBLISHER) {
            entity.setShowIframeTag(Boolean.FALSE);
            entity.setShowBrowserPassbackTag(Boolean.FALSE);
            entity.setAdvExclusions(AdvExclusionsType.DISABLED);
        }
    }

    private void prepareSizeTemplateLists() {
        AccountRole role = entity.getAccountRole();
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY) {
            creativeTemplateList = new ArrayList<TemplateTO>(CollectionUtils.convert(new Converter<Template, TemplateTO>() {
                @Override
                public TemplateTO item(Template t) {
                    return new TemplateTO(t);
                }
            }, entity.getTemplates()));
            Collections.sort(creativeTemplateList, new LocalizableTOComparator<TemplateTO>());
        } else if (role == AccountRole.PUBLISHER) {
            discoverTemplateList = new ArrayList<TemplateTO>();
            for (Template t : entity.getTemplates()) {
                discoverTemplateList.add(new TemplateTO(t));
            }
            Collections.sort(discoverTemplateList, new LocalizableTOComparator<TemplateTO>());
        }

        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY || role == AccountRole.PUBLISHER) {
            creativeSizeList = new ArrayList<CreativeSizeTO>(CollectionUtils.convert(new Converter<CreativeSize, CreativeSizeTO>() {
                @Override
                public CreativeSizeTO item(CreativeSize cs) {
                    return new CreativeSizeTO(cs);
                }
            }, entity.getCreativeSizes()));
            Collections.sort(creativeSizeList, new LocalizableTOComparator<CreativeSizeTO>());
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
