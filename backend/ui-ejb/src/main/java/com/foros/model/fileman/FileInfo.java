package com.foros.model.fileman;

//import eu.medsea.mimeutil.MimeType;
//import eu.medsea.mimeutil.MimeUtil;
//import eu.medsea.util.StringUtil;

import com.foros.session.fileman.FileUtils;

import java.io.Serializable;

/**
 *
 * @author pavel
 */
public class FileInfo implements Serializable {
    private String name;
    private boolean directory;
    private long time;
    private long length;
    private String mimeType;

    public FileInfo() {
    }

    public FileInfo(java.io.File file) {
        this.name = file.getName();
        this.directory = file.isDirectory();
        this.length = file.length();
        this.time = file.lastModified();

        this.mimeType = FileUtils.getMimeTypeByExtension(file.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
