package com.foros.persistence.hibernate;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class BulkSequenceGenerator extends SequenceGenerator {
    private static final Logger logger = Logger.getLogger(BulkSequenceGenerator.class.getName());

    private static final int DEFAULT_ALLOCATION_SIZE = 10;

    private String sql;
    private int allocationSize;
    private Class returnClass;

    private long[] idPool;
    private int currentIdPoolIndex;

    @Override
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        super.configure(type, params, dialect);
        String sequenceName = PropertiesHelper.getString("sequenceName", params, null);
        if (sequenceName == null) {
            throw new MappingException("sequenceName is not mapped!");
        }
        allocationSize = PropertiesHelper.getInt("allocationSize", params, DEFAULT_ALLOCATION_SIZE);
        idPool = new long[allocationSize];
        currentIdPoolIndex = allocationSize;
        sql = "select nextval('" + sequenceName + "'::regclass) from generate_series(1, " + allocationSize + ")";
        returnClass = type.getReturnedClass();
    }

    @Override
    public synchronized Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        if (currentIdPoolIndex == allocationSize) {
            idPool = generateIds(session);
            currentIdPoolIndex = 0;
        }

        Number id = IdentifierGeneratorHelper.createNumber(idPool[currentIdPoolIndex++], returnClass);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Sequence identifier generated: " + id);
    }

        return id;
    }

    private long[] generateIds(SessionImplementor session) {
        try {
            PreparedStatement st = session.getBatcher().prepareSelectStatement(sql);
            try {
                try (ResultSet rs = st.executeQuery()) {
                    long[] res = new long[idPool.length];
                    int i = 0;
                    while (rs.next()) {
                        res[i++] = rs.getInt(1);
                    }
                    if (i != res.length) {
                        throw new HibernateException("Not enough ids in ResultSet.");
                    }
                    return res;
                }
            } finally {
                session.getBatcher().closeStatement(st);
            }
        } catch (SQLException sqle) {
            throw JDBCExceptionHelper.convert(
                    session.getFactory().getSQLExceptionConverter(),
                    sqle,
                    "could not get next sequence value",
                    sql
            );
        }
    }
}
