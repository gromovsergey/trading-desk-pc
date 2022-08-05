package com.foros.session.fileman;

import com.foros.model.template.OptionType;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class FilesNotFoundException extends FileNotFoundException {
    private Map<String, String> files;
    private Map<String, String> notAllowedFiles;
    private Map<String, OptionType> fileOptionTypes;

    /** 
     * Creates a new instance of FilesNotFoundException 
     */
    public FilesNotFoundException() {
        files = new HashMap<String, String>();
        notAllowedFiles = new HashMap<String, String>();
        fileOptionTypes = new HashMap<String, OptionType>();
    }

    public void addFile(String key, String fileName, OptionType optionType) {
        getFiles().put(key, fileName);
        getFileOptionTypes().put(key, optionType);
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public Map<String, String> getNotAllowedFiles() {
        return notAllowedFiles;
    }

    public void addNotAllowedFile(String key, String value, OptionType optionType) {
        getNotAllowedFiles().put(key, value);
        getFileOptionTypes().put(key, optionType);
    }

    public Map<String, OptionType> getFileOptionTypes() {
        return fileOptionTypes;
    }
}
