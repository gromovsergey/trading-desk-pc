package com.foros.rs.sandbox.factory;

import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.test.factory.QueryParam;
import com.foros.test.factory.TestFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Logger;

public abstract class BaseGeneratorFactory<E extends Identifiable, T extends TestFactory<E>> {
    private static final Logger logger = Logger.getLogger(BaseGeneratorFactory.class.getName());

    protected T factory;
    protected String entityName;

    public BaseGeneratorFactory(T factory) {
        this.factory = factory;
    }

    public E findOrCreate(String entityName) {
        this.entityName = entityName;
        try {
            Class<E> entityClass = getEntityClass();
            E existing = factory.findAny(entityClass, getFindByNameQuery(entityName));
            logger.info("Appropriate entity " + entityClass.getName() + " found. Id: " + existing.getId());
            return update(existing);
        } catch (IllegalStateException e) {
            return create();
        }
    }

    private E update(E existing) {
        E result = createDefault();
        result.setId(existing.getId());
        setName(result, entityName);
        fixUTE(result);
        if (result instanceof VersionEntityBase) {
            ((VersionEntityBase) result).setVersion(((VersionEntityBase) existing).getVersion());
        }
        update(result, existing);
        return existing;
    }

    private void fixUTE(E result) {
        try {
            for (Method method : result.getClass().getMethods()) {
                String getter = method.getName();
                if (getter.startsWith("get") && method.getReturnType().equals(String.class) && method.getParameterTypes().length == 0) {
                    String setter = getter.replace("get", "set");
                    String value = (String) method.invoke(result);
                    if (value != null && value.startsWith(TestFactory.OUI_TEST_ENTITY_NAME_PREFIX)) {
                        try {
                            Method setterMethod = result.getClass().getMethod(setter, String.class);
                            setterMethod.invoke(result, getter.replace("get", ""));
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't restore UTE value  for class " + result.getClass(), e);
        }

    }

    public E create() {
        E result = createDefault();
        setName(result, entityName);
        fixUTE(result);
        persist(result);
        updatePersistentEntity(result);
        factory.clearContext();
        result = factory.refresh(result);

        logger.info("New entity " + getEntityClass().getName() + " created. Id: " + result.getId());

        return result;
    }

    protected void updatePersistentEntity(E result) {
    }

    protected abstract E createDefault();

    protected void setName(E entity, String name) {
        Exception cause = null;
        try {
            for (Method method : entity.getClass().getMethods()) {
                if (method.getName().equals("setName")) {
                    method.invoke(entity, name);
                    return;
                }
            }
        } catch (Exception e) {
            cause = e;
        }

        throw new RuntimeException("Can't set name for class " + entity.getClass(), cause);
    }

    protected void persist(E entity) {
        factory.persist(entity);
    }

    protected void update(E entity, E existing) {
        factory.update(entity);
    }

    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("name", name);
    }

    private Class<E> getEntityClass() {
        //noinspection unchecked
        return (Class) (((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
    }
}
