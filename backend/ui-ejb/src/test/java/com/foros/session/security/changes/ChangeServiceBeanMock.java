package com.foros.session.security.changes;

import com.foros.audit.serialize.AuditChange;
import com.foros.changes.ChangesInterceptorHandler;
import com.foros.changes.ChangesService;
import com.foros.changes.ChangesServiceBean;
import com.foros.util.command.executor.HibernateWorkExecutorService;

import javax.ejb.EJB;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

public class ChangeServiceBeanMock implements ChangesService {

    @Autowired
    private ChangesServiceBean changesServiceBean;

    @EJB
    private HibernateWorkExecutorService executorService;

    public void initialize() {
        changesServiceBean.initialize();
    }

    public void addChange(final AuditChange auditChange) {
        executorService.execute(new ChangesServiceBean.AbstractChangesInterceptorWork() {
            @Override
            public void processChanges(Session session, ChangesInterceptorHandler changesInterceptor) {
                if (changesInterceptor.isInitialized()) {
                    changesServiceBean.addChange(auditChange);
                }
            }
        });
    }

    @Override
    public void handleChanges() {
        changesServiceBean.handleChanges();
    }
}
