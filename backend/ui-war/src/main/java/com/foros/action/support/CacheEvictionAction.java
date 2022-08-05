package com.foros.action.support;

import com.foros.action.BaseActionSupport;
import com.foros.action.admin.country.CountrySource;
import com.foros.cache.generic.CacheProviderService;
import com.foros.framework.ReadOnly;
import com.foros.reporting.tools.olap.query.saiku.SaikuStatementProvider;
import com.foros.session.cache.CacheService;
import com.foros.util.ExceptionUtil;
import com.foros.util.StringUtil;
import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassSearcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.persistence.Entity;

public class CacheEvictionAction extends BaseActionSupport {
    private boolean allEntries = false;
    private String id;
    private String collectionName;
    private List<Class> entityClasses;
    private String className;

    @EJB
    CacheService cacheService;

    @EJB
    private CacheProviderService cacheProviderService;

    @EJB
    private SaikuStatementProvider saikuStatementProvider;

    public String refreshOlapDataSource() throws Exception {
        saikuStatementProvider.refresh();
        return SUCCESS;
    }

    @ReadOnly
    public String main() throws Exception {
        return SUCCESS;
    }

    public String evict() throws Exception {
        if (StringUtil.isPropertyNotEmpty(collectionName)) {
            evictCollection();
        } else {
            evictEntity();
        }
        return SUCCESS;
    }

    public String clear() throws Exception {
        cacheProviderService.getCache().clear();
        addActionMessage("Memcache was cleared successfully");
        return SUCCESS;
    }

    private void evictEntity() {
        Class classObject = getClassObject();
        if (classObject != null) {
            try {
                if (isAllEntries()) {
                    cacheService.evictRegionNonTransactional(classObject);
                    addActionMessage(getText("evicted.class", new String[]{classObject.getName()}));
                } else {
                    cacheService.evictNonTransactional(classObject, !StringUtil.isNumber(id) ? id : StringUtil.convertToLong(id));
                    addActionMessage(getText("evicted.class.id", new String[]{classObject.getName(), id}));
                }
            } catch (Exception ex) {
                addFieldError("class", getText("errors.detail", new String[]{ExceptionUtil.getRootMessage(ex)}));
            }
        }
    }

    private void evictCollection() {
        Class classObject = getClassObject();
        if (classObject != null) {
            String name = classObject.getName() + "." + getCollectionName();
            try {
                if (isAllEntries()) {
                    cacheService.evictCollectionNonTransactional(name);
                    addActionMessage(getText("evicted.collection", new String[]{name}));
                } else {
                    cacheService.evictCollectionNonTransactional(name, StringUtil.convertToLong(id));
                    addActionMessage(getText("evicted.collection.id", new String[]{name, id}));
                }
            } catch (Exception ex) {
                addFieldError("collection", getText("errors.detail", new String[]{ExceptionUtil.getRootMessage(ex)}));
            }
        }
    }

    private Class getClassObject() {
        if (StringUtil.isPropertyEmpty(className)) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            addFieldError("class", getText("errors.classnNotFound", new String[]{className}));
        } catch (NoClassDefFoundError e) {
            // need to catch this error for following scenario
            // e.g. actual class name is com.foros.model.Creative and user provided com.foros.model.creative
            addFieldError("class", getText("errors.classnNotFound", new String[]{className}));
        }

        return null;
    }

    @Override
    public void validate() {
        super.validate();

        if (StringUtil.isPropertyNotEmpty(id)) {
            try {
                if (className.endsWith("Country") && !CountrySource.getCountryCodes().contains(id)) {
                    addFieldError("id", getText("errors.field.invalid"));
                } else if (!className.endsWith("Country")) {
                    StringUtil.convertToLong(id);
                }
            } catch (NumberFormatException ne) {
                addFieldError("id", getText("errors.field.integer"));
            }
        }

        if (StringUtil.isPropertyNotEmpty(collectionName) && !isAllEntries() && StringUtil.isPropertyEmpty(id)) {
            addFieldError("id", getText("errors.field.required"));
        }

        if (StringUtil.isPropertyEmpty(collectionName) && !isAllEntries() && StringUtil.isPropertyEmpty(id)) {
            addFieldError("id", getText("errors.field.required"));
            addFieldError("collection", getText("errors.field.required"));
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isAllEntries() {
        return allEntries;
    }

    public void setAllEntries(boolean allEntries) {
        this.allEntries = allEntries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Class> getEntityClasses() {
        if (entityClasses == null) {
            ClassSearcher classSearcher = new ClassSearcher("com.foros.model", true);
            try {
                Set<Class> classesSet = classSearcher.search(new ClassFilter() {
                    @Override
                    public boolean accept(Class<?> clazz) {
                        return clazz.getAnnotation(Entity.class) != null;
                    }
                });

                trimClassList(classesSet);
                entityClasses = sortEntityClasses(new ArrayList<Class>(classesSet));
            } catch (Exception e) {
                throw new RuntimeException("Error while loading class");
            }
        }
        return entityClasses;
    }

    private List<Class> sortEntityClasses(List<Class> clazzList) {
        Collections.sort(clazzList, new Comparator<Class>() {
            public int compare(Class class1, Class class2) {
                return StringUtil.lexicalCompare(class1.getSimpleName(), class2.getSimpleName());
            }
        });
        return clazzList;
    }

    private void trimClassList(Set<Class> entityClasses) {
        for (Class clazz : entityClasses.toArray(new Class[entityClasses.size()])) {
            retainBaseClass(clazz, entityClasses);
        }
    }

    private void retainBaseClass(final Class clazz, Set<Class> entityClasses) {
        Class inputClass = clazz;
        boolean removeClass = false;

        while (inputClass.getSuperclass() != null && inputClass.getSuperclass().getAnnotation(Entity.class) != null) {
            Class sc = inputClass.getSuperclass();
            if (entityClasses.contains(sc)) {
                entityClasses.remove(inputClass);
                removeClass = true;
            }
            inputClass = sc;
        }
        if (removeClass) {
            entityClasses.remove(clazz);
        }
    }
}
