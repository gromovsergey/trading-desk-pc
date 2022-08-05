package com.foros.session;

import com.foros.util.SQLUtil;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.sql.DataSource;

@LocalBean
@Stateless(name = "StatsJobJdbcTemplate")
@Interceptors(TimeExecutionLoggingInterceptor.class)
public class StatsJobJdbcTemplate extends PostgresqlJdbcTemplate {

    @Override
    @Resource(name = "jdbc/statsjob", mappedName = "jdbc/statsjob")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        SQLUtil.tryInitExceptionTranslator(this);
    }
}
