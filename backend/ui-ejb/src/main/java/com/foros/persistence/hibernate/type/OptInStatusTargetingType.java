package com.foros.persistence.hibernate.type;

import com.foros.model.campaign.OptInStatusTargeting;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class OptInStatusTargetingType implements UserType {
    private static final int TYPE = Types.VARCHAR;

    @Override
    public int[] sqlTypes() {
        return new int[] {TYPE};
    }

    @Override
    public Class returnedClass() {
        return OptInStatusTargeting.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null && y == null) {
            return true;
        }

        boolean xInstanceOf = x instanceof OptInStatusTargeting;
        boolean yInstanceOf = y instanceof OptInStatusTargeting;

        if (xInstanceOf && yInstanceOf) {
            return x.equals(y);
        }

        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        return rs.wasNull() ? null : parse(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, TYPE);
        } else {
            st.setString(index, toString((OptInStatusTargeting) value));
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }

        return new OptInStatusTargeting(((OptInStatusTargeting) value));
    }

    @Override
    public boolean isMutable() {
        return true;
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

        return new OptInStatusTargeting(((OptInStatusTargeting) cached));
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public static OptInStatusTargeting parse(String str) {
        if (str == null || str.length() != 3) {
            throw new IllegalArgumentException("Illegal string given: '" + str + "'");
        }

        return new OptInStatusTargeting(
                readValue(str.charAt(0)),
                readValue(str.charAt(1)),
                readValue(str.charAt(2))
        );
    }

    private static boolean readValue(char letter) {
        switch (letter) {
            case 'Y':
                return true;
            case 'N':
                return false;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }

    public static String toString(OptInStatusTargeting val) {
        return (val.isOptedInUsers() ? "Y" : "N") + (val.isOptedOutUsers() ? "Y" : "N") + (val.isUnknownUsers() ? "Y" : "N");
    }

}

