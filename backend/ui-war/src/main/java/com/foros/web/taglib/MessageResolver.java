package com.foros.web.taglib;

import com.foros.util.StringUtil;

public class MessageResolver {
    public static String resolveGlobal(String resource, String id, Object prepare) {
        return StringUtil.resolveGlobal(resource, id, toBoolean(prepare));
    }

    private static Boolean toBoolean(Object prepare) {
        if (prepare instanceof Boolean) {
            return (Boolean) prepare;
        } else if (prepare instanceof String) {
            return Boolean.valueOf((String)prepare);
        } else {
            throw new IllegalArgumentException("true/false is expected");
        }
    }
}
