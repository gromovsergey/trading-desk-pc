package com.foros.session.site;

import com.foros.model.ExtensionProperty;
import com.foros.model.site.Site;
import com.foros.session.UploadContext;

import java.util.List;

import javax.ejb.Local;

/**
 * This service deals as a "Facade" for bulk sites update or create.
 * It executes relative TagService, SiteService or CreativeService in a sigle transaction, as well as relays on
 * their security checks.
 */
@Local
public interface SiteUploadService {

    ExtensionProperty<UploadContext> UPLOAD_CONTEXT = new ExtensionProperty<UploadContext>(UploadContext.class);

    public static final ExtensionProperty<String[]> ORIGINAL_VALUES = new ExtensionProperty<String[]>(String[].class);

    /**
     * Updates and creates sites passed as a parameter.
     * Sites must have valid name and account to be successfully created.
     * Site must have valid Id, to be successfully updated.
     */
    void createOrUpdateAll(String validationResultId);

    SiteUploadValidationResultTO validateAll(List<Site> siteList);

    List<Site> fetchValidatedSites(String validationResultId);

    String saveResults(List<Site> sites);
}
