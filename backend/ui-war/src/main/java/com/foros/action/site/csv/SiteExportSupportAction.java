package com.foros.action.site.csv;

import static com.foros.config.ConfigParameters.SITE_CSV_UPLOAD_MAX_ROW_COUNT;

import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigService;
import com.foros.model.site.Site;
import com.foros.session.TooManyRowsException;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

public abstract class SiteExportSupportAction extends BaseActionSupport {

    @EJB
    private SiteService siteService;

    @EJB
    private TagsService tagsService;

    @EJB
    private ConfigService configService;

    protected List<Site> sites;

    protected abstract Collection<Long> getIds();

    protected void prepareSitesForExport() {
        if (getIds().isEmpty()) {
            addActionError(getText("site.export.nothingSelected"));
            return;
        }

        try {
            sites = siteService.fetchSitesForCsvDownload(getIds(), getMaxRowCount());
        } catch (TooManyRowsException e) {
            addActionError(getText("site.export.tooManyRows", new String[] { String.valueOf(getMaxRowCount()) }));
            return;
        }

        for (Site site : sites) {
            tagsService.fetchPassbackHtml(site.getTags());
        }
    }

    private Integer getMaxRowCount() {
        return configService.get(SITE_CSV_UPLOAD_MAX_ROW_COUNT);
    }
}
