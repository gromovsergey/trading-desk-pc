package com.foros.util.command;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author: Boris Vanin
 */
public abstract class AbstractConnectionHibernateWork<R> implements HibernateWork<R> {

    private R result = null;

    public R execute(Session session) {
        beforeExecution(session);
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                result = AbstractConnectionHibernateWork.this.execute(connection);
            }
        });
        afterExecution(session);
        return result;
    }

    protected void beforeExecution(Session session) {
        // do nothing by default
    }

    protected void afterExecution(Session session) {
        // do nothing by default
    }

    protected abstract R execute(Connection connection) throws SQLException;

}
