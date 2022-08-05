package com.foros.util.command;

import org.hibernate.Session;

/**
 * Author: Boris Vanin
 */
public abstract class AbstractNothingReturnHibernateWork implements HibernateWork<Void> {

    public final Void execute(Session session) {
        executeIt(session);
        return null;
    }

    public abstract void executeIt(Session session);

}
