package com.foros.action.admin.account;

import java.util.HashMap;
import java.util.Map;

import com.foros.breadcrumbs.CustomParametersBreadcrumbsElement;

public class ClobParamBreadcrumbsElement extends CustomParametersBreadcrumbsElement {
    public ClobParamBreadcrumbsElement(String name, String path, Long accountId) {
        super(name, path);
        Map<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("accountId", accountId.toString());
        setParameters(parameters);
    }

    public static ClobParamBreadcrumbsElement createNoticeBreadcrumbsElement(Long accountId) {
        return new ClobParamBreadcrumbsElement("Notice.entityName", "Notices/main", accountId);
    }

    public static ClobParamBreadcrumbsElement createTermsBreadcrumbsElement(Long accountId) {
        return new ClobParamBreadcrumbsElement("TermsOfUse.entityName", "TermsOfUse/main", accountId);
    }
}
