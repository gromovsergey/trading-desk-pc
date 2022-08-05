package com.foros.session;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;

@LocalBean
@Stateless
@Interceptors(TimeExecutionLoggingInterceptor.class)
public class LoggingJdbcTemplate extends PostgresqlJdbcTemplate {

    @PersistenceContext
    private EntityManager postgresEm;

    @EJB
    private CurrentUserService currentUserService;

    private LoggingJdbcTemplate self;

    @PostConstruct
    public void init() {
        self = ServiceLocator.getInstance().lookup(LoggingJdbcTemplate.class);
        setDataSource(new EntityManagerDataSource());
        SQLUtil.tryInitExceptionTranslator(this);
    }

    private class EntityManagerDataSource extends AbstractDataSource implements SmartDataSource {
        @Override
        public boolean shouldClose(Connection con) {
            return false;
        }

        @Override
        public Connection getConnection() throws SQLException {
            //noinspection deprecation
            return PersistenceUtils.getHibernateSession(postgresEm).connection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return getConnection();
        }
    }

    public void scheduleEviction() {
        PersistenceUtils.scheduleEviction(postgresEm);
    }

    public LoggingJdbcTemplate withAuthContext() {
        execute("select common.set_user_info((?::int,?::inet))", currentUserService.getUserId(), CurrentUserSettingsHolder.getIp());
        PersistenceUtils.getInterceptor(postgresEm).getJdbcTemplateCleaner().initialize(this);
        return self;
    }

    void clearAuthInfo() {
        execute("select common.clear_user_info()");
    }
}
