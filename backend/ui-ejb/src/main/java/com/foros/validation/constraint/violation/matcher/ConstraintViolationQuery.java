package com.foros.validation.constraint.violation.matcher;


import com.foros.validation.constraint.violation.ConstraintViolation;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstraintViolationQuery {
    public static final String KW_INDEX = "#index";
    public static final String KW_KEY = "#key";
    public static final String KW_PROPERTY = "#property";
    public static final String KW_PATH = "#path";
    public static final String SPECIAL_SYMBOLS = ".[]()";

    private Pattern pattern;
    private String query;

    private ConstraintViolationQuery(Pattern pattern, String query) {
        this.pattern = pattern;
        this.query = query;
    }

    public ConstraintViolationMatcher matcher(ConstraintViolation violation) {
        Matcher matcher = pattern.matcher(violation.getPropertyPath().toString());
        return new ConstraintViolationMatcher(matcher, violation);
    }

    @Override
    public String toString() {
        return query + "->" + pattern.toString();
    }

    public static ConstraintViolationQuery compile(String query) {
        StringTokenizer tokenizer = new StringTokenizer(query, SPECIAL_SYMBOLS, true);
        StringBuilder regexp = new StringBuilder(query.length() * 2);
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();

            if (KW_INDEX.equals(token)) {
                regexp.append("\\d+");
            } else if (KW_KEY.equals(token)) {
                regexp.append(".+");
            } else if (KW_PROPERTY.equals(token)) {
                regexp.append("[a-zA-Z_0-9]+");
            } else if (KW_PATH.equals(token)) {
                regexp.append(".*");
            } else if ("()".contains(token)) {
                regexp.append(token);
            } else {
                regexp.append(Pattern.quote(token));
            }
        }

        return new ConstraintViolationQuery(Pattern.compile(regexp.toString()), query);
    }

}
