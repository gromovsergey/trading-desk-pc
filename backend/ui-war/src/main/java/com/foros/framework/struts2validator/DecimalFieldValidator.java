package com.foros.framework.struts2validator;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.util.StringUtil;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.ActionContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class DecimalFieldValidator extends Struts2FieldValidatorSupport {
    private String precision;
    private String scale;
    private int precisionValue;
    private int scaleValue;

    public void validate(Object action) throws ValidationException {
        String fieldName = getFieldName();
        BigDecimal value;
        
        Object obj = this.getFieldValue(fieldName, action);
        if (obj == null) {
            return;
        }
        
        String str = obj.toString();
        
        try {
            value = parseFieldValue(obj).stripTrailingZeros();
        } catch (NumberFormatException e) {
            Locale locale = CurrentUserSettingsHolder.getLocale();
            NumberFormat nf = NumberFormat.getInstance(locale);
            ParsePosition pos = new ParsePosition(0);
            try {
                double doubleValue = nf.parse(str, pos).doubleValue();
                value = BigDecimal.valueOf(doubleValue);
            } catch (Exception ex) {
                return;
            }
            if (pos.getIndex() != str.length()) {
                return;
            }
        }

        parseParameterValues(action);
        if (value.precision() - value.scale() > precisionValue - scaleValue || value.scale() > scaleValue) {
            addError(fieldName, action);
        }
    }

    private BigDecimal parseFieldValue(Object obj) throws NumberFormatException {
        if (obj instanceof BigDecimal) {
            return (BigDecimal)obj;
        }
        if (obj instanceof BigInteger) {
            return new BigDecimal((BigInteger)obj);
        }
        if (obj instanceof Number) {
            return new BigDecimal(((Number)obj).doubleValue());
        } else {
            return new BigDecimal(obj.toString());
        }
    }

    private void parseParameterValues( Object object) {
        this.precisionValue = parseInt(getPrecision(), object);
        this.scaleValue = parseInt(getScale(), object);
    }

    private int parseInt(String value, Object object) {
        if (value != null) {
            try {
                return ((Number)getFieldValue(value, object)).intValue();
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("DecimalFieldValidator - [parseInt]: Unable to parse given integer parameter " + value, e);
                }
            }
        }
        return -1;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getFormat() {
        return StringUtil.getNumberInputMask(ActionContext.getContext().getLocale(), precisionValue, scaleValue);
    }
}
