package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.FilesService;

public interface ForosFileService {
    FilesService getFilesService();
    FilesService getAdminFilesService();
}
