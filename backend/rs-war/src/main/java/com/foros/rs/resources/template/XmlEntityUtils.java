package com.foros.rs.resources.template;

import com.foros.model.VersionEntityBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class XmlEntityUtils {
    private static final Logger logger = Logger.getLogger(XmlEntityUtils.class.getName());

    public static void removeVersionRecursively(VersionEntityBase entity) {
        Set<VersionEntityBase> versionBased = new HashSet<>();
        findVersionBasedRecursively(entity, versionBased);
        for (VersionEntityBase vbe : versionBased) {
            vbe.setVersion(null);
        }
    }

    private static void findVersionBasedRecursively(VersionEntityBase entity, Set<VersionEntityBase> versionBased) {
        // If XmlAccessorType == None then JAXB will process XmlElement annotated methods only
        if (!entity.getClass().isAnnotationPresent(XmlAccessorType.class) ||
                !entity.getClass().getAnnotation(XmlAccessorType.class).value().equals(XmlAccessType.NONE)) {
            throw new IllegalArgumentException("Entity " + entity.getClass() + " has XmlAccessorType != NONE");
        }

        versionBased.add(entity);
        for (Method method : entity.getClass().getMethods()) {
            // If XmlJavaTypeAdapter annotation is present then there is special custom mapping of the entity
            if (!method.isAnnotationPresent(XmlElement.class) || method.isAnnotationPresent(XmlJavaTypeAdapter.class)) {
                continue;
            }

            if (VersionEntityBase.class.isAssignableFrom(method.getReturnType())) {
                VersionEntityBase currentEntity = (VersionEntityBase) invoke(entity, method);
                if (currentEntity!= null && !versionBased.contains(currentEntity)) {
                    findVersionBasedRecursively(currentEntity, versionBased);
                }
            } else if (Collection.class.isAssignableFrom(method.getReturnType())) {
                Collection collection = (Collection)invoke(entity, method);
                if (collection != null) {
                    for (Object obj : collection) {
                        if (VersionEntityBase.class.isAssignableFrom(obj.getClass()) && !versionBased.contains(obj)) {
                            findVersionBasedRecursively((VersionEntityBase)obj, versionBased);
                        }
                    }
                }
            }
        }
    }

    private static Object invoke(Object entity, Method method) {
        try {
            return method.invoke(entity);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Can't invoke " + entity.getClass().getName() + "." + method.getName(), e);
        }
        return null;
    }
}
