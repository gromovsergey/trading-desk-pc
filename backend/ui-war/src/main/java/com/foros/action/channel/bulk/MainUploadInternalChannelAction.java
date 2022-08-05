package com.foros.action.channel.bulk;

import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.action.channel.UploadChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.restriction.annotation.Restrict;


public class MainUploadInternalChannelAction extends UploadChannelActionSupport implements BreadcrumbsSupport {
    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.upload")
    public String main() {
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new UploadChannelsBreadcrumbsElement());
    }
}
