package com.foros.action.fileman;

import com.foros.util.StringUtil;

import java.util.Comparator;

public class FileForm {
    public static final FileComparator FILE_COMPARATOR = new FileComparator();
    private String name;
    private boolean directory;
    private String date;
    private String length;
    private String filetype;

    boolean allowedToSelect;

    public static class FileComparator implements Comparator<FileForm> {
        public int compare(FileForm form1, FileForm form2) {
            if (form1.isDirectory() && !form2.isDirectory()) {
                return -1;
            } else {
                if (form2.isDirectory() && !form1.isDirectory()) {
                    return 1;
                } else {
                    return StringUtil.compareToIgnoreCase(form1.getName(), form2.getName());
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public boolean isAllowedToSelect() {
        return allowedToSelect;
    }

    public void setAllowedToSelect(boolean allowedToSelect) {
        this.allowedToSelect = allowedToSelect;
    }
}
