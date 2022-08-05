package com.foros.action.site;

import com.foros.session.NamedTO;

public class TagTO extends NamedTO {
    private NamedTO site = new NamedTO();

    public NamedTO getSite() {
        return site;
    }

    public void setSite(NamedTO site) {
        this.site = site;
    }
}
