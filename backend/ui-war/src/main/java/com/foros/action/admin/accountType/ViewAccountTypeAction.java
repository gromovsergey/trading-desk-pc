package com.foros.action.admin.accountType;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.util.bean.Filter;
import com.foros.util.tree.TreeHolder;
import com.foros.util.tree.TreeNode;

import java.util.Iterator;

import javax.persistence.EntityNotFoundException;

public class ViewAccountTypeAction extends AccountTypeSupportAction implements BreadcrumbsSupport {

    private Iterator<TreeNode<EntityTO>> browsersChannelsIterator;
    private Iterator<TreeNode<EntityTO>> applicationsChannelsIterator;

    @ReadOnly
    @Restrict(restriction = "AccountType.view")
    public String view() {
        if (entity.getId() == null) {
            throw new EntityNotFoundException("Account Type with id = null not found");
        }
        entity = service.view(entity.getId());
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new AccountTypesBreadcrumbsElement()).add(new AccountTypeBreadcrumbsElement(entity));
    }


    @Override
    protected void initDeviceHelper() {
        deviceHelper = new DeviceTargetingHelper(getModel().getDeviceChannels(), new Filter<TreeNode<EntityTO>>() {
            @Override
            public boolean accept(TreeNode<EntityTO> node) {
                return true;
            }
        });
    }

    public Iterator<TreeNode<EntityTO>> getBrowsersChannelsIterator() {
        if (browsersChannelsIterator == null) {
            browsersChannelsIterator = new TreeHolder<EntityTO>(getDeviceHelper().getBrowsersTreeRoot()).iterator();
        }
        return browsersChannelsIterator;
    }

    public Iterator<TreeNode<EntityTO>> getApplicationsChannelsIterator() {
        if (applicationsChannelsIterator == null) {
            applicationsChannelsIterator = new TreeHolder<EntityTO>(getDeviceHelper().getApplicationsTreeRoot()).iterator();
        }
        return applicationsChannelsIterator;
    }
}
