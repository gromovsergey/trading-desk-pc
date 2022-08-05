package com.foros.action.channel.bulk;

import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.action.channel.UploadChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;


public class SubmitUploadChannelInternalAction extends SubmitUploadChannelBaseAction implements BreadcrumbsSupport {

    public String submit() {
        return doSubmit();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new UploadChannelsBreadcrumbsElement());
    }
}
