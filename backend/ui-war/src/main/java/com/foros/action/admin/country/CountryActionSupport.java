package com.foros.action.admin.country;

import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.site.CategoryTO;
import com.foros.session.admin.country.CountryService;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.session.birt.BirtReportService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

class CountryActionSupport extends BaseActionSupport implements ModelDriven<Country> {

    @EJB
    protected CountryService countryService;

    @EJB
    private ConfigService configService;

    @EJB
    private BirtReportService reportService;

    protected Country country = new Country();
    private File invoiceRptFile;
    private String invoiceRptFileFileName;

    private List<AddressField> addressFieldsList;

    private List<CategoryTO> siteCategoryTOList = new ArrayList<CategoryTO>();
    private List<CategoryTO> contentCategoryTOList = new ArrayList<CategoryTO>();

    public List<CategoryTO> getSiteCategoryTOList() {
        return siteCategoryTOList;
    }

    public void setSiteCategoryTOList(List<CategoryTO> siteCategoryTOList) {
        this.siteCategoryTOList = siteCategoryTOList;
    }

    public List<CategoryTO> getContentCategoryTOList() {
        return contentCategoryTOList;
    }

    public void setContentCategoryTOList(List<CategoryTO> contentCategoryTOList) {
        this.contentCategoryTOList = contentCategoryTOList;
    }

    public String getDefaultAdservingDomain() {
        return configService.get(ConfigParameters.DEFAULT_ADSERVING_DOMAIN);
    }

    public String getDefaultDiscoverDomain() {
        return configService.get(ConfigParameters.DEFAULT_DISCOVER_DOMAIN);
    }

    public String getDefaultStaticDomain() {
        return configService.get(ConfigParameters.DEFAULT_STATIC_DOMAIN);
    }

    @Override
    public Country getModel() {
        return country;
    }

    public Country getEntity() {
        return country;
    }

    public void setEntity(Country country) {
        this.country = country;
    }

    public List<AddressField> getAddressFieldsList() {
        return addressFieldsList;
    }

    public void setAddressFieldsList(List<AddressField> addressFieldsList) {
        this.addressFieldsList = addressFieldsList;
    }

    public File getInvoiceRptFile() {
        return invoiceRptFile;
    }

    public void setInvoiceRptFile(File invoiceRptFile) {
        this.invoiceRptFile = invoiceRptFile;
    }

    public void setInvoiceRptFileFileName(String invoiceRptFileFileName) {
        this.invoiceRptFileFileName = invoiceRptFileFileName;
    }

    public String getInvoiceRptFileFileName() {
        return invoiceRptFileFileName;
    }

    protected void prepareConfigValues() {
        prepareConfigAdServerDomain();
        prepareConfigDiscoverDomain();
        prepareConfigStaticDomain();
    }

    protected void prepareConfigAdServerDomain() {
        if (StringUtils.isEmpty(country.getAdservingDomain())) {
            country.setAdservingDomain(getDefaultAdservingDomain());
        }
    }

    protected void prepareConfigDiscoverDomain() {
        if (StringUtils.isEmpty(country.getDiscoverDomain())) {
            country.setDiscoverDomain(getDefaultDiscoverDomain());
        }
    }

    protected void prepareConfigStaticDomain() {
        if (StringUtils.isEmpty(country.getStaticDomain())) {
            country.setStaticDomain(getDefaultStaticDomain());
        }
    }

    protected void prepareAddressAndFile() {
        BirtReport invoiceReport = country.getInvoiceReport();

        setInvoiceRptFile(invoiceReport != null ?
                new File(reportService.getFullTemplatePath(invoiceReport)) : null);

        setAddressFieldsList(countryService.getAddressFields(country.getCountryCode()));
        Collections.sort(getAddressFieldsList());

        // load site/content categories
        setSiteCategoryTOList(countryService.findForEditSiteCategories(country));
        setContentCategoryTOList(countryService.findForEditContentCategories(country));

    }
}
