package com.foros.persistence.hibernate.type;

import com.foros.model.Flags;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.ImmutableType;
import org.hibernate.type.LiteralType;

public class FlagsType extends ImmutableType implements LiteralType {

    public Object get(ResultSet rs, String name) throws SQLException {
        return new Flags(rs.getInt(name));
    }

    public Class getReturnedClass() {
        return Flags.class;
    }

    public void set(PreparedStatement st, Object value, int index)
    throws SQLException {
        st.setInt(index, ((Flags) value).intValue());
    }

    public int sqlType() {
        return Types.INTEGER;
    }

    @Override
    public String toString(Object value) throws HibernateException {
        return value.toString();
    }

    public String getName() {
        return Flags.class.getName();
    }

    public String objectToSQLString(Object value, Dialect dialect) throws Exception {
        return String.valueOf(((Flags) value).intValue());
    }

    public Flags fromStringValue(String xml) {
        return new Flags(Integer.parseInt(xml));
    }

    @Override
    public Flags replace(Object originalObj, Object targetObj, SessionImplementor session, Object owner, Map copyCache) throws HibernateException {
        Flags original = (Flags) originalObj;
        Flags target = (Flags) targetObj;
        return target.set(original);
    }
}
