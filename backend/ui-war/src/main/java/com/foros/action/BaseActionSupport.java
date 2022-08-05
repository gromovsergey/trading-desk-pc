package com.foros.action;

import com.foros.model.LocalizableName;
import com.foros.security.principal.SecurityContext;
import com.foros.util.LocalizableNameUtil;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BaseActionSupport extends ActionSupport implements ConstraintValidationsAware {

    private final transient TextProvider textProvider = new TextProviderSupport(new PrincipalLocaleProvider());
    private Collection<ConstraintViolation> constraintViolations = new ArrayList<>(5);

    public String getText(LocalizableName ln) {
        return LocalizableNameUtil.getLocalizedValue(ln);
    }

    @Override
    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    @Override
    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    @Override
    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    @Override
    public String getText(String aTextName, List args) {
        return textProvider.getText(aTextName, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    @Override
    public String getText(String aTextName, String defaultValue, List args) {
        return textProvider.getText(aTextName, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, args);
    }

    @Override
    public String getText(String key, String defaultValue, List args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    public String getContextName() {
        return ActionContext.getContext().getName();
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ConstraintViolation> getConstraintViolations() {
        return constraintViolations;
    }

    public boolean isInternal() {
        return SecurityContext.isInternal();
    }

    public boolean isAdvertiser() {
        return SecurityContext.isAdvertiser();
    }

    public boolean isAgencyOrAdvertiser() {
        return SecurityContext.isAgencyOrAdvertiser();
    }

    public boolean isPublisher() {
        return SecurityContext.isPublisher();
    }

    public boolean isIsp() {
        return SecurityContext.isIsp();
    }

    public boolean isCmp() {
        return SecurityContext.isCmp();
    }

    // It's impossible to use quotes in OGNL
    public String quote() {
        return "\'";
    }

    public String quote(String quote) {
        return '\'' + quote + '\'';
    }
}
