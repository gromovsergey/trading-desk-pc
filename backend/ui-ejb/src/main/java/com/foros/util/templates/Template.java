package com.foros.util.templates;

import com.foros.util.StringUtil;
import com.foros.util.templates.bundle.Bundle;
import com.foros.util.templates.bundle.Bundles;
import com.foros.util.templates.bundle.MultiBundle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Boris Vanin
 */
public class Template {
    public static final String DELIMETER = "##";
    public static final Pattern PATTERN = Pattern.compile(DELIMETER + "([\\w\\.]+)" + DELIMETER);

    private Map<String, String> mainReplacements = new HashMap<>();

    private String template;
    private MultiBundle bundle;

    public Template(String template) {
        this.template = template;
        this.bundle = Bundles.createMultiBundle(Bundles.createBundle(mainReplacements));
    }

    public Template add(String key, String value) {
        this.mainReplacements.put(key, value);
        return this;
    }

    public Template addAll(Map<String, String> values) {
        mainReplacements.putAll(values);
        return this;
    }

    public Template addBundle(Bundle bundle) {
        this.bundle.addBundle(bundle);
        return this;
    }

    public String generate() {
        Set<String> keys = fetchKeys(template);

        String buffer = template;
        for (String key : keys) {
            buffer = buffer.replace(DELIMETER + key + DELIMETER, resolve(key));
        }

        return buffer;
    }

    private String resolve(String key) {
        String result = bundle.get(key);
        return result != null ? result : "";
    }

    private Set<String> fetchKeys(String template) {
        Set<String> result = new HashSet<>();

        if (!StringUtil.isPropertyEmpty(template)) {
            Matcher matcher = PATTERN.matcher(template);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        }
        return result;
    }

    protected Bundle getBundle() {
        return bundle;
    }

    @Override
    public String toString() {
        return "Template[" + template + "]";
    }
}
