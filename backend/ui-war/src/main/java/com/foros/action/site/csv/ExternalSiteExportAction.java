package com.foros.action.site.csv;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;
import com.foros.util.context.RequestContexts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class ExternalSiteExportAction extends SiteExportSupportAction implements RequestContextsAware, PublisherSelfIdAware,
        ServletRequestAware, ServletResponseAware {

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    private Long publisherId;

    @EJB
    private AccountService accountService;

    public Long getPublisherId() {
        return publisherId;
    }

    @Override
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    protected Account getPublisherAccount() {
        return accountService.find(publisherId);
    }

    @Override
    protected Collection<Long> getIds() {
        return (Collection<Long>) (publisherId != null ? Arrays.asList(publisherId) : Collections.emptyList());
    }

    @ReadOnly
    public String export() throws Exception {
        prepareSitesForExport();

        if (hasActionErrors()) {
            return INPUT;
        }

        SiteExportHelper.serialize(request, response, getPublisherAccount().getName(), sites, SiteFieldCsv.EXTERNAL_EXPORT_METADATA);

        return null;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(publisherId);
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
