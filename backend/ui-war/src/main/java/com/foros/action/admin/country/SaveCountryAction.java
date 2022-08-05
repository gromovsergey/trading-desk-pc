package com.foros.action.admin.country;

import com.foros.action.Invalidable;
import com.foros.action.Refreshable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.CustomFileUploadInterceptor;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Country;
import com.foros.model.site.CategoryTO;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteCategory;
import com.foros.session.fileman.FileContentException;
import com.foros.session.fileman.FileSizeException;
import com.foros.util.BeanUtils;
import com.foros.util.Schema;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;
import com.foros.util.url.URLValidator;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.RequestAware;

public class SaveCountryAction extends EditCountryActionSupport implements Refreshable, RequestAware, Invalidable, BreadcrumbsSupport {
    private Map<String, Object> request;

    @Validations(
        customValidators = {
            @CustomValidator(type = "fileLength", fieldName = "invoiceRptFile", key = "errors.fileEmptyNotExist")
        },
        conversionErrorFields = {
            @ConversionErrorFieldValidator(fieldName = "defaultPaymentTerms", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "highChannelThreshold", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "lowChannelThreshold", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "minUrlTriggerThreshold", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "maxUrlTriggerShareView", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "sortOrder", key = "errors.field.integer"),
            @ConversionErrorFieldValidator(fieldName = "defaultVATRateView", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "defaultAgencyCommissionView", key = "errors.field.number"),
            @ConversionErrorFieldValidator(fieldName = "minRequiredTagVisibility", key = "errors.field.integer")
        }
    )
    public String save() throws Exception {
        preSave();

        // validate site and content categories
        validateCategories();
        if (!getFieldErrors().isEmpty()) {
            prepareConfigValues();
            return INPUT;
        }

        country.setAddressFields(getAddressFieldsList());
        country.setContentCategories(buildContentCategories());
        country.setSiteCategories(buildSiteCategories());

        try {
            countryService.update(country, getInvoiceRptFile());
        } catch (Exception ex) {
            prepareConfigValues();

            if (ex instanceof FileContentException) {
                addFieldError("invoiceRptFile", getText("errors.file.invalidContent"));
                return INPUT;
            }

            throw ex;
        }

        return SUCCESS;
    }

    private void preSave() {
        // remove null categories
        trimCategoryLists();

        if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
            throw new FileSizeException();
        }

        preSaveURLs();
    }

    private void preSaveURLs() {
        if (StringUtils.isEmpty(country.getAdservingDomain()) || country.getAdservingDomain().equals(getDefaultAdservingDomain())) {
            country.setAdservingDomain(null);
        } else {
            country.setAdservingDomain(UrlUtil.stripUrl(country.getAdservingDomain()));
        }

        if (StringUtils.isEmpty(country.getDiscoverDomain()) || country.getDiscoverDomain().equals(getDefaultDiscoverDomain())) {
            country.setDiscoverDomain(null);
        } else {
            country.setDiscoverDomain(UrlUtil.stripUrl(country.getDiscoverDomain()));
        }

        if (StringUtils.isEmpty(country.getStaticDomain()) || country.getStaticDomain().equals(getDefaultStaticDomain())) {
            country.setStaticDomain(null);
        } else {
            country.setStaticDomain(UrlUtil.stripUrl(country.getStaticDomain()));
        }

        country.setAdTagDomain(UrlUtil.stripUrl(country.getAdTagDomain()));
        country.setConversionTagDomain(UrlUtil.stripUrl(country.getConversionTagDomain()));
    }

    @Override
    public void needRefresh() {
        String countryCode = country.getCountryCode();

        country = countryService.find(countryCode);
        setAddressFieldsList(countryService.getAddressFields(countryCode));
        setContentCategoryTOList(countryService.findForEditContentCategories(getEntity()));
        setSiteCategoryTOList(countryService.findForEditSiteCategories(getEntity()));
        Collections.sort(getAddressFieldsList());
    }

    private void validateCategories() {
        validateCategories(getContentCategoryTOList(), "contentCategory");
        validateCategories(getSiteCategoryTOList(), "siteCategory");

        retainLinkedContentCategories();
        retainLinkedSiteCategories();
    }

    private void validateCategories(List<CategoryTO> categories, String name) {
        Set<String> categoryNames = new HashSet<String>();

        for (int errorIndex = 0; errorIndex < categories.size(); errorIndex++) {
            CategoryTO category = categories.get(errorIndex);
            
            if (StringUtil.isPropertyEmpty(category.getName())) {
                // name should not be empty, add error message
                addFieldError(name + "[" + errorIndex + "].name", getText("errors.field.required"));
            } else if (category.getName().length() > 200) {
                addFieldError(name + "[" + errorIndex + "].name", getText("errors.field.maxlength", new String[] {"200"}));
            } else {
                // duplicate category check to show user friendly message on UI
                if (!categoryNames.add(category.getName())) {
                    addFieldError(name + "[" + errorIndex + "].name", getText("errors.duplicate", new String[]{getText("Country.category.name")}));
                }
            }
        }
    }

    private Set<ContentCategory> buildContentCategories() throws Exception {
        Set<ContentCategory> categories = new HashSet<ContentCategory>(getContentCategoryTOList().size());

        for (CategoryTO category : getContentCategoryTOList()) {
            ContentCategory categoryEntity = new ContentCategory();

            BeanUtils.copyProperties(categoryEntity, category, null);
            categoryEntity.setCountry(country);
            categories.add(categoryEntity);
        }

        return categories;
    }

    private Set<SiteCategory> buildSiteCategories() throws Exception {
        Set<SiteCategory> categories = new HashSet<SiteCategory>(getSiteCategoryTOList().size());

        for (CategoryTO category : getSiteCategoryTOList()) {
            SiteCategory categoryEntity = new SiteCategory();

            BeanUtils.copyProperties(categoryEntity, category, null);
            categoryEntity.setCountry(country);
            categories.add(categoryEntity);
        }

        return categories;
    }

    private void trimCategoryLists() {
        Iterator<CategoryTO> iterator = getContentCategoryTOList().iterator();

        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }

        iterator = getSiteCategoryTOList().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
    }

    private void retainLinkedSiteCategories() {
        List<CategoryTO> existingCategories = countryService.findForEditSiteCategories(getEntity());
        List<CategoryTO> removedCategories = new ArrayList<CategoryTO>();

        for (CategoryTO category : existingCategories) {
            if (!getSiteCategoryTOList().contains(category)) {
                removedCategories.add(category);
            }
        }

        // add back the removed site categories to input
        for (CategoryTO category : removedCategories) {
            if (category.isDependencyExists()) {
                getSiteCategoryTOList().add(category);
                addFieldError("siteCategory[" + getSiteCategoryTOList().indexOf(category) + "].name",
                        getText("Country.category.used", new String[] {getText("Country.site.category")}));
            }
        }
    }

    private void retainLinkedContentCategories() {
        List<CategoryTO> contentCategories = countryService.findForEditContentCategories(getEntity());
        List<CategoryTO> removedCategories = new ArrayList<CategoryTO>();

        for (CategoryTO category : contentCategories) {
            if (!getContentCategoryTOList().contains(category)) {
                removedCategories.add(category);
            }
        }

        // add back the removed content categories to input
        for (CategoryTO category : removedCategories) {
            if (category.isDependencyExists()) {
                getContentCategoryTOList().add(category);
                addFieldError("contentCategory[" + getContentCategoryTOList().indexOf(category) + "].name",
                        getText("Country.category.used", new String[] {getText("Country.content.category")}));
            }
        }
    }

    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    @Override
    public void validate() {
        super.validate();

        if (StringUtils.isNotEmpty(country.getAdservingDomain()) && !URLValidator.isValid(Schema.HTTP.getValue() + country.getAdservingDomain())) {
            addFieldError("adservingDomain", getText("errors.field.invalidDomain"));
        }

        if (StringUtils.isNotEmpty(country.getDiscoverDomain()) && !URLValidator.isValid(Schema.HTTP.getValue() + country.getDiscoverDomain())) {
            addFieldError("discoverDomain", getText("errors.field.invalidDomain"));
        }

        if (StringUtils.isNotEmpty(country.getStaticDomain()) && !URLValidator.isValid(Schema.HTTP.getValue() + country.getStaticDomain())) {
            addFieldError("staticDomain", getText("errors.field.invalidDomain"));
        }

        if (StringUtils.isNotEmpty(country.getAdTagDomain()) && !URLValidator.isValid(Schema.HTTP.getValue() + country.getAdTagDomain())) {
            addFieldError("adTagDomain", getText("errors.field.invalidDomain"));
        }

        if (StringUtils.isNotEmpty(country.getConversionTagDomain()) && !URLValidator.isValid(Schema.HTTP.getValue() + country.getConversionTagDomain())) {
            addFieldError("conversionTagDomain", getText("errors.field.invalidDomain"));
        }
    }

    @Override
    public void invalid() throws Exception {
        prepareConfigValues();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (id != null) {
            Country persistent = countryService.find(id);
            breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }

        return breadcrumbs;
    }
}
