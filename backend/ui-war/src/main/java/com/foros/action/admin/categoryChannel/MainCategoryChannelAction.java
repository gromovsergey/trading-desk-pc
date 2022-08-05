package com.foros.action.admin.categoryChannel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.admin.categoryChannel.CategoryChannelTO;

import java.util.List;

import javax.ejb.EJB;

public class MainCategoryChannelAction extends BaseActionSupport {
    @EJB
    private CategoryChannelService categoryChannelService;

    private List<CategoryChannelTO> channels;

    public List<CategoryChannelTO> getChannels() {
        return channels;
    }

    @ReadOnly
    @Restrict(restriction = "CategoryChannel.view")
    public String main() {
        channels = categoryChannelService.getChannelList(null);
        return INPUT;
    }

}
