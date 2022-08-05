package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class DoubleRangeFieldValidator extends Struts2FieldValidatorSupport {
    String maxInclusive = null;
    String minInclusive = null;
    String minExclusive = null;
    String maxExclusive = null;

    Double maxInclusiveValue = null;
    Double minInclusiveValue = null;
    Double minExclusiveValue = null;
    Double maxExclusiveValue = null;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Double value;
        
        Object obj = this.getFieldValue(fieldName, object);
        if (obj == null) {
            return;
        }
        
        String str = obj.toString();
        
        try {            
            value = Double.valueOf(str);
        } catch (NumberFormatException e) {
            Locale locale = CurrentUserSettingsHolder.getLocale();
            NumberFormat nf = NumberFormat.getInstance(locale);
            ParsePosition pos = new ParsePosition(0);
            try {
                value = nf.parse(str, pos).doubleValue();
            } catch (Exception ex) {
                return;
            }
            if (pos.getIndex() != str.length()) {
                return;
            }
        }

        parseParameterValues(object);
        if ((maxInclusiveValue != null && value.compareTo(maxInclusiveValue) > 0) ||
                (minInclusiveValue != null && value.compareTo(minInclusiveValue) < 0) ||
                (maxExclusiveValue != null && value.compareTo(maxExclusiveValue) >= 0) ||
                (minExclusiveValue != null && value.compareTo(minExclusiveValue) <= 0)) {
            addError(fieldName, object);
        }
    }

    private void parseParameterValues(Object object) {
        this.minInclusiveValue = parseDouble(minInclusive, object);
        this.maxInclusiveValue = parseDouble(maxInclusive, object);
        this.minExclusiveValue = parseDouble(minExclusive, object);
        this.maxExclusiveValue = parseDouble(maxExclusive, object);
    }

    private Double parseDouble (String value, Object object) {
        if (value != null) {
            try {
                return ((Number)getFieldValue(value, object)).doubleValue();
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("DoubleRangeFieldValidator - [parseDouble]: Unable to parse given double parameter " + value);
                }
            }
        }
        return null;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMaxInclusive() {
        return maxInclusive;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMinInclusive() {
        return minInclusive;
    }

    public String getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(String minExclusive) {
        this.minExclusive = minExclusive;
    }

    public String getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(String maxExclusive) {
        this.maxExclusive = maxExclusive;
    }
}
