package com.foros.action.creative;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.fileman.ContentSource;

import java.io.IOException;
import javax.ejb.EJB;

public class CreativePreviewAction extends BaseActionSupport {

    @EJB
    private CreativePreviewService previewService;

    public String path;
    private ContentSource contentSource;

    @ReadOnly
    public String generate() throws IOException {
        contentSource = previewService.generatePreview(path);
        if (contentSource != null) {
            return SUCCESS;
        } else {
            return "404";
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }
}
