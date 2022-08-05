package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class PostgreSimpleArrayUserType implements UserType {

    public static final UserType INSTANCE = new PostgreSimpleArrayUserType();

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER};
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value != null) {
            Connection connection = st.getConnection();
            Collection<String> collection = (Collection<String>)value;
            st.setArray(index, connection.createArrayOf("varchar", collection.toArray()));
        } else {
            st.setNull(index, sqlTypes()[0]);
        }

    }

    @Override
    public Class returnedClass() {
        return List.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return ObjectUtils.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMutable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        throw new UnsupportedOperationException();
    }
}