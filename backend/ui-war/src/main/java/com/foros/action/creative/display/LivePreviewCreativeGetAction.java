package com.foros.action.creative.display;

import com.opensymphony.xwork2.Action;
import com.foros.framework.ReadOnly;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.fileman.ContentSource;

import javax.ejb.EJB;


public class LivePreviewCreativeGetAction {

    @EJB
    private CreativePreviewService previewService;

    private String previewPath;
    private ContentSource contentSource;

    @ReadOnly
    public String process() {
        contentSource = previewService.getTemporaryPreview(getPreviewPath());
        if (contentSource != null) {
            return Action.SUCCESS;
        } else {
            return "404";
        }
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }
}
