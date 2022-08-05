package com.foros.session.creative;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
  It is assumed that size and template pair is unique for every defaultName
 */
public class SizeTemplateBasedValueResolver implements Serializable {
    private static final ValueType NULL_VALUE_TYPE = new ValueType(null, ColumnTypes.string());

    private Map<SizeTemplateKey, ValueType> values = new HashMap<>();

    public ValueType getValue(String defaultName, CreativeSize size, CreativeTemplate template, Object optionValue) {
        ValueType result = values.get(new SizeTemplateKey(defaultName, size.getDefaultName(), template.getDefaultName()));
        if (optionValue == null) {
            return result == null ? NULL_VALUE_TYPE : result;
        }
        return new ValueType(optionValue, result.getType());
    }

    public void addValue(String sizeName, String templateName, String defaultName, String defaultValue, ColumnType type) {
        values.put(new SizeTemplateKey(defaultName, sizeName, templateName), new ValueType(defaultValue, type));
    }

    public void addValue(CreativeSize size, CreativeTemplate template, String defaultName, String defaultValue, ColumnType type) {
        addValue(size.getDefaultName(), template.getDefaultName(), defaultName, defaultValue, type);
    }

    public void addValue(CreativeSize size, CreativeTemplate template, String defaultName, ValueType valueType) {
        values.put(new SizeTemplateKey(defaultName, size.getDefaultName(), template.getDefaultName()), valueType);
    }

    public Collection<ValueType> getValueTypes() {
        return values.values();
    }

    private class SizeTemplateKey implements Serializable {
        private String defaultName;
        private String sizeName;
        private String templateName;

        public SizeTemplateKey(String defaultName, String sizeName, String templateName) {
            this.defaultName = defaultName;
            this.sizeName = sizeName;
            this.templateName = templateName;
        }

        public String getDefaultName() {
            return defaultName;
        }

        public String getSizeName() {
            return sizeName;
        }

        public String getTemplateName() {
            return templateName;
        }

        @Override
        public int hashCode() {
            final int base = 31;
            int result = 1;
            result = base * result + (defaultName == null ? 0 : defaultName.hashCode());
            result = base * result + (sizeName == null ? 0 : sizeName.hashCode());
            result = base * result + (templateName == null ? 0 : templateName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            SizeTemplateKey other = (SizeTemplateKey) obj;
            return (defaultName == null ? other.defaultName == null : defaultName.equals(other.defaultName)) &&
                   (sizeName == null ? other.sizeName == null : sizeName.equals(other.sizeName)) &&
                   (templateName == null ? other.templateName == null : templateName.equals(other.templateName));
        }

        @Override
        public String toString() {
            return "SizeTemplateKey(" + defaultName + ", " + sizeName + ", " + templateName + ")";
        }
    }
}
