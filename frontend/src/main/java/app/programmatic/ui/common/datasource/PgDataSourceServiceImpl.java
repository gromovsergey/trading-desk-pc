package app.programmatic.ui.common.datasource;

import app.programmatic.ui.authorization.model.AuthUserInfo;
import app.programmatic.ui.authorization.service.AuthorizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.concurrent.Callable;


@Service
public class PgDataSourceServiceImpl implements DataSourceService {
    @Autowired
    private AuthorizationService authorizationService;

    public <T> T executeWithAuth(JdbcOperations jdbcOperations, Callable<T> callable) {
        AuthUserInfo user = authorizationService.getAuthUserInfo();

        jdbcOperations.execute("select common.set_user_info((?::int,?::inet))",
                (PreparedStatement ps) -> {
                    ps.setLong(1, user.getId());
                    ps.setString(2, user.getIp());
                    return ps.execute();
                });
        try {
            return callable.call();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            jdbcOperations.execute("select common.clear_user_info()");
        }
    }

    public void executeWithAuth(JdbcOperations jdbcOperations, Runnable runnable) {
        executeWithAuth(jdbcOperations,
                        new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                runnable.run();
                                return null;
                            }
                        });
    }
}
