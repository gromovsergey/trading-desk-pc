package com.foros.reporting.tools.query.parameters;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;

public class TypeToUserTypeAdapter implements UserType {
    private Type type;

    public TypeToUserTypeAdapter(Type type) {
        this.type = type;
    }

    @Override
    public int[] sqlTypes() {
        return type.sqlTypes(null);
    }

    @Override
    public Class returnedClass() {
        return type.getReturnedClass();
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        return type.nullSafeGet(rs, names, null, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        type.nullSafeSet(st, value, index, null);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return type.deepCopy(value, null, null);
    }

    @Override
    public boolean isMutable() {
        return type.isMutable();
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return type.disassemble(value, null, null);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return type.assemble(cached, null, null);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return type.replace(original, target, null, owner, null);
    }
}
