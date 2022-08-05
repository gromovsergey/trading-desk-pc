package com.foros.action.birt;

import java.util.Map;

public abstract class ReportInfoSupport implements ReportInfo {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void populateParameters(Map<String, String> params) {
    }

    @Override
    public boolean isStrictParameters() {
        return false;
    }
}
    
