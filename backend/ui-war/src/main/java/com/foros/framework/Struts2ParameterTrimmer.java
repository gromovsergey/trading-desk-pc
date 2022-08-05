package com.foros.framework;

import com.foros.util.StringUtil;

import java.util.Map;

public class Struts2ParameterTrimmer implements ParameterTrimmer {

    public void trimParameter(Map parameters, String name) {
        Object value = parameters.get(name);
        if (value instanceof String[]) {
            String[] paramValue = (String[]) value;
            for (int i = 0; i < paramValue.length; i++) {
                paramValue[i] = StringUtil.trimProperty(paramValue[i], "");
            }
        } else if (value instanceof String) {
            parameters.put(name, StringUtil.trimProperty((String) value, ""));
        }
    }
}
