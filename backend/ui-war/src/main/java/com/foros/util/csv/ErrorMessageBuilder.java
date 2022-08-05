package com.foros.util.csv;

import com.foros.util.StringUtil;
import com.foros.validation.constraint.convertion.ErrorMessageList;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class ErrorMessageBuilder<E extends PathableCsvField> implements ErrorMessageList {

    private List<String> messages = new ArrayList<String>();
    private E[] fieldCsvs;
    private Class entityType;

    public ErrorMessageBuilder(E[] fieldCsvs, Class entityType) {
        this.fieldCsvs = fieldCsvs;
        this.entityType = entityType;
    }

    public void setEntityType(Class entityType) {
        this.entityType = entityType;
    }

    private static <E extends PathableCsvField> E find (E[] fieldCsvs, Class entityType, String fieldPath) {
        for (E fieldCsv : fieldCsvs) {
            Class beanType = fieldCsv.getBeanType();
            if (isMatched(entityType, beanType) && isMatched(fieldPath, fieldCsv)) {
                return fieldCsv;
            }
        }
        return null;
    }

    private static boolean isMatched(Class entityType, Class beanType) {
        if (ObjectUtils.equals(entityType, beanType)) {
            return true;
        }
        if (beanType == null) {
            return false;
        }
        return beanType.isAssignableFrom(entityType);
    }

    private static boolean isMatched(String path, PathableCsvField field) {
        String fieldPath = field.getFieldPath();
        if (path.equals(fieldPath) || path.endsWith("." + fieldPath)) {
            return true;
        }
        return false;
    }

    @Override
    public void add(String path, String message) {
        String fieldName = null;
        if (path != null) {
            PathableCsvField field = find(fieldCsvs, entityType, path);
            if (field != null) {
                fieldName = StringUtil.getLocalizedString(field.getNameKey(), true);
                if (fieldName == null) {
                    fieldName = field.getNameKey();
                }
            }
        }
        if (fieldName != null) {
            messages.add(fieldName + ": " + message);
        } else {
            messages.add(message);
        }
    }

    public String build() {
        return StringUtils.join(messages, ", ");
    }
}
