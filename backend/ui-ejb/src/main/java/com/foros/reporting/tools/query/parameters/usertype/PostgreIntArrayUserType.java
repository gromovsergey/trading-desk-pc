package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class PostgreIntArrayUserType implements UserType {

    public static PostgreIntArrayUserType INSTANCE = new PostgreIntArrayUserType();

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER};
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
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value != null) {
            Collection<Long> collection = (Collection<Long>)value;
            st.setArray(index, st.getConnection().createArrayOf("int4", collection.toArray(new Long[collection.size()])));
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMutable() {
        return true;
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
