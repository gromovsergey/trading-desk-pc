package com.foros.action.admin.country.placementsBlacklist;

import com.opensymphony.xwork2.util.CreateIfNull;
import com.foros.action.BaseActionSupport;
import com.foros.action.admin.country.CountriesBreadcrumbsElement;
import com.foros.action.admin.country.CountryBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Country;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.PlacementsBlacklistValidationResultTO;
import com.foros.session.channel.service.PlacementsBlacklistService;

import javax.ejb.EJB;
import java.security.AccessControlException;

public class BaseUploadPlacementsBlacklistAction extends BaseActionSupport implements BreadcrumbsSupport {

    @CreateIfNull
    protected PlacementsBlacklistValidationResultTO validationResult;
    private Breadcrumbs breadcrumbs;
    private String id;
    private Country country;
    private BulkFormat format = BulkFormat.CSV;
    private boolean alreadySubmitted = false;

    @EJB
    private CountryService countryService;

    @EJB
    protected PlacementsBlacklistService placementsBlacklistService;

    public String getId() {
        return id;
    }

    public void setId(String countryCode) {
        this.id = countryCode;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    public Country getCountry() {
        if (country == null && getId() != null) {
            country = countryService.find(getId());
        }
        if (country == null) {
            throw new AccessControlException("Valid country id is required");
        }
        return country;
    }

    public boolean isAlreadySubmitted() {
        return alreadySubmitted;
    }

    public void setAlreadySubmitted(boolean alreadySubmitted) {
        this.alreadySubmitted = alreadySubmitted;
    }

    public PlacementsBlacklistValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(PlacementsBlacklistValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    public Breadcrumbs getBreadcrumbs() {
        if (breadcrumbs == null) {
            breadcrumbs = new Breadcrumbs()
                    .add(new CountriesBreadcrumbsElement())
                    .add(new CountryBreadcrumbsElement(getCountry()))
                    .add(new EntityPlacementsBlacklistBreadcrumbsElement(getCountry()))
                    .add(new PlacementsBlacklistBulkUploadBreadcrumbsElement());
        }
        return breadcrumbs;
    }
}
