package com.foros.action.site.csv;

import com.foros.framework.CustomFileUploadInterceptor;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.ExceptionUtil;
import com.foros.util.StringUtil;
import com.foros.util.csv.CsvFormatException;
import com.foros.validation.ValidationService;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.RequestAware;

public class SiteCSVProcessorAction extends SiteUploadSupportAction implements RequestAware {

    private Map<String, Object> request;

    @EJB
    private ValidationService validationService;

    @RequiredFieldValidator(fieldName = "fileToUpload", key = "errors.field.required")
    @Restrict(restriction = "PublisherEntity.upload", parameters = "#target.publisherId")
    public String validateCsv() throws Exception {
        validationService.validate("Site.fileUpload", getFileToUpload()).throwIfHasViolations();
        if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
            throw new UploadSizeExceedException(StringUtil.getLocalizedString("errors.file.sizeExceeded"));
        }

        try {
            List<Site> sitesList = readCsv();
            setValidationResult(siteUploadService.validateAll(sitesList));
        } catch (NullPointerException npe) {
            return getInputResultName();
        } catch (CsvFormatException e) {
            addFieldError("error", e.getMessage());
        } catch (Exception e) {
            handleError(ExceptionUtil.getRootException(e));
        }

        return getSuccessResultName();
    }

    private List<Site> readCsv() throws SiteParserException {
        InputStream is = null;
        try {
            List<Site> sites = new SiteCsvProcessor(CurrentUserSettingsHolder.getLocale(), getMaxRowCount(), isInternalMode())
                    .parse(new FileInputStream(getFileToUpload()));
            if (!isInternalMode()) {
                setAccountToSites(sites);
            }
            return sites;
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void setAccountToSites(List<Site> sites) {
        PublisherAccount publisherAccount = new PublisherAccount(getPublisherId());
        for (Site site : sites) {
            site.setAccount(publisherAccount);
        }
    }

    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }
}
