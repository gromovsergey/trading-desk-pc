package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFieldValidator extends Struts2FieldValidatorSupport {
    private String expression;
    private boolean caseSensitive = true;
    private boolean trim = true;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null || expression == null) {
            return;
        }

        // XW-375 - must be a string
        if (!(value instanceof String)) {
            return;
        }

        // string must not be empty
        String str = ((String) value).trim();
        if (str.length() == 0) {
            return;
        }

        // match against expression
        Pattern pattern;
        if (isCaseSensitive()) {
            pattern = Pattern.compile(expression);
        } else {
            pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        }

        String compare = (String) value;
        if ( trim ) {
            compare = compare.trim();
        }
        Matcher matcher = pattern.matcher( compare );

        if (!matcher.matches()) {
            addError(fieldName, object);
        }
    }

    /**
     * @return Returns the regular expression to be matched.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the regular expression to be matched.
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @return Returns whether the expression should be matched against in
     *         a case-sensitive way.  Default is <code>true</code>.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the expression should be matched against in
     * a case-sensitive way.  Default is <code>true</code>.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * @return Returns whether the expression should be trimed before matching.
     * Default is <code>true</code>.
     */
    public boolean isTrimed() {
        return trim;
    }

    /**
     * Sets whether the expression should be trimed before matching.
     * Default is <code>true</code>.
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }
}
