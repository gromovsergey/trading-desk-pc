package com.foros.action.site.csv;

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

public class SiteExportValidationResultAction extends SiteUploadSupportAction
        implements ServletRequestAware, ServletResponseAware {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @EJB
    private AccountService accountService;

    @EJB
    private SiteUploadService siteUploadService;

    @ReadOnly
    public void export() throws Exception {
        Collection<Site> sites = siteUploadService.fetchValidatedSites(getValidationResult().getId());

        SiteExportHelper.serialize(request, response, getExportFileName(), sites, isInternalMode() ? SiteFieldCsv.INTERNAL_REVIEW_METADATA : SiteFieldCsv.EXTERNAL_REVIEW_METADATA);
    }

    private String getExportFileName() {
        if (publisherId != null) {
            return accountService.find(publisherId).getName();
        } else {
            return SiteExportHelper.EXPORT_FILE_NAME;
        }
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
