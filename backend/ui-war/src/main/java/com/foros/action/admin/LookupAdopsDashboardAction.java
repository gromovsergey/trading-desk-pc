package com.foros.action.admin;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;

public class LookupAdopsDashboardAction extends BaseActionSupport {

    private String lookup;
    private Long id;

    @ReadOnly
    public String lookup() throws Exception {
        return lookup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }
}
