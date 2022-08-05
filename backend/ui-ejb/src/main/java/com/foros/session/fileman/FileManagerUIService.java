package com.foros.session.fileman;

import javax.ejb.Local;

@Local
public interface FileManagerUIService {
    FileManager getRootFileManager();

    FileManager getTemplatesFileManager();

    FileManager getKwmToolFileManager();
}
