package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.session.CurrentUserService;
import com.foros.session.site.TagsService;

import javax.ejb.EJB;

public class StatusTagAction extends BaseActionSupport {
    private Long id;
    private Long siteId;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private TagsService tagsService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String delete() {
        if (!currentUserService.isInternal()) {
            siteId = tagsService.find(id).getSite().getId();
        }
        tagsService.delete(getId());

        return SUCCESS;
    }

    public String undelete() {
        tagsService.undelete(getId());

        return SUCCESS;
    }

    public Long getSiteId() {
        return siteId;
    }
}
