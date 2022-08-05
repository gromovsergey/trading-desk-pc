package com.foros.action.site.csv;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.site.Site;
import com.foros.session.account.AccountService;
import com.foros.session.site.SiteUploadService;

import java.util.Collection;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class InternalSiteExportAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware {

    private String resultId;

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @EJB
    private AccountService accountService;

    @EJB
    private SiteUploadService siteUploadService;

    @ReadOnly
    public String export() throws Exception {
        Collection<Site> sites = siteUploadService.fetchValidatedSites(getResultId());

        SiteExportHelper.serialize(request, response, SiteExportHelper.EXPORT_FILE_NAME, sites, SiteFieldCsv.INTERNAL_EXPORT_METADATA);

        return null;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
