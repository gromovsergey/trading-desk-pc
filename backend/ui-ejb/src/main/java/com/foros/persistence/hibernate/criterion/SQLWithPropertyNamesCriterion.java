package com.foros.persistence.hibernate.criterion;

import java.util.Arrays;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.Type;
import org.hibernate.util.ArrayHelper;
import org.hibernate.util.StringHelper;

public class SQLWithPropertyNamesCriterion implements Criterion {
    private final String sql;
    private final String[] propertyNames;
	private final TypedValue[] typedValues;

    public SQLWithPropertyNamesCriterion(String sql, String[] propertyNames) {
        this(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY, propertyNames);
    }

    protected SQLWithPropertyNamesCriterion(String sql, Object[] values, Type[] types, String[] propertyNames) {
		this.sql = sql;
		typedValues = new TypedValue[values.length];

        for ( int i=0; i<typedValues.length; i++ ) {
			typedValues[i] = new TypedValue( types[i], values[i], EntityMode.POJO );
		}

        this.propertyNames = propertyNames;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		String result = StringHelper.replace(sql, "{alias}", criteriaQuery.getSQLAlias(criteria));

        for (String propertyName : propertyNames) {
            String[] cols = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);

            if (cols.length != 1) {
                throw new IllegalArgumentException("Invalid property '" + propertyName + "', mapped to " + Arrays.asList(cols));
            }

            result = StringHelper.replace(result, propertyName, cols[0]);
        }

        return result;
	}

	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return typedValues;
	}

	public String toString() {
		return sql;
	}
}
