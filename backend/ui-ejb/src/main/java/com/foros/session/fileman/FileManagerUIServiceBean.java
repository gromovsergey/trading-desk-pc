package com.foros.session.fileman;

import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageFileContentRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageFileSizeRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getTextAdImageRestrictionFilter;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxDirLevelsRestriction;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxFilesInDir;
import static com.foros.session.fileman.restrictions.FileManagerRestrictionUtils.getUploadMaxFilesInZipRestriction;
import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.FileManagerRestrictions;
import com.foros.session.fileman.audit.FileSystemAuditImpl;
import com.foros.session.fileman.restrictions.AccountFolderFileRestriction;
import com.foros.session.fileman.restrictions.CompositeFileRestriction;
import com.foros.session.fileman.restrictions.FileRestriction;
import com.foros.session.fileman.restrictions.RestrictionFilter;
import com.foros.session.security.AuditService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless(name = "FileManagerUIService")
@Interceptors({RestrictionInterceptor.class})
public class FileManagerUIServiceBean implements FileManagerUIService {
    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private FileManagerRestrictions fileManagerRestrictions;

    @EJB
    private ConfigService configService;

    @EJB
    private AuditService auditService;

    private Config config;
    private PathProvider fmRootPathProvider;
    private PathProvider templatesPathProvider;
    private PathProvider kwmToolPathProvider;

    @PostConstruct
    private void initialize() {
        fmRootPathProvider = pathProviderService.getAdminFileManagerFolder();
        templatesPathProvider = pathProviderService.getTemplates();
        kwmToolPathProvider = pathProviderService.getKwmTool();
        config = configService.detach();
    }

    @Override
    @Restrict(restriction = "FileManager.manage")
    public FileManager getRootFileManager() {
        return newFileManager(pathProviderService.createFileSystem(fmRootPathProvider));
    }

    @Override
    @Restrict(restriction = "Template.viewFileManager")
    public FileManager getTemplatesFileManager() {
        return newFileManager(pathProviderService.createFileSystem(templatesPathProvider));
    }

    @Override
    @Restrict(restriction="KWMTool.view")
    public FileManager getKwmToolFileManager() {
        return newFileManager(pathProviderService.createFileSystem(kwmToolPathProvider));
    }

    private FileManager newFileManager(FileSystem fs) {
        RestrictionFilter filter = getTextAdImageRestrictionFilter(config, pathProviderService.getCreatives());

        fs.setFileTypesRestriction(NullFileTypesRestrictionImpl.INSTANCE);

        // Maximum file size during upload 20MB
        fs.setFileSizeRestriction(getTextAdImageFileSizeRestriction(config, filter));

        // no more than 10 folder levels within account folder
        FileRestriction creativesFolderMaxDirLevel = new AccountFolderFileRestriction(
            pathProviderService.getCreatives(),
            getUploadMaxDirLevelsRestriction(config)
        );

        FileRestriction publisherAccountFolderMaxDirLevel = new AccountFolderFileRestriction(
            pathProviderService.getPublisherAccounts(),
            getUploadMaxDirLevelsRestriction(config)
        );

        // no more than 1000 folders+files on each level of folder
        FileRestriction uploadMaxFilesInDir = getUploadMaxFilesInDir(config);
        fs.setFileRestriction(new CompositeFileRestriction(uploadMaxFilesInDir, creativesFolderMaxDirLevel,
                publisherAccountFolderMaxDirLevel));
        
        // no more than 1000 files+folders in one ZIP file
        fs.setZipRestriction(getUploadMaxFilesInZipRestriction(config));

        fs.setFileManagerRestrictions(fileManagerRestrictions);
        // File content restrictions
        fs.setFileContentRestriction(getTextAdImageFileContentRestriction(config, filter));

        fs.setFileSystemAudit(new FileSystemAuditImpl(auditService, templatesPathProvider.getRootDir(), FileManager.Folder.Templates.getId()));

        return new FileManagerImpl(fs);
    }
}
