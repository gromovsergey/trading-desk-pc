package com.foros.action.fileman;

import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;
import com.foros.config.ConfigService;
import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.PathIsNotAccessibleException;
import com.foros.session.fileman.TooManyDirLevelsException;
import com.foros.util.StringUtil;

import java.io.IOException;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

public class FileManagerCreateFolderAction extends FileManagerActionSupport {
    @EJB
    private ConfigService configService;

    private String newFolder;

    public String createFolder() {
        prepareFileManager();

        try {

            if (!fileManager.checkExist(getCurrDirStr())) {
                addFieldError("resourceNotFound", getText("errors.fileOrFolder.notExist"));
                refreshCurrentDirectory(getCurrDirStr());
                return INPUT;
            }

            if (StringUtil.isPropertyEmpty(newFolder)) {
                addFieldError("newFolder", getText("errors.file.folderNameRequired"));
                return INPUT;
            }

            if (StringUtils.contains(newFolder, '/') || StringUtils.contains(newFolder, '\\')) {
                addFieldError("newFolder", getText("fileman.slashes.not.allowed"));
                return INPUT;
            }

            if (StringUtil.trimProperty(newFolder).equals(".") || StringUtil.trimProperty(newFolder).equals("..")) {
                addFieldError("newFolder", getText("errors.file.badFolderName"));
                return INPUT;
            }

            if (fileManager.checkExist(getCurrDirStr(), newFolder)) {
                addFieldError("newFolder", getText("fileman.folder.exists"));
                return INPUT;
            }

            fileManager.createFolder(getCurrDirStr(), newFolder);
            return SUCCESS;
        } catch (BadNameException e) {
            addFieldError("newFolder", getText("errors.file.badFolderName"));
        } catch (PathIsNotAccessibleException e) {
            addFieldError("newFolder", getText("errors.folder", new String[] {newFolder}));
        } catch (AccountSizeExceededException e) {
            addFieldError("newFolder", getText("errors.file.accDirSizeExceeded"));
        } catch (TooManyDirLevelsException e) {
            Integer maxDirLevels = configService.get(UPLOAD_MAX_DIR_LEVELS);
            addFieldError("newFolder", getText("errors.file.tooManyLevels", new String[] {maxDirLevels.toString()}));
        } catch (IOException e) {
            addFieldError("newFolder", getText("errors.folder", new String[] {newFolder}));
        }

        return INPUT;
    }

    public String getNewFolder() {
        return newFolder;
    }

    public void setNewFolder(String newFolder) {
        this.newFolder = newFolder;
    }
}
