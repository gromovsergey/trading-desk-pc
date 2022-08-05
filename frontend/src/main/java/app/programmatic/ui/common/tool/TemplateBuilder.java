package app.programmatic.ui.common.tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateBuilder {
    public static final String DELIMETER = "##";
    public static final Pattern PATTERN = Pattern.compile(DELIMETER + "([\\w\\.]+)" + DELIMETER);

    private Map<String, String> replacements = new HashMap<>();

    private String template;

    public TemplateBuilder(String template) {
        this.template = template;
    }

    public TemplateBuilder add(String key, String value) {
        this.replacements.put(key, value);
        return this;
    }

    public TemplateBuilder addAll(Map<String, String> values) {
        replacements.putAll(values);
        return this;
    }

    public String generate() {
        Set<String> keys = fetchKeys(template);

        String buffer = template;
        for (String key : keys) {
            buffer = buffer.replace(DELIMETER + key + DELIMETER, replacements.get(key));
        }

        return buffer;
    }

    private Set<String> fetchKeys(String template) {
        Set<String> result = new HashSet<>();

        if (!(template == null || template.trim().length() == 0)) {
            Matcher matcher = PATTERN.matcher(template);
            while (matcher.find()) {
                result.add(matcher.group(1));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "TemplateBuilder[" + template + "]";
    }


}
