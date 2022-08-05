package com.foros.cache.jbc;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.jboss.cache.notifications.annotation.CacheListener;
import org.jboss.cache.notifications.annotation.CacheStarted;
import org.jboss.cache.notifications.annotation.NodeEvicted;
import org.jboss.cache.notifications.event.Event;
import org.jboss.cache.notifications.event.NodeEvictedEvent;
import org.jgroups.JChannel;
import org.jgroups.jmx.JmxConfigurator;


@CacheListener
public class ForosCacheListener {

    private static final Logger logger = Logger.getLogger(ForosCacheListener.class.getName());
    private static final String JGROUPS_MBEAN_BASE_NAME = "org.jgroups";
    private final MBeanServer server;


    public ForosCacheListener() {
        ArrayList servers = MBeanServerFactory.findMBeanServer(null);
        if (servers == null || servers.size() == 0) {
            //Unreachable code (if running inside Glassfish)
            throw new RuntimeException("No MBeanServers found. Can't register JGroups MBean.");
        }
        server = (MBeanServer)servers.get(0);
    }

    @CacheStarted
    public void handleStart(Event event) {
        if (event.getCache() == null || event.getCache().getConfiguration() == null ||
                event.getCache().getConfiguration().getRuntimeConfig() == null ||
                event.getCache().getConfiguration().getRuntimeConfig().getChannel() == null) {
            logger.log(Level.WARNING, "handleStart(Event event): JBossCache config doesn't contain JGroups config.");
            return;
        }

        try {
            JChannel jgroupsChannel = (JChannel) event.getCache().getConfiguration().getRuntimeConfig().getChannel();
            JmxConfigurator.registerChannel(jgroupsChannel, server, JGROUPS_MBEAN_BASE_NAME, null, true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't register JGroups MBean.", e);
        }
    }
}
