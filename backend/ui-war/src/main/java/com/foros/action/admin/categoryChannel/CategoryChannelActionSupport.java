package com.foros.action.admin.categoryChannel;

import com.foros.action.BaseActionSupport;
import com.foros.model.channel.CategoryChannel;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.admin.categoryChannel.CategoryChannelTO;
import com.foros.util.EntityUtils;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class CategoryChannelActionSupport extends BaseActionSupport implements ModelDriven<CategoryChannel> {
    @EJB
    protected CategoryChannelService categoryChannelService;
    @EJB
    protected AccountService accountService;

    protected CategoryChannel categoryChannel = new CategoryChannel();

    private Long accountId;
    private String accountName;
    protected Collection<EntityTO> channelOwners;
    protected List<CategoryChannelTO> childrenChannels;
    protected List<EntityTO> parentLocations;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public List<CategoryChannelTO> getChildrenChannels() {
        return childrenChannels;
    }

    public List<EntityTO> getParentLocations() {
        return parentLocations;
    }

    public Collection<EntityTO> getChannelOwners() {
        return channelOwners;
    }

    @Override
    public CategoryChannel getModel() {
        return categoryChannel;
    }

    protected void initChannelOwners() {
        channelOwners = accountService.getInternalAccounts(true);
        EntityUtils.applyStatusRules(channelOwners, null, false);
    }
}
