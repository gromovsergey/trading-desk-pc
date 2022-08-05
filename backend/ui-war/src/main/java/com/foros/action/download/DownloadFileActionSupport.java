package com.foros.action.download;

import com.foros.action.fileman.FileManagerActionSupport;
import com.foros.session.fileman.ContentSource;

public abstract class DownloadFileActionSupport extends FileManagerActionSupport {
    private String targetFile;
    private ContentSource contentSource;

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }

    public abstract String download();

}
