package com.foros.session.query;

import java.sql.Types;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class SqlOrder extends Order {

    private String sql;
    private String propertyName;
    private boolean ascending;

    private boolean ignoreCase = false;

    @Override
    public Order ignoreCase() {
        this.ignoreCase = true;
        return this;
    }

    public SqlOrder(String sqlFormat, String propertyName, boolean ascending) {
        super(propertyName, ascending);
        this.sql = sqlFormat;
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        // copy from org.hibernate.criterion.Order
        String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
        Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
        StringBuffer fragment = new StringBuffer();
        for ( int i=0; i<columns.length; i++ ) {
            SessionFactoryImplementor factory = criteriaQuery.getFactory();
            boolean lower = ignoreCase && type.sqlTypes( factory )[i]== Types.VARCHAR;
            if (lower) {
                fragment.append( factory.getDialect().getLowercaseFunction() )
                    .append('(');
            }
            fragment.append( formatSql(sql, columns[i]) );
            if (lower) fragment.append(')');
            fragment.append( ascending ? " asc" : " desc" );
            if ( i<columns.length-1 ) fragment.append(", ");
        }
        return fragment.toString();
    }

    private String formatSql(String sql, String column) {
        return sql.replace("{column}", column);
    }

    public static Order asc(String sqlFormat, String propertyName) {
        return new SqlOrder(sqlFormat, propertyName, true);
    }

    public static Order desc(String sqlFormat, String propertyName) {
        return new SqlOrder(sqlFormat, propertyName, false);
    }

}
