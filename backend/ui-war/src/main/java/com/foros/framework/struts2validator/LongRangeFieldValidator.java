package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;


public class LongRangeFieldValidator extends AbstractRangeValidator<Number> {

    private Long maxValue = null;
    private Long minValue = null;

    private String max = null;
    private String min = null;


    public Comparable<Number> getMaxComparatorValue() {
        return maxValue == null ? null : new ComparableNumber(maxValue);
    }

    public Comparable<Number> getMinComparatorValue() {
        return minValue == null ? null : new ComparableNumber(minValue);
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMax() {
        return max;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMin() {
        return min;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        parseParameterValues(object);
        super.validate(object);
        
    }

    private void parseParameterValues(Object object) {
        this.minValue = parseLong(min, object);
        this.maxValue = parseLong(max, object);
    }

    private Long parseLong (String value, Object object) {
        if (value != null) {
            try {
                return ((Number)getFieldValue(value, object)).longValue();
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("LongRangeFieldValidator - [parseLong]: Unable to parse given long parameter " + value);
                }
            }
        }
        return null;
    }
    
    private static class ComparableNumber implements Comparable<Number> {
        private Long thisN;

        private ComparableNumber(Long number) {
            this.thisN = number;
        }

        public int compareTo(Number thatN) {
            return thisN.compareTo(thatN.longValue());
        }
    }
}
