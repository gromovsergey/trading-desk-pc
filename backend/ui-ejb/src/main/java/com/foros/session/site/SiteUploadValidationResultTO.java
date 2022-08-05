package com.foros.session.site;

import com.foros.util.Stats;

public class SiteUploadValidationResultTO {
    private String id;
    private long lineWithErrors = 0;

    private Stats sites = new Stats();
    private Stats tags = new Stats();

    public Stats getSites() {
        return sites;
    }

    public void setSites(Stats sites) {
        this.sites = sites;
    }

    public Stats getTags() {
        return tags;
    }

    public void setTags(Stats tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLineWithErrors() {
        return lineWithErrors;
    }

    public void setLineWithErrors(long lineWithErrors) {
        this.lineWithErrors = lineWithErrors;
    }

    public boolean isErrorsExist() {
        return lineWithErrors > 0;
    }

    public boolean isValid() {
        return ((lineWithErrors == 0) && ((sites.getCreated() + sites.getUpdated() + tags.getCreated() + tags.getUpdated()) > 0));
    }
}
