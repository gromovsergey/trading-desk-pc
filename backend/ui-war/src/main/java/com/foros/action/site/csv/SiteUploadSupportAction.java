package com.foros.action.site.csv;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_UPLOAD_SIZE;
import static com.foros.config.ConfigParameters.SITE_CSV_UPLOAD_MAX_ROW_COUNT;
import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigService;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.session.site.SiteService;
import com.foros.session.site.SiteUploadService;
import com.foros.session.site.SiteUploadValidationResultTO;
import com.foros.util.context.RequestContexts;

import java.io.File;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public abstract class SiteUploadSupportAction extends BaseActionSupport implements RequestContextsAware, PublisherSelfIdAware {

    private static final String SUCCESS_PUBLISHERS = "successPublishers";
    private static final String INPUT_PUBLISHERS = "inputPublishers";

    protected Long publisherId;
    protected File fileToUpload;
    private SiteUploadValidationResultTO validationResult = new SiteUploadValidationResultTO();
    private boolean uploadSubmitted = false;
    private Boolean isInternalMode;

    @EJB
    private SiteService siteService;

    @EJB
    protected SiteUploadService siteUploadService;

    @EJB
    private ConfigService configService;

    @Override
    public void switchContext(RequestContexts contexts) {
        if (publisherId != null) {
            contexts.getPublisherContext().switchTo(publisherId);
        }
    }

    public File getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(File fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public SiteUploadValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(SiteUploadValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    public boolean isUploadSubmitted() {
        return uploadSubmitted;
    }

    public void setUploadSubmitted(boolean uploadSubmitted) {
        this.uploadSubmitted = uploadSubmitted;
    }

    public int getMaxFileUploadSizeInMb() {
        return configService.get(DEFAULT_MAX_UPLOAD_SIZE) / (1024 * 1024);
    }

    public Integer getMaxRowCount() {
        return configService.get(SITE_CSV_UPLOAD_MAX_ROW_COUNT);
    }

    public Long getPublisherId() {
        return publisherId;
    }

    @Override
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    protected void handleError(Exception cause) throws Exception {
        if (cause instanceof SiteParserException) {
            SiteParserException pex = (SiteParserException) cause;
            addFieldError("fileToUpload", getText(pex.getKey()));
        } else if (cause instanceof EntityNotFoundException) {
            addFieldError("error",  getText("emtpy.message", new String[]{cause.getMessage()}));
        } else {
            throw cause;
        }
    }

    public boolean isInternalMode() {
        if (isInternalMode == null) {
            isInternalMode = isInternal() && getPublisherId() == null;
        }
        return isInternalMode;
    }

    protected String getInputResultName() {
        if (isInternalMode()) {
            return INPUT_PUBLISHERS;
        } else {
            return INPUT;
        }
    }

    protected String getSuccessResultName() {
        if (isInternalMode()) {
            return SUCCESS_PUBLISHERS;
        } else {
            return SUCCESS;
        }
    }

}
