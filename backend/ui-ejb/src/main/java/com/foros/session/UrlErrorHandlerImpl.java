package com.foros.session;

import com.foros.util.url.UrlErrorHandler;
import com.foros.validation.ValidationContext;
import org.apache.commons.lang.ArrayUtils;

public class UrlErrorHandlerImpl implements UrlErrorHandler {
    private ValidationContext context;
    private String value;
    private String path;

    public UrlErrorHandlerImpl(ValidationContext context, String value, String path) {
        this.context = context;
        this.value = value;
        this.path = path;
    }

    @Override
    public void invalidURL() {
        context.addConstraintViolation("errors.url")
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void invalidPort(String port) {
        context.addConstraintViolation("errors.url.port")
                .withParameters(port)
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void httpPortOnly() {
        context.addConstraintViolation("errors.url.httpPortOnly")
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void invalidUserinfo(String userinfo) {
        context.addConstraintViolation("errors.url.userinfo")
                .withParameters(userinfo)
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void emptyHost() {
        context.addConstraintViolation("errors.url.emptyHost")
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void invalidHost(String host) {
        context.addConstraintViolation("errors.url.host")
                .withParameters(host)
                .withValue(value)
                .withPath(path);
    }

    @Override
    public void invalidSchema(String[] schemas) {
        String template;
        if (ArrayUtils.contains(schemas, null)) {
            if (schemas.length > 1) {
                template = "errors.url.schema.canBeEmpty";
            } else {
                template = "errors.url.schema.onlyEmpty";
            }
        } else {
            template = "errors.url.schema";
        }

        StringBuilder schemasStr = new StringBuilder();
        for (int i = 0; i < schemas.length; i++) {
            if (schemas[i] != null) {
                if (i > 0) {
                    schemasStr.append(", ");
                }
                schemasStr.append(schemas[i]);
            }
        }

        context.addConstraintViolation(template)
                .withParameters(schemasStr.toString())
                .withValue(value)
                .withPath(path);
    }
}
