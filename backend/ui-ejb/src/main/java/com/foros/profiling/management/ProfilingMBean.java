package com.foros.profiling.management;

import com.foros.profiling.ProfilingStatistic;
import com.foros.profiling.ProfilingStatisticHolder;
import org.glassfish.gmbal.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@LocalBean
@ManagedObject
@Singleton(name = "ProfilingMBean")
public class ProfilingMBean {

    private static final ManagedObjectManager objectManager
            = ManagedObjectManagerFactory.createStandalone("com.foros.profiling.management");

    private static final ProfilingStatisticCsvSerializer CSV_SERIALIZER = new ProfilingStatisticCsvSerializer();

    @PostConstruct
    protected void init() {
        objectManager.stripPrefix("com.foros.profiling.management");
        objectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
        objectManager.setRuntimeDebug(false);
        objectManager.setTypelibDebug(0);
        objectManager.createRoot(this);
    }

    @PreDestroy
    public void deactivate() {
        objectManager.unregister(this);
    }


    @NameValue
    public String getName() {
        return "Profiling management";
    }

    @ManagedOperation
    public void start() {
        ProfilingStatisticHolder.start();
    }

    @ManagedOperation
    public String stop() {
        return statisticToString(ProfilingStatisticHolder.stop());
    }

    @ManagedOperation
    public String reset() {
        return statisticToString(ProfilingStatisticHolder.reset());
    }

    @ManagedOperation
    public boolean isStarted() {
        return ProfilingStatisticHolder.isEnabled();
    }

    @ManagedOperation
    public String get() {
        return statisticToString(ProfilingStatisticHolder.get());
    }

    private String statisticToString(ProfilingStatistic statistic) {
        return CSV_SERIALIZER.serialize(statistic);
    }

}
