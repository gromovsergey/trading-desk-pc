package com.foros.persistence.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

public class GenericEnumType implements EnhancedUserType, ParameterizedType {
    private static final String PROPERTY_NAME_VALUE_OF_METHOD = "valueOfMethod";
    private static final String PROPERTY_NAME_IDENTIFIER_METHOD = "identifierMethod";
    private static final String PROPERTY_NAME_ENUM_CLASS = "enumClass";
    private static final String PROPERTY_NAME_NULL_VALUE = "nullValue";

    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";
    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

    @SuppressWarnings("unchecked")
    private Class<? extends Enum> enumClass;
    private Class<?> identifierType;
    private Method identifierMethod;
    private Method valueOfMethod;
    private Enum nullValue;
    private AbstractSingleColumnStandardBasicType<Object> type;
    private int[] sqlTypes;
    private boolean frozen = false;

    public GenericEnumType() {
    }

    public GenericEnumType(Class<? extends Enum> enumClass, String identifierMethodName, String valueOfMethodName) {
        this.enumClass = enumClass;
        setIdentifierMethodAndType(identifierMethodName);
        setTypes(identifierType.getName());
        setValueOfMethod(valueOfMethodName);
        frozen = true;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        try {
            Object identifier = type.get(rs, names[0], null);
            if (identifier == null) {
                return nullOrDefault();
            }
            if (identifier instanceof String) {
                // remove trailing spaces for CHAR(N) columns
                identifier = ((String)identifier).trim();
            }
            return valueOfMethod.invoke(enumClass, identifier);
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking valueOf method '" + valueOfMethod.getName()
                    + "' of " + "enumeration class '" + enumClass + "'", e);
        }
    }

    private Object nullOrDefault() throws Exception {
        return nullValue;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        try {
            if (isNullOrDefault(value)) {
                st.setNull(index, type.sqlType());
            } else {
                Object identifier = identifierMethod.invoke(value);
                type.set(st, identifier, index, null);
            }
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking identifierMethod '" + identifierMethod.getName()
                    + "' of " + "enumeration class '" + enumClass + "'", e);
        }
    }
    
    private boolean isNullOrDefault(Object value) throws Exception {
        return value == null || value == nullValue;
    }
    
    public void setParameterValues(Properties parameters) {
        if (frozen) {
            throw new IllegalStateException();
        }
        setEnumClass(parameters);
        setIdentifierMethodAndType(parameters);
        setTypes(identifierType.getName());
        setValueOfMethod(parameters);
        setDefaultValue(parameters);
        frozen = true;
    }

    private void setEnumClass(Properties parameters) {
        String enumClassName = parameters.getProperty(PROPERTY_NAME_ENUM_CLASS);
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException cfne) {
            throw new HibernateException("Enum class not found", cfne);
        }
    }
    
    private void setIdentifierMethodAndType(Properties parameters) {
        String identifierMethodName = parameters.getProperty(PROPERTY_NAME_IDENTIFIER_METHOD, DEFAULT_IDENTIFIER_METHOD_NAME);
        setIdentifierMethodAndType(identifierMethodName);
    }

    private void setIdentifierMethodAndType(String identifierMethodName) {
        try {
            identifierMethod = enumClass.getMethod(identifierMethodName);
            identifierType = identifierMethod.getReturnType();
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain identifier method", e);
        }
    }

    private void setTypes(String identifierTypeName) {
        type = (AbstractSingleColumnStandardBasicType) (new TypeResolver()).basic(identifierTypeName);

        if (type == null) {
            throw new HibernateException("Unsupported identifier type " + identifierType.getName());
        }
        sqlTypes = new int[] { type.sqlType() };
    }
    
    private void setDefaultValue(Properties parameters) {
        String nullValueString = parameters.getProperty(PROPERTY_NAME_NULL_VALUE);
        if (nullValueString == null) {
            return;
        }

        nullValue = Enum.valueOf(enumClass, nullValueString);
    }
    
    private void setValueOfMethod(Properties parameters) {
        String valueOfMethodName = parameters.getProperty(PROPERTY_NAME_VALUE_OF_METHOD, DEFAULT_VALUE_OF_METHOD_NAME);
        setValueOfMethod(valueOfMethodName);
    }

    private void setValueOfMethod(String valueOfMethodName) {
        try {
            valueOfMethod = enumClass.getMethod(valueOfMethodName, new Class[] { identifierType });
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain valueOf method", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class returnedClass() {
        return enumClass;
    }

    @Override
    public int[] sqlTypes() {
        return sqlTypes;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String objectToSQLString(Object value) {
        try {
            Object identifier = identifierMethod.invoke(value);
            return '\'' + identifier.toString() + '\'';
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking identifierMethod '" + identifierMethod.getName()
                    + "' of " + "enumeration class '" + enumClass + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toXMLString(Object value) {
        return ((Enum) value).name();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(enumClass, xmlValue);
    }
}
