package com.foros.reporting.tools.query.parameters.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.joda.time.Period;
import org.postgresql.util.PGInterval;

public class PostgreIntervalUserType implements UserType {
    public static final PostgreIntervalUserType USER_TYPE = new PostgreIntervalUserType();
    public static final Type TYPE = new CustomType(USER_TYPE);

    private static final int[] SQL_TYPES = { Types.OTHER };

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return Period.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String interval = rs.getString(names[0]);
        if (rs.wasNull() || interval == null) {
            return null;
        }

        PGInterval pg = new PGInterval(interval);
        int years = pg.getYears();
        int months = pg.getMonths();
        int weeks = 0;
        int days = pg.getDays();
        int hours = pg.getHours();
        int minutes = pg.getMinutes();
        int seconds = (int)pg.getSeconds();
        int millis = (int) ((pg.getSeconds() * 1000) % 1000);
        return new Period(years, months, weeks, days, hours, minutes, seconds, millis);
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            Period joda = (Period) value;
            int years = joda.getYears();
            int months = joda.getMonths();
            int days = joda.getDays();
            int hours = joda.getHours();
            int minutes = joda.getMinutes();
            double seconds = joda.getMillis() * 0.0001d + joda.getSeconds();
            st.setObject(index, new PGInterval(years, months, days, hours, minutes, seconds), Types.OTHER);
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
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

}

