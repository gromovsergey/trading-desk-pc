package com.foros.action.fileman;

import com.foros.framework.ReadOnly;
import com.foros.session.fileman.SwfToHtmlConverterService;
import org.apache.commons.io.FilenameUtils;

import javax.ejb.EJB;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConvertSwfAction extends FileManagerActionSupport {

    @EJB
    SwfToHtmlConverterService converterService;

    private String sourceFileName;
    private String targetFileName;
    private String targetFileNameWithMacro;
    private String clickMacro;
    private boolean withClickUrlMacro;
    private boolean withoutClickUrlMacro;

    @ReadOnly
    public String dialog() {
        targetFileName = FilenameUtils.removeExtension(sourceFileName) + ".html";
        if (isFileExists(targetFileName)) {
            addFieldError("targetFileName", getText("fileman.error.fileExists", Arrays.asList(targetFileName)));
        }
        targetFileNameWithMacro = FilenameUtils.removeExtension(sourceFileName) + "_with_macro.html";
        if (isFileExists(targetFileNameWithMacro)) {
            addFieldError("targetFileNameWithMacro", getText("fileman.error.fileExists", Arrays.asList(targetFileNameWithMacro)));
        }
        withClickUrlMacro = true;
        withoutClickUrlMacro = true;
        return SUCCESS;
    }

    @ReadOnly
    public String check() {
        return SUCCESS;
    }

    public String convert() {
        List<String> warnings = converterService.convert(
                getFileManager(),
                getCurrDirStr(),
                sourceFileName,
                withoutClickUrlMacro,
                targetFileName,
                withClickUrlMacro,
                targetFileNameWithMacro,
                clickMacro
        );
        if (!warnings.isEmpty()) {
            for (String warning : warnings) {
                addActionError(warning);
            }
            return "warning";
        }
        return SUCCESS;
    }

    private boolean isFileExists(String fileName) {
        try {
            return getFileManager().checkExist(getCurrDirStr(), fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTargetFileExists() {
        return withoutClickUrlMacro && isFileExists(targetFileName);
    }

    public boolean isTargetWithMacroFileExists() {
        return withClickUrlMacro && isFileExists(targetFileNameWithMacro);
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getTargetFileNameWithMacro() {
        return targetFileNameWithMacro;
    }

    public void setTargetFileNameWithMacro(String targetFileNameWithMacro) {
        this.targetFileNameWithMacro = targetFileNameWithMacro;
    }

    public String getClickMacro() {
        return clickMacro;
    }

    public void setClickMacro(String clickMacro) {
        this.clickMacro = clickMacro;
    }

    public Boolean getWithClickUrlMacro() {
        return withClickUrlMacro;
    }

    public void setWithClickUrlMacro(Boolean withClickUrlMacro) {
        this.withClickUrlMacro = withClickUrlMacro;
    }

    public Boolean getWithoutClickUrlMacro() {
        return withoutClickUrlMacro;
    }

    public void setWithoutClickUrlMacro(Boolean withoutClickUrlMacro) {
        this.withoutClickUrlMacro = withoutClickUrlMacro;
    }
}
