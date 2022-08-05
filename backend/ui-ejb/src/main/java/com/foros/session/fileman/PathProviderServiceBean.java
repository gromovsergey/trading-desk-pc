package com.foros.session.fileman;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import com.foros.config.ConfigParameter;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;

/**
 * <pre>
 *    + Data Root
 *    |
 *    +--+ FM Root
 *    |  |
 *    |  +--+ Creatives Folder
 *    |  +--+ Oracle Finance Data Folder
 *    |  +--+ Reports Folder
 *    |  +--+ Templates Folder
 *    |  +--+ Publisher Accounts Folder
 *    |
 *    +--- Preview Folder
 *    +--- Tags Folder
 * </pre>
 */
@Singleton(name = "PathProviderService")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PathProviderServiceBean implements PathProviderService {
    private static final Logger logger = Logger.getLogger(PathProviderServiceBean.class.getName());

    @EJB
    ConfigService configService;

    private PathProvider root;
    private PathProvider adminFileManagerFolder;
    private PathProvider opportunities;
    private PathProvider accountDocuments;
    private PathProvider channelReport;
    private PathProvider creatives;
    private PathProvider terms;
    private PathProvider templates;
    private PathProvider reports;
    private PathProvider reportDocuments;
    private PathProvider preview;
    private PathProvider tags;
    private PathProvider publisherAccounts;
    private PathProvider discover;
    private PathProvider kwmTool;
    private PathProvider bulkUpload;

    private String fixFolderName(String path) {
        if (java.io.File.separatorChar == '\\') {
            path = path.replace('\\', '/');
        }

        if (!path.endsWith("/")) {
            path += "/";
        }

        return path;
    }

    private PathProvider getFolder(PathProvider root, ConfigParameter<String> parameter, OnNoProviderRoot mode) {
        String folderName = getFolderName(parameter);

        return root.getNested(folderName, mode);
    }

    private String getFolderName(ConfigParameter<String> parameter) {
        String folderName = configService.get(parameter);

        if (folderName == null) {
            logger.log(Level.WARNING, "System property '" + parameter.getName() + "' is not defined");
            throw new NullPointerException("Can't create path provider for "+ parameter.getName() +" folder, check the config.");
        }
        return fixFolderName(folderName);
    }

    @PostConstruct
    private void initialize() {
        // Data Root
        root = createRoot();
        logger.log(Level.INFO, "Root: " + root.toString());

        // FM Root
        adminFileManagerFolder = getFolder(root, ConfigParameters.ADMIN_FILE_MANAGER_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "FM Root: " + adminFileManagerFolder.toString());

        // Preview Folder
        preview = getFolder(root, ConfigParameters.PREVIEW_FOLDER, OnNoProviderRoot.AutoCreate);
        logger.log(Level.INFO, "Preview Folder: " + preview.toString());

        // Tags Folder
        tags = getFolder(root, ConfigParameters.TAGS_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Tags Folder: " + tags.toString());
        
        // Opportunity Folder
        opportunities = getFolder(root, ConfigParameters.OPPORTUNITIES_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Opportunities Folder: " + opportunities.toString());

        // Account documents Folder
        accountDocuments = getFolder(root, ConfigParameters.ACCOUNT_DOCUMENTS_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Account documents Folder: " + accountDocuments.toString());

        // Channel report Folder
        channelReport = getFolder(root, ConfigParameters.CHANNEL_REPORT_FOLDER, OnNoProviderRoot.AutoCreate);
        logger.log(Level.INFO, "Channel report Folder: " + channelReport.toString());

        // Creatives Folder
        creatives = getFolder(root, ConfigParameters.CREATIVES_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Creatives Folder: " + creatives.toString());

        // Terms Folder
        terms = getFolder(root, ConfigParameters.TERMS_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Terms Folder: " + terms.toString());

        // Templates Folder
        templates = getFolder(root, ConfigParameters.TEMPLATES_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Templates Folder: " + templates.toString());

        // Reports Folder
        reports = getFolder(root, ConfigParameters.REPORTS_FOLDER, OnNoProviderRoot.AutoCreate);
        logger.log(Level.INFO, "Reports Folder: " + reports.toString());

        // Report documents folder
        reportDocuments = getFolder(root, ConfigParameters.REPORT_DOCUMENTS_FOLDER, OnNoProviderRoot.AutoCreate);
        logger.log(Level.INFO, "Report documents Folder: " + reportDocuments.toString());

        // Publisher Accounts Folder
        publisherAccounts = getFolder(root, ConfigParameters.PUBLISHER_ACCOUNTS_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Publisher Accounts Folder: " + publisherAccounts.toString());

        // Webwise Discover Folder
        discover = getFolder(root, ConfigParameters.DISCOVER_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "Discover Folder: " + discover.toString());

        // KWM Tool Folder
        kwmTool = getFolder(root, ConfigParameters.KWM_TOOL_FOLDER, OnNoProviderRoot.Fail);
        logger.log(Level.INFO, "KWM Tool Folder: " + kwmTool.toString());

        // Bulk upload tool folder
        bulkUpload = getFolder(root, ConfigParameters.BULK_UPLOAD_FOLDER, OnNoProviderRoot.AutoCreate);
        logger.log(Level.INFO, "Bulk upload tool folder: " + bulkUpload.toString());
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getAdminFileManagerFolder() {
        return adminFileManagerFolder;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getOpportunities() {
        return opportunities;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getAccountDocuments() {
        return accountDocuments;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getChannelReport() {
        return channelReport;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getCreatives() {
        return creatives;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getTerms() {
        return terms;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getTemplates() {
        return templates;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getPreview() {
        return preview;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getReports() {
        return reports;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getReportDocuments() {
        return reportDocuments;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getTags() {
        return tags;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getPublisherAccounts() {
        return publisherAccounts;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getDiscover() {
        return discover;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getKwmTool() {
        return kwmTool;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getBulkUpload() {
        return bulkUpload;
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getNested(PathProvider pathProvider, String dir) {
        return pathProvider.getNested(dir);
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public PathProvider getNested(PathProvider pathProvider, String dir, OnNoProviderRoot mode) {
        return pathProvider.getNested(dir, mode);
    }

    @Override
    @TransactionAttribute(NOT_SUPPORTED)
    public FileSystem createFileSystem(PathProvider pathProvider) {
        return pathProvider.createFileSystem();
    }

    private PathProvider createRoot() {
        try {
            String fodlerName = getFolderName(ConfigParameters.DATA_ROOT);
            return newSimplePathProvider(fodlerName);
        } catch (IOException e) {
            throw new FileManagerException("Can't initialize root path provider");
        }
    }

    private PathProvider newSimplePathProvider(String dataRoot) throws IOException {
        File file = new File(dataRoot);
        return new SimplePathProvider(file, configService.get(ConfigParameters.ALLOWED_FILE_TYPES));
    }
}
