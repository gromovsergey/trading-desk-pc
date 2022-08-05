package com.foros.session;

import com.foros.util.SQLUtil;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.sql.DataSource;

@LocalBean
@Stateless
@Interceptors(TimeExecutionLoggingInterceptor.class)
public class ImpalaJdbcTemplate extends JdbcTemplate {
    @Resource(name = "jdbc/impala", mappedName = "jdbc/impala")
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        setDataSource(dataSource);
        SQLUtil.tryInitExceptionTranslator(this);
    }
}
