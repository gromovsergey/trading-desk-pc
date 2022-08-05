package com.foros.action.opportunity;

import com.foros.framework.ReadOnly;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.FileUtils;
import com.foros.session.opportunity.OpportunityService;

import java.io.IOException;
import javax.ejb.EJB;

public class ViewIOFileAction extends OpportunitySupportAction {

    private Long id;

    @EJB
    private OpportunityService opportunityService;

    @EJB
    private AccountService accountService;

    private String contentType;

    private String fileName;

    private ContentSource contentSource;

    @ReadOnly
    public String download() throws IOException {
        contentType = FileUtils.getMimeTypeByExtension(fileName);
        contentSource = opportunityService.getIOFileContent(id, fileName);
        return SUCCESS;
    }


    public ContentSource getContentSource() {
        return contentSource;
    }

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTargetFile() {
        return fileName;
    }
}
