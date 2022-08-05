package com.foros.action.admin;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.admin.DynamicResource;
import com.foros.model.security.Language;
import com.foros.session.admin.DynamicResourcesService;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class DynamicResourceAction extends BaseActionSupport {
    @EJB
    protected DynamicResourcesService service;
    private Map<String, DynamicResource> values = new HashMap<String, DynamicResource>();
    private String resourceKey;
    private String actionPath;

    public Language[] getLanguages() {
        return Language.values();
    }

    public Map<String, DynamicResource> getValues() {
        return values;
    }

    public void setValues(Map<String, DynamicResource> values) {
        this.values = values;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    protected String getFinalKey() {
        return resourceKey;
    }

    @ReadOnly
    public String edit() {
        List<DynamicResource> resources = service.findResources(getFinalKey());
        composeValues(resources);
        return SUCCESS;
    }

    private void composeValues(List<DynamicResource> resources) {
        values = new HashMap<String, DynamicResource>();
        for (DynamicResource res : resources) {
            values.put(res.getLang(), res);
        }
    }

    @Validations(
        requiredStrings = {
              @RequiredStringValidator(fieldName="resourceKey", key="errors.field.required")}
    )
    public String save() {
        List<DynamicResource> created = new ArrayList<DynamicResource>();
        List<DynamicResource> updated = new ArrayList<DynamicResource>();
        List<DynamicResource> deleted = new ArrayList<DynamicResource>();

        for (String locale : values.keySet()) {
            DynamicResource res = values.get(locale);
            if (StringUtil.isPropertyEmpty(res.getValue())) {
                if (StringUtil.isPropertyNotEmpty(res.getKey())) {
                    deleted.add(res);
                }
            } else {
                if (StringUtil.isPropertyNotEmpty(res.getKey())) {
                    updated.add(res);
                } else {
                    res.setKey(getFinalKey());
                    created.add(res);
                }
            }
        }
        service.saveResources(created, updated, deleted);
        return INPUT;
    }

    public Long getMaxLength() {
        return 1000L;
    }

    @Override
    public void validate() {
        for (Map.Entry<String, DynamicResource> entry : values.entrySet()) {
            if (entry.getValue().getValue().length() > getMaxLength()) {
                addFieldError("value_" + entry.getKey(), getText("errors.field.maxlength",
                        new String[]{String.valueOf(getMaxLength())}));
            }
        }
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }
}
