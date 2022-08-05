package com.foros.persistence.hibernate.type;

import com.foros.model.time.TimeSpan;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class TimeSpanSecondsType implements UserType {

    public static final int TYPE = Types.INTEGER;

    @Override
    public int[] sqlTypes() {
        return new int[] {TYPE};
    }

    @Override
    public Class returnedClass() {
        return TimeSpan.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && x.equals(y));
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        long valueInSeconds = rs.getLong(names[0]);
        return rs.wasNull() ? null : TimeSpan.fromSeconds(valueInSeconds);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        TimeSpan ts = (TimeSpan) value;
        Long valueInSeconds = null;

        if (ts != null) {
            valueInSeconds = ts.getValueInSeconds();
        }

        if (valueInSeconds == null) {
            st.setNull(index, TYPE);
        } else {
            st.setLong(index, valueInSeconds);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        TimeSpan ts = (TimeSpan) value;
        return ts == null ? null : new TimeSpan(ts);
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
        if (cached == null) {
            return null;
        }
        Long valueInSeconds = ((TimeSpan) cached).getValueInSeconds();
        return valueInSeconds == null ? null : TimeSpan.fromSeconds(valueInSeconds);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
