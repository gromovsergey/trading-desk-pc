package com.foros.action.admin.country.ctra;

import com.foros.breadcrumbs.CustomParametersBreadcrumbsElement;
import com.foros.model.Country;

import java.util.HashMap;
import java.util.Map;

public class CTRAlgorithmBreadcrumbsElement extends CustomParametersBreadcrumbsElement {
    public CTRAlgorithmBreadcrumbsElement(Country country) {
        super("country.CTRAlgorithmData", "Country/CTRAlgorithm/view");
        Map<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("id", country.getCountryCode());
        setParameters(parameters);
    }
}
