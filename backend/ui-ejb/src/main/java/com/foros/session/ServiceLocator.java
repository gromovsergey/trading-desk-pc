package com.foros.session;

import com.foros.session.spring.EjbSupportConfiguration;
import java.util.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class ServiceLocator implements ServiceLookup {
    private static final Logger logger = Logger.getLogger(ServiceLocator.class.getName());

    private static ServiceLookup instance;

    private BeanFactory beans;

    private ServiceLocator() {
        this.beans = createEjbContext();
    }

    private AnnotationConfigApplicationContext createEjbContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(EjbSupportConfiguration.class);
        context.refresh();
        return context;
    }

    public synchronized static ServiceLookup getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    @Override
    public <T> T lookup(Class<T> lookupClass) {
        return beans.getBean(lookupClass);
    }

}
