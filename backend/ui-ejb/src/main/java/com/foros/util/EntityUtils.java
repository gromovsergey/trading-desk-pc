package com.foros.util;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.EntityBase;
import com.foros.model.IdNameStatusEntity;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.VersionEntityBase;
import com.foros.model.security.OwnedEntity;
import com.foros.session.BusinessException;
import com.foros.session.EntityTO;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.copy.BeanCloner;
import com.foros.util.jpa.JpaQueryWrapper;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.OptimisticLockException;
import javax.persistence.Table;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

public class EntityUtils {
    private static final Map<Class<?>, Field> entityIdMap =
            Collections.synchronizedMap(new HashMap<Class<?>, Field>());

    private EntityUtils() {
    }

    public static <T extends EntityBase> T clone(T src) {
        @SuppressWarnings("unchecked")
        T clone = (T)BeanCloner.clone(src, new CopyFilter());

        return clone;
    }

    public static <T extends EntityBase> T cloneWithChanges(T src) {
        T result = clone(src);
        Collection<String> changes = src.getChanges();
        result.registerChange(changes.toArray(new String[changes.size()]));
        return result;
    }

    public static <T extends EntityBase> void copy(T dst, T src) {
        PropertyDescriptor[] beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(src.getClass()).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("Beans introspection failed");
        }

        if (src instanceof VersionEntityBase) {
            Timestamp srcVersion = ((VersionEntityBase) src).getVersion();
            Timestamp dstVersion = ((VersionEntityBase) dst).getVersion();
            if (srcVersion != null && dstVersion != null && !srcVersion.equals(dstVersion)) {
                throw new OptimisticLockException(dst);
            }
        }

        Collection<String> changes = new HashSet<String>();
        changes.addAll(src.getChanges());

        for (PropertyDescriptor prop : beanInfo) {
            if (changes.contains(prop.getName())) {
                changes.remove(prop.getName());
                try {
                    Object srcValue = prop.getReadMethod().invoke(src);
                    prop.getWriteMethod().invoke(dst, srcValue);
                } catch (Throwable t) {
                    throw new RuntimeException("failed to copy property " + src.getClass() + "." + prop.getName() + "" , t);
                }
            }
        }

        if (!changes.isEmpty()) {
            throw new RuntimeException("Not all properties was found in the source entity");
        }
    }

    public static <T extends EntityBase>
    void removeDeleted(Collection<T> oldCol, Collection<T> newCol, EntityManager em) {
        if (oldCol == null)
            oldCol = new LinkedList<T>();

        if (newCol == null)
            newCol = new LinkedList<T>();

        // Remove deleted
        for (T t : oldCol) {
            if (!newCol.contains(t))
                em.remove(t);
        }
        em.flush();
    }

    @SuppressWarnings({"ConstantConditions"})
    public static <T extends EntityBase> Collection<T> merge(Collection<T> dst, Collection<T> src, EntityManager em) {
        if (dst == null) {
            dst = new LinkedList<T>();
        }

        if (src == null) {
            src = new LinkedList<T>();
        }

        // Remove deleted
        for (Iterator<T> it = dst.iterator(); it.hasNext();) {
            T t = it.next();
            if (!src.contains(t)) {
                em.remove(t);
                it.remove();
            }
        }

        // Merge existing & persist new
        for (T t : src) {
            boolean isNewEntity = false;
            try {
                if (PropertyUtils.getProperty(t, "id") == null) {
                    isNewEntity = true;
                }
            } catch (Exception ignored) {
            }

            if (isNewEntity || !t.getChanges().contains("id")) {
                em.persist(t);
                dst.add(t);
            } else {
                boolean found = false;
                for (T dt : dst) {
                    if (dt.equals(t)) {
                        EntityUtils.copy(dt, t);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new IllegalArgumentException(
                            "Entity with it's ID set was not found in target collection");
                }
            }
        }

        return dst;
    }

    @SuppressWarnings("all")
    public static Object getPropertyValue(Object o, String propertyName) throws Exception {
        if (o == null || propertyName == null || propertyName.length() == 0) {
            throw new Exception("Wrong parameters, Object = " + o + " property = " + propertyName);
        }

        Method m = MethodUtils.getAccessibleMethod(o.getClass(),
                "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()),
                new Class[0]);
        if (m == null) {
            throw new NoSuchMethodException("No getter  method found for property " + propertyName + " of a bean " + o.getClass().getName());
        }
        return m.invoke(o, (Object[]) null);
    }

    public static boolean setPropertyValue(Object o, String propertyName, Object value) {
        if (o == null || propertyName == null || propertyName.length() == 0) {
            return false;
        }

        boolean isSet = false;
        try {
            String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1,
                    propertyName.length());
            for (Method m : o.getClass().getMethods()) {
                if (m.getName().equals(methodName)) {
                    m.invoke(o, value);
                    isSet = true;
                }
            }
        } catch (Exception ex) {
            return isSet;
        }

        return isSet;
    }

    /**
     * Applies status rules. Rules are the following:
     * <ul>
     * <li>If entity is deleted and retainDeleted == true - add "(DELETED)" to the name</li>
     * <li>If entity is deleted but is used (currentId != null and entity.id = currentId) then add "(DELETED)" to the name</li>
     * <li>If entity is deleted, not used  and retainDeleted == false - then remove it form the list</li>
     * <li>If entity is inactive - then add "(INACTIVE)" to the name</li>
     * </ul>
     *
     * @param src           collection rules should be applied at
     * @param currentId     ID of currently selected entity
     * @param retainDeleted if all deleted entites should remain
     * @return the same collection with rules applied
     */
    public static <T extends EntityTO> Collection<T> applyStatusRules(Collection<T> src, Long currentId, boolean retainDeleted) {
        for (Iterator<? extends EntityTO> it = src.iterator(); it.hasNext();) {
            EntityTO entityTO = it.next();
            if (entityTO.getStatus() == Status.DELETED && !retainDeleted && !entityTO.getId().equals(currentId)) {
                it.remove();
                continue;
            }
            entityTO.setName(appendStatusSuffix(entityTO.getName(), entityTO.getStatus()));
        }

        return src;
    }

    /**
     * Applies status rules to the entity implementing OwnedEntity.
     *
     * @param src entity implementing OwnedEntity
     * @return entity with applier status rules
     */
    public static <T extends OwnedEntity> T applyOwnerStatusRule(T src) {
        src.getAccount().setName(appendStatusSuffix(src.getAccount().getName(), src.getAccount().getStatus()));
        return src;
    }

    /**
     * Applies status rules with retainDeleted == false.
     *
     * @param src       collection of the entities
     * @param currentId id of currently selected entity
     * @return collection of entities with applied status rules
     */
    public static Collection<? extends EntityTO> applyStatusRules(Collection<? extends EntityTO> src, Long currentId) {
        return applyStatusRules(src, currentId, false);
    }

    /**
     * Applies status rules and return new list with EntityTO
     *
     * @param src           collection of the entities
     * @param currentId     id of currently selected entity
     * @param retainDeleted if all deleted entities should remain
     * @return collection of EntityTO with applied status rules
     */
    public static List<EntityTO> convertWithStatusRules(Collection<? extends IdNameStatusEntity> src, Long currentId, boolean retainDeleted) {
        List<EntityTO> entityList = new ArrayList<EntityTO>();
        for (Iterator<? extends IdNameStatusEntity> it = src.iterator(); it.hasNext();) {
            IdNameStatusEntity entity = it.next();
            if (entity.getStatus() == Status.DELETED && !retainDeleted && !entity.getId().equals(currentId)) {
                it.remove();
                continue;
            }
            EntityTO entityTO = new EntityTO(entity.getId(), appendStatusSuffix(entity.getName(), entity.getStatus()), entity.getStatus().getLetter());
            entityList.add(entityTO);
        }

        return entityList;
    }

    /**
     * Makes a copy of source list and places copies of EntiyTo into, rather list elements themselfs.
     * Also applies status rules to append prefixes to names.
     */
    public static  List<EntityTO>  copyWithStatusRules(List<? extends EntityTO> src, Long currentId, boolean retainDeleted) {
        List<EntityTO> entityList = new LinkedList<EntityTO>();

        for (Iterator<? extends EntityTO> it = src.iterator(); it.hasNext();) {
            EntityTO entity = it.next();
            if (entity.getStatus() == Status.DELETED && !retainDeleted && !entity.getId().equals(currentId)) {
                continue;
            }
            EntityTO entityTO = new EntityTO(entity.getId(), appendStatusSuffix(entity.getName(), entity.getStatus()), entity.getStatus().getLetter());
            entityList.add(entityTO);
        }

        return entityList;
    }

    /**
     * Returns array of allowed statuses for entity type
     *
     * @param entityClass entity type
     * @return Status[]
     */
    public static <T extends StatusEntityBase> Status[] getAllowedStatuses(Class<T> entityClass) {
        AllowedStatuses allowedStatuses = entityClass.getAnnotation(AllowedStatuses.class);

        if (allowedStatuses == null) {
            throw new IllegalArgumentException(
                    "Entity " + entityClass.getName() + " should be annotated with " + AllowedStatuses.class.getName());
        }

        return allowedStatuses.values();
    }

    /**
     * Returns array of allowed QA statuses for entity type
     *
     * @param entityClass entity type
     * @return Status[]
     */
    public static <T extends ApprovableEntity> ApproveStatus[] getAllowedQAStatuses(Class<T> entityClass) {
        AllowedQAStatuses allowedStatuses = entityClass.getAnnotation(AllowedQAStatuses.class);

        if (allowedStatuses == null) {
            throw new IllegalArgumentException(
                    "Approvable entity " + entityClass.getName() + " should be annotated with " + AllowedQAStatuses.class.getName());
        }

        return allowedStatuses.values();
    }


    /**
     * Find id propery name marked @Id annotation of entity class.
     *
     * @param entityClass entity class
     * @return id property name of given entity class
     */
    public static String findIdPropertyName(Class<?> entityClass) {
        Field idField = findIdProperty(entityClass);
        if (idField != null) {
            return idField.getName();
        } else {
            return "";
        }
    }

    /**
     * Find id propery field marked @Id annotation of entity class.
     *
     * @param entityClass entity class
     * @return field object for id property of given entity class
     */
    public static Field findIdProperty(Class<?> entityClass) {
        Field idField = entityIdMap.get(entityClass);

        if (idField != null) {
            return idField;
        }

        for (Class clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (AnnotationUtil.isAnnotationPresent(clazz, field, Id.class)) {
                    entityIdMap.put(entityClass, field);
                    return field;
                }
            }
        }

        return null;
    }

    public static String findTableName(Class<? extends EntityBase> entityClass) {
        if (entityClass == null) {
            return null;
        }

        Table tbl = entityClass.getAnnotation(Table.class);
        return tbl != null ? tbl.name() : entityClass.getSimpleName();
    }

    public static void checkDeletedCondition(Collection<? extends StatusEntityBase> previous, Collection<? extends StatusEntityBase> current) throws BusinessException {
        long deletedPrev = 0;
        if (previous != null) {
            for (StatusEntityBase o : previous) {
                if (o != null && Status.DELETED.equals(o.getStatus())) {
                    deletedPrev++;
                }
            }
        }

        long deletedCurr = 0;
        if (current != null) {
            for (StatusEntityBase o : current) {
                if (o != null && Status.DELETED.equals(o.getStatus())) {
                    deletedCurr++;
                }
            }
        }

        if (deletedPrev < deletedCurr) {
            throw new BusinessException("Deleted entity added!");
        }
    }

    public static void checkEntityVersion(VersionEntityBase entity, Timestamp version) {
        if (!entity.getVersion().equals(version)) {
            throw new VersionCollisionException();
        }
    }

    public static Set<Long> getEntityIds(Collection<? extends Identifiable> entities) {
        HashSet<Long> res = new HashSet<Long>(entities.size());
        for (Identifiable entity : entities) {
            res.add(entity.getId());
        }
        return res;
    }

    public static <T extends Identifiable> HashMap<Long, T> mapEntityIds(Collection<T> entities) {
        HashMap<Long, T> res = new HashMap<>(entities.size());
        for (Identifiable entity : entities) {
            res.put(entity.getId(), (T)entity);
        }
        return res;
    }

    /**
     * Appends a localized suffix denendly on Status value.
     * It appends nothing if Status is not eqial to (DELETED or INACTIVE)
     *
     * @param value target string to be modifed.
     * @param status status for which suffix will be generated
     *
     * @return a new stirng with appended suffix, or the same string if status not applicable.
     */
    public static String appendStatusSuffix(String value, Status status) {

        if (status == null) {
            throw new IllegalArgumentException("Status can't be null");
        }

        switch (status) {
            case DELETED:
                return value + " " + StringUtil.getLocalizedString("suffix.deleted");
            case INACTIVE:
                return value + " " + StringUtil.getLocalizedString("suffix.inactive");
            default:
                return value;
        }
    }

    public static String appendStatusSuffix(String value, DisplayStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can't be null");
        }

        switch (status.getMajor()) {
            case DELETED:
                return value + " " + StringUtil.getLocalizedString("suffix.deleted");
            case INACTIVE:
                return value + " " + StringUtil.getLocalizedString("suffix.inactive");
            default:
                return value;
        }
    }

    public static <T extends Identifiable> void checkEntitiesIds(Class<T> aClass, Set<T> entities, EntityManager em) {
        if (!entities.isEmpty()) {
            Set<Long> ids = new HashSet<Long>();
            for (Identifiable e : entities) {
                ids.add(e.getId());
            }
            int realCount = new JpaQueryWrapper(em, "select count(*) from " + aClass.getSimpleName() + " e where e.id in :ids").setArrayParameter("ids", ids).executeCount();
            if (entities.size() != realCount) {
                throw new EntityNotFoundException("Some entities of type " + aClass.getSimpleName() + " has wrong ids!");
            }
        }
    }

    public static <T extends EntityTO> Collection<T> sortByStatus(Collection<T> source) {
        if (source != null && !source.isEmpty()) {
            Collections.sort((List<EntityTO>) source, new StatusNameTOComparator());
        }
        return source;
     }

    public static boolean isIdentifiable(Identifiable entity) {
        return entity != null && entity.getId() != null;
    }
}
