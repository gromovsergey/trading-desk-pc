package com.foros.action.admin.categoryChannel;

import com.foros.action.BaseActionSupport;
import com.foros.session.admin.categoryChannel.CategoryChannelService;

import javax.ejb.EJB;

public class StatusCategoryChannelAction extends BaseActionSupport {
    @EJB
    private CategoryChannelService categoryChannelService;

    // parameters
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String activate()  {
        categoryChannelService.activate(getId());
        return SUCCESS;
    }

    public String inactivate()  {
        categoryChannelService.inactivate(getId());
        return SUCCESS;
    }

    public String delete() {
        categoryChannelService.delete(id);
        return SUCCESS;
    }

    public String undelete() {
        categoryChannelService.undelete(id);
        return SUCCESS;
    }
}
