package com.foros.persistence.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;


public class InetType implements UserType {
    private static final int TYPE = Types.OTHER;

    @Override
    public int[] sqlTypes() {
        return new int[] { TYPE };
    }

    @Override
    public Class returnedClass() {
        return InetType.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x != null) {
            return x.equals(y);
        }

        return y == null;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        if(x != null) {
            return x.hashCode();
        }

        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        return rs.wasNull() ? null : value;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, TYPE);
        } else {
            PGobject pgObject = new PGobject();
            pgObject.setType("inet");
            pgObject.setValue((String)value);
            st.setObject(index, pgObject);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
