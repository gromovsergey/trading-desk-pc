package com.foros.action;

import com.foros.util.UITimestamp;

public class IdNameVersionForm<NameT> extends IdNameForm<NameT> {
    private UITimestamp version;

    public IdNameVersionForm() {

    }

    public UITimestamp getVersion() {
        if (version == null) {
            version = new UITimestamp((System.currentTimeMillis()));
        }
        return version;
    }

    public void setVersion(UITimestamp version) {
        if (version == null) {
            this.version = new UITimestamp((System.currentTimeMillis()));
        } else {
            this.version = version;
        }
    }
}
