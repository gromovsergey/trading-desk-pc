package com.foros.validation.constraint.validator;

import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.validation.ValidationException;

public class ValidatorUtils {

    public static class IdInfo {
        private Object id;
        private String fieldName;

        public IdInfo(Object id, String fieldName) {
            this.id = id;
            this.fieldName = fieldName;
        }

        public Object getId() {
            return id;
        }

        public String getFieldName() {
            return fieldName;
        }
    }

    public static IdInfo getEntityId(Class<?> entityClass, EntityBase entity) {
        if (isCountry(entityClass, entity)) {
            return new IdInfo( entity != null ? ((Country) entity).getCountryCode() : null, "countryCode");
        } else if (isIdentifiable(entityClass, entity)) {
            return new IdInfo( entity != null ? ((Identifiable) entity).getId() : null, "id");
        }

        throw new ValidationException("Entity " + entity + "has not identifier");
    }

    private static boolean isIdentifiable(Class<?> entityClass, EntityBase entity) {
        return Identifiable.class.isAssignableFrom(entityClass) || entity instanceof Identifiable;
    }

    private static boolean isCountry(Class<?> entityClass, EntityBase entity) {
        return Country.class.isAssignableFrom(entityClass) || entity instanceof Country;
    }


}
