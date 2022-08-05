package com.foros.action.admin.country;

import com.foros.framework.ReadOnly;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.ContentSourceSupport;

import com.foros.session.birt.BirtReportService;

import java.io.File;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public class DownloadCountryAction extends CountryActionSupport {

    private String id;
    private String targetFile;
    private ContentSource contentSource;

    @EJB
    private BirtReportService reportService;

    @ReadOnly
    public String download() {
        if (getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        country = countryService.find(getId());
        String fullTemplatePath = reportService.getFullTemplatePath(country.getInvoiceReport());
        contentSource = ContentSourceSupport.create(new File(fullTemplatePath));
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }
}
