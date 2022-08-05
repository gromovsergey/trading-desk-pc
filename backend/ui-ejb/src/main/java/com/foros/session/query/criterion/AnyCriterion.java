package com.foros.session.query.criterion;

import com.foros.reporting.tools.query.parameters.usertype.PostgreLongArrayUserType;

import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

public class AnyCriterion implements Criterion {

    private final Type arrayType;
    private final String propertyName;
    private final Object value;

    public AnyCriterion(Type arrayType, String propertyName, Object value) {
        this.arrayType = arrayType;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.findColumns(propertyName, criteria);

        String result = StringHelper.join(" and ", StringHelper.suffix( columns, " = any(?)" ));
        if (columns.length > 1) {
            result = '(' + result + ')';
        }
        return result;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] { new TypedValue(arrayType, value, EntityMode.POJO) };
    }

    public static AnyCriterion anyId(String propertyName, Collection<Long> ids) {
        return new AnyCriterion(new CustomType(new PostgreLongArrayUserType()), propertyName, ids);
    }
}

