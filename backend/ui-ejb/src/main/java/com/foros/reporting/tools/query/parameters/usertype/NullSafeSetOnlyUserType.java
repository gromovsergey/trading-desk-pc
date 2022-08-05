package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public abstract class NullSafeSetOnlyUserType implements UserType {

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
