package com.foros.session.fileman.restrictions;

import com.foros.config.Config;
import com.foros.session.fileman.PathProvider;

import static com.foros.config.ConfigParameters.ALLOWED_TEXT_AD_IMAGE_TYPES;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_ACCOUNT_SIZE;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_FILES_IN_ZIP;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_FILES_PER_FOLDER;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_SIZE;
import static com.foros.config.ConfigParameters.DEFAULT_MAX_UPLOAD_SIZE;
import static com.foros.config.ConfigParameters.TEXT_AD_IMAGES_FOLDER;
import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;

public class FileManagerRestrictionUtils {
    /**
     * @return restriction to all
     */
    public static FileSizeRestriction getUploadMaxFileSizeRestriction(Config config) {
        return new FileSizeRestrictionImpl(config.get(DEFAULT_MAX_UPLOAD_SIZE));
    }

    public static FileRestriction getUploadMaxFilesInDir(Config config) {
        return new FilesPerFolderRestriction(config.get(DEFAULT_MAX_FILES_PER_FOLDER));
    }
    
    public static QuotaProvider getUploadMaxAccountSizeQuotaProvider(Config config) {
        return new QuotaProviderImpl(config.get(DEFAULT_MAX_ACCOUNT_SIZE));
    }

    public static QuotaProvider getUploadMaxAccountSizeQuotaProvider(Config config, PathProvider rootPathProvider) {
        return new QuotaProviderImpl(config.get(DEFAULT_MAX_ACCOUNT_SIZE), rootPathProvider);
    }

    public static ZipRestriction getUploadMaxFilesInZipRestriction(Config config) {
        return new FileCountZipRestriction(config.get(DEFAULT_MAX_FILES_IN_ZIP));
    }

    public static FileRestriction getUploadMaxDirLevelsRestriction(Config config) {
        return new FileDepthRestriction(config.get(UPLOAD_MAX_DIR_LEVELS));
    }

    public static FileRestriction getUploadMaxDirLevelsRestriction(Config config, PathProvider rootPathProvider) {
        return new FileDepthRestriction(config.get(UPLOAD_MAX_DIR_LEVELS), rootPathProvider);
    }

    public static FileContentRestriction getTextAdImageFileContentRestriction(Config config) {
        return new TextAdImageFileContentRestriction(config.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION),
                config.get(ALLOWED_TEXT_AD_IMAGE_TYPES), NullRestrictionFilter.INSTANCE);
    }

    public static FileContentRestriction getTextAdImageFileContentRestriction(Config config, RestrictionFilter filter) {
        return new TextAdImageFileContentRestriction(config.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION),
                config.get(ALLOWED_TEXT_AD_IMAGE_TYPES), filter);
    }

    public static FileSizeRestriction getTextAdImageFileSizeRestriction(Config config) {
        return new TextAdImageFileSizeRestriction(config.get(DEFAULT_MAX_UPLOAD_SIZE),
                config.get(DEFAULT_MAX_TEXT_AD_IMAGE_SIZE), NullRestrictionFilter.INSTANCE);
    }

    public static FileSizeRestriction getTextAdImageFileSizeRestriction(Config config, RestrictionFilter filter) {
        return new TextAdImageFileSizeRestriction(config.get(DEFAULT_MAX_UPLOAD_SIZE),
                config.get(DEFAULT_MAX_TEXT_AD_IMAGE_SIZE), filter);
    }

    public static RestrictionFilter getTextAdImageRestrictionFilter(Config config, PathProvider creativesPathProvider) {
        return new TextAdImageRestrictionFilter(config.get(TEXT_AD_IMAGES_FOLDER), creativesPathProvider);
    }
}
