package com.foros.action.site;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.session.site.WDTagPreviewService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;

public class ViewWDTagAction extends WDTagActionSupport implements RequestContextsAware, BreadcrumbsSupport {

    private String tagHtmlCode;

    @EJB
    private WDTagPreviewService previewService;

    @ReadOnly
    public String view() {
        if (wdTag.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }
        wdTag = wdTagService.view(wdTag.getId());

        try {
            setTagHtmlCode(previewService.getHTMLCode(wdTag));
        } catch (Exception e) {
            String msg = getText("wdtag.htmlCodeNotAvailable");
            addFieldError("tagHtmlCode", msg);
        }

        return SUCCESS;
    }

    public String getTagHtmlCode() {
        return tagHtmlCode;
    }

    public void setTagHtmlCode(String tagHtmlCode) {
        this.tagHtmlCode = tagHtmlCode;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(wdTag.getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new SiteBreadcrumbsElement(wdTag.getSite())).add(new WDTagBreadcrumbsElement(wdTag));
    }
}
