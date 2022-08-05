package com.foros.action.birt;

import java.util.Map;

public interface ReportInfo {

    /**
     * @return rptdesign file too generate report
     */
    String getFile();

    /**
     * @return page title
     */
    String getTitle();

    /**
     * Populate report parameters
     * @param params marameters map
     */
    void populateParameters(Map<String, String> params);

    /**
     * Restrict report parameters (not birt parameters).
     * @return true = use ONLY values passed via populateParameters.
     */
    boolean isStrictParameters();
}
