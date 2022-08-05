package com.foros.framework;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Container;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Current mapper will check is requested url corresponds to valid class name and method, if it isn't
 * then 404 error will be displayed instead of RuntimeException and 500 error.
 *
 * @author alexey_chernenko
 */
public class CustomActionMapper implements ActionMapper {
    private static Logger logger = Logger.getLogger(CustomActionMapper.class.getName());

    /**
     * Used for performance issue, to evoid unnecessary class loading
     */
    private static final ConcurrentMap<String, ClassHolder> ACTION_CLASSES = new ConcurrentHashMap<String, ClassHolder>();

    /** Default instance to delegate */
    private final DefaultActionMapper actionMapper = new DefaultActionMapper();

    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String allow) {
        actionMapper.setAllowDynamicMethodCalls(allow);
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES)
    public void setSlashesInActionNames(String allow) {
        actionMapper.setSlashesInActionNames(allow);
    }

    @Inject(StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE)
    public void setAlwaysSelectFullNamespace(String val) {
        actionMapper.setAlwaysSelectFullNamespace(val);
    }

    @Inject
    public void setContainer(Container container) {
        actionMapper.setContainer(container);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWED_ACTION_NAMES, required = false)
    public void setAllowedActionNames(String allowedActionNames) {
        actionMapper.setAllowedActionNames(allowedActionNames);
    }

    /**
     * If no class and method found for requested uri, then null action mapping is returned.
     */
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping mapping = actionMapper.getMapping(request, configManager);

        if (mapping != null) {
            RuntimeConfiguration runtimeConfiguration = configManager.getConfiguration().getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(mapping.getNamespace(), mapping.getName());
            if (actionConfig != null) {
                Class actionClass = loadClass(actionConfig.getClassName());

                if (actionClass != null && isMethodExists(actionClass, actionConfig.getMethodName())) {
                    return mapping;
                }
            }
        }
        return null;
    }

    @Override
    public ActionMapping getMappingFromActionName(String actionName) {
        return actionMapper.getMappingFromActionName(actionName);
    }

    /*
    * (non-Javadoc)
    *
    * @see org.apache.struts2.dispatcher.mapper.ActionMapper#getUriFromActionMapping(org.apache.struts2.dispatcher.mapper.ActionMapping)
    */
    public String getUriFromActionMapping(ActionMapping mapping) {
        return actionMapper.getUriFromActionMapping(mapping);
    }

    private boolean isMethodExists(Class clazz, String methodName) {
        if (clazz != null) {
            try {
                clazz.getMethod(methodName);
                return true;
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "Failed to lookup a method {0} in class {1}", new Object[]{methodName, clazz.getName()});
                return false;
            }
        }
        return false;
    }

    private Class loadClass(final String className) {
        try {
            final ClassHolder classHolder = new ClassHolder(className);
            ClassHolder existingClassHolder;
            if ((existingClassHolder = ACTION_CLASSES.putIfAbsent(className, classHolder)) != null) {
                return existingClassHolder.get();
            }

            return classHolder.get();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Could not find class {0}", className);
            ACTION_CLASSES.remove(className);
            return null;
        } catch (NoClassDefFoundError e) {
            logger.log(Level.SEVERE, "Could not load class {0}", className);
            ACTION_CLASSES.remove(className);
            return null;
        }
    }

    private static class ClassHolder {
        private final Lock lock;
        private final String name;
        private Class clazz;

        private ClassHolder(String name) {
            this.lock = new ReentrantLock();
            this.name = name;
        }

        private Class get() throws NoClassDefFoundError, ClassNotFoundException {
            try {
                lock.lock();
                if (clazz == null) {
                    clazz = ClassLoaderUtil.loadClass(name, CustomActionMapper.class);
                }
                return clazz;
            } finally {
                lock.unlock();
            }
        }
    }
}
