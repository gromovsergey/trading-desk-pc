package com.foros.rs.provider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextFactory {
    private static final ConcurrentMap<Class, JAXBContext> cache = new ConcurrentHashMap<>();

    public static JAXBContext newInstance(Class<?> type) throws JAXBException {
        JAXBContext context = cache.get(type);
        if (context == null) {
            context = JAXBContext.newInstance(type);
            cache.put(type, context);
        }
        return context;
    }
}
