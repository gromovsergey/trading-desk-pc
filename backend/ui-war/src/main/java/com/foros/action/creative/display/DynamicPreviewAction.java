package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.fileman.ContentSource;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.struts2.ServletActionContext;

public class DynamicPreviewAction extends BaseActionSupport {
    private final static Logger logger = Logger.getLogger(DynamicPreviewAction.class.getName());

    @EJB
    private CreativePreviewService creativePreviewService;

    private ContentSource contentSource;

    private Long creativeId;
    private String path;

    @ReadOnly
    public String preview() {
        try {
            setContentSource(creativePreviewService.dcreative(path, creativeId));
        } catch (Throwable t) {
            //We shouldn't show any foros error pages with foros links (logout, etc.) in preview
            logger.log(Level.SEVERE, "Can't generate dynamic preview", t);

            ServletActionContext.getResponse().setStatus(404);
            return null;
        }

        return SUCCESS;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
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

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }
}
