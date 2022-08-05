package com.foros.session.fileman;

import javax.ejb.Local;

@Local
public interface PathProviderService {

    PathProvider getAdminFileManagerFolder();

    PathProvider getOpportunities();

    PathProvider getAccountDocuments();

    PathProvider getChannelReport();

    PathProvider getCreatives();

    PathProvider getTerms();

    PathProvider getTemplates();

    PathProvider getPreview();

    PathProvider getReports();

    PathProvider getReportDocuments();

    PathProvider getTags();

    PathProvider getPublisherAccounts();

    PathProvider getDiscover();

    PathProvider getKwmTool();

    PathProvider getBulkUpload();

    PathProvider getNested(PathProvider pathProvider, String dir);

    PathProvider getNested(PathProvider pathProvider, String dir, OnNoProviderRoot mode);

    FileSystem createFileSystem(PathProvider pathProvider);
}
