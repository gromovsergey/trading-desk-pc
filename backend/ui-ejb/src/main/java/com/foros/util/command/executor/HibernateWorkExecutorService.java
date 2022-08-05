package com.foros.util.command.executor;

import com.foros.util.command.HibernateWork;

import javax.ejb.Local;

@Local
public interface HibernateWorkExecutorService {
    <T> T execute(HibernateWork<T> work);
}
