package com.foros.util.command;

import org.hibernate.Session;

public interface HibernateWork<R> {

    R execute(Session session);

}
