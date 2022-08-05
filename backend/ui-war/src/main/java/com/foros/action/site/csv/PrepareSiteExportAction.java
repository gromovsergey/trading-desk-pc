package com.foros.action.site.csv;

import com.foros.framework.ReadOnly;
import com.foros.session.site.SiteUploadService;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;

public class PrepareSiteExportAction extends SiteExportSupportAction {

    @EJB
    private SiteUploadService siteUploadService;

    private Collection<Long> accountIds = new ArrayList<>();

    private String text;

    @ReadOnly
    public String prepareExport() throws Exception {
        prepareSitesForExport();

        if (hasActionErrors()) {
            text = getActionErrors().toArray(new String[] {})[0];
            return INPUT;
        }

        text = "resultId=" + siteUploadService.saveResults(sites);

        return SUCCESS;
    }

    @Override
    protected Collection<Long> getIds() {
        return getAccountIds();
    }

    public Collection<Long> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(Collection<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public String getText() {
        return text;
    }

}
