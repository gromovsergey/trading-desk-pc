package com.foros.audit.changes;

import static com.foros.monitoring.JmxBeanName.DATABASE_CHANGE_ADAPTER_NAME;
import com.foros.monitoring.AbstractMonitoringBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@LocalBean
@Singleton
@Startup
public class DatabaseChangeAdapterMonitoringBean extends AbstractMonitoringBean {
    @EJB
    private DatabaseChangeAdapterTimedBean databaseChangeAdapterTimed;

    @PostConstruct
    public void init() {
        startMonitoring(DATABASE_CHANGE_ADAPTER_NAME.getValue(), databaseChangeAdapterTimed.getInterval());
    }

    @Override
    protected long getLastProcessedTime() {
        return databaseChangeAdapterTimed.getLastProcessedTime();
    }

    @Override
    protected boolean isActive() {
        return databaseChangeAdapterTimed.isActive();
    }
}
