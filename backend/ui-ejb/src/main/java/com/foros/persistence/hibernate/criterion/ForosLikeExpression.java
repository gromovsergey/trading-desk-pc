package com.foros.persistence.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.TypedValue;

public class ForosLikeExpression implements Criterion {
    private final String propertyName;
    private final Object value;
    private final Character escapeChar;
    private final boolean ignoreCase;

    public ForosLikeExpression(
            String propertyName,
            String value,
            Character escapeChar,
            boolean ignoreCase) {
        this.propertyName = propertyName;
        this.value = value;
        this.escapeChar = escapeChar;
        this.ignoreCase = ignoreCase;
    }

    public ForosLikeExpression(
            String propertyName,
            String value) {
        this( propertyName, value, null, false );
    }

    public ForosLikeExpression(
            String propertyName,
            String value,
            MatchMode matchMode) {
        this( propertyName, matchMode.toMatchString( value ) );
    }

    public ForosLikeExpression(
            String propertyName,
            String value,
            MatchMode matchMode,
            Character escapeChar,
            boolean ignoreCase) {
        this( propertyName, matchMode.toMatchString( value ), escapeChar, ignoreCase );
    }

    // To fix Java bug related to Turkish character (OUI-21793)
    public String toSqlString(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
        if ( columns.length != 1 ) {
            throw new HibernateException( "Like may only be used with single-column properties" );
        }
        String lhs = ignoreCase
                ? dialect.getLowercaseFunction() + '(' + columns[0] + ')'
                : columns[0];

        String esc = ( escapeChar == null ? "" : " escape \'" + escapeChar + "\'" );

        return lhs + " like " + (ignoreCase ? "lower(?)" : "?") + esc;

    }

    public TypedValue[] getTypedValues(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] {
                criteriaQuery.getTypedValue( criteria, propertyName, value.toString() )
        };
    }
}
