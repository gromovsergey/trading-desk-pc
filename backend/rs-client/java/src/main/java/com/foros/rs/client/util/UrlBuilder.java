package com.foros.rs.client.util;

import com.foros.rs.client.RsException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlBuilder {

    private String uri;

    private UrlBuilder(String uri) {
        this.uri = uri;
    }

    private Map<String , Object> queryParameters = new HashMap<String, Object>();

    public UrlBuilder setQueryParameter(String name, Object value) {
        if (value != null) {
            queryParameters.put(name, value);
        }

        return this;
    }

    public UrlBuilder addQueryEntity(Object entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            QueryParameter queryParameter = field.getAnnotation(QueryParameter.class);
            if (queryParameter != null) {
                String name = queryParameter.value();
                if (name == null) {
                    name = field.getName();
                }

                Object value = getFieldValue(entity, field);
                if (value != null) {
                    if (value.getClass().isAnnotationPresent(QueryEntity.class)) {
                        addQueryEntity(value);
                    } else {
                        setQueryParameter(name, value);
                    }
                }
            }
        }

        return this;
    }

    private Object getFieldValue(Object entity, Field field) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                return field.get(entity);
            } finally {
                field.setAccessible(accessible);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RsException(e);
        }
    }

    public String toString() {
        return build();
    }

    public String build() {
        return uri + buildQueryParameters();
    }

    private String buildQueryParameters() {
        if (queryParameters.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder("?");
        for (Map.Entry<String, Object> parameter : queryParameters.entrySet()) {
            if (parameter.getValue() instanceof Iterable) {
                for (Object item : (Iterable) parameter.getValue()) {
                    if (parameter.getKey() != null && item != null) {
                        result.append(parameter.getKey()).append("=").append(encode(item.toString())).append("&");
                    }
                }
            } else if(parameter.getKey() != null && parameter.getValue() != null) {
                result.append(parameter.getKey()).append("=").append(encode(parameter.getValue())).append("&");
            }
        }

        return result.toString();
    }

    private String encode(Object value) {
        try {
            return URLEncoder.encode(value.toString(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static UrlBuilder path(String path) {
        return new UrlBuilder(path);
    }
}
