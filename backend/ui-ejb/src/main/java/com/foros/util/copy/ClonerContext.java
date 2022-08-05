package com.foros.util.copy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import com.foros.util.bean.Filter;

/**
 *
 * @author oleg_roshka
 */
public class ClonerContext {
    static final class ClassMetadata {
        Constructor m_noargConstructor; // cached no-arg constructor
        Field[] m_declaredFields; // cached declared fields
        boolean m_noargConstructorAccessible;
        boolean m_fieldsAccessible;
    }
    
    //objMap maps a source object to its clone in the current traversal
    private Map<Object, Object> objMap;
    
    //metadataMap maps a Class object to its ClassMetadata.
    private Map<Class, ClassMetadata> metadataMap;
    private Map<Object, Field> fieldMap;
    private Filter<Object> clonerFilter;

    public ClonerContext() {
        objMap = new IdentityHashMap<Object, Object>();
        metadataMap = new HashMap<Class, ClassMetadata>();
        fieldMap = new HashMap<Object, Field>();
        clonerFilter = null;
    }

    public ClonerContext(Filter<Object> clonerFilter) {
        this();
        this.clonerFilter = clonerFilter;
    }

    public ClassMetadata getMetaData(Class clazz) {
        ClassMetadata metadata = metadataMap.get(clazz);
        if (metadata == null) {
            metadata = new ClassMetadata();
            metadataMap.put(clazz, metadata);
        }

        return metadata;
    }

    public void putClone(Object source, Object clone) {
        objMap.put(source, clone);
    }

    public Object getClone(Object source) {
        return objMap.get(source);
    }

    public boolean containsCloneForObject(Object source) {
        return objMap.containsKey(source);
    }

    void putField(Object source, Field field) {
        fieldMap.put(source, field);
    }

    public Field getField(Object source) {
        return fieldMap.get(source);
    }

    public Filter<Object> getClonerFilter() {
        return clonerFilter;
    }
}
