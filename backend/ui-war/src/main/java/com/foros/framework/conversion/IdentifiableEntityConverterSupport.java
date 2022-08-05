package com.foros.framework.conversion;

import com.foros.model.Identifiable;

import java.util.Map;

public abstract class IdentifiableEntityConverterSupport extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        Identifiable identifiable = newInstance(toClass);
        Long id = Long.valueOf(value);
        identifiable.setId(id);
        return identifiable;
    }

    protected abstract Identifiable newInstance(Class toClass);

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (o == null) {
            return null;
        }
        Identifiable identifiable = (Identifiable) o;

        Long id = identifiable.getId();
        if (id == null) {
            return null;
        }
        return id.toString();
    }
}
