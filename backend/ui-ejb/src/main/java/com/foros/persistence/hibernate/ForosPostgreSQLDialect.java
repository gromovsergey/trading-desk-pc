package com.foros.persistence.hibernate;

import com.foros.reporting.tools.query.parameters.usertype.PostgreIntervalUserType;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.LongType;

public class ForosPostgreSQLDialect extends PostgreSQLDialect {
    public ForosPostgreSQLDialect() {
        super();
        registerFunction("bitand", new SQLFunctionTemplate(LongType.INSTANCE, "(?1 & ?2)"));
        registerFunction("to_interval_sec", new SQLFunctionTemplate(PostgreIntervalUserType.TYPE, "(?1 * interval '1 sec')"));
    }
}
