package com.foros.changes.inspection;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.audit.serialize.serializer.CollectionAuditSerializer;
import com.foros.audit.serialize.serializer.NullAuditSerializer;
import com.foros.audit.serialize.serializer.entity.WrapperAuditSerializer;
import com.foros.audit.serialize.serializer.entity.EntityAuditSerializer;
import com.foros.audit.serialize.serializer.primitive.BigDecimalAuditSerializer;
import com.foros.audit.serialize.serializer.primitive.PrimitiveAuditSerializer;
import com.foros.changes.inspection.changeNode.CollectionFieldChange;
import com.foros.changes.inspection.changeNode.EntityChange;
import com.foros.changes.inspection.changeNode.EntityFieldChange;
import com.foros.changes.inspection.changeNode.PrimitiveFieldChange;
import com.foros.model.EntityBase;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class ChangeDescriptorRegistryImpl implements ChangeDescriptorRegistry {

    private Map<Class, EntityChangeDescriptor> descriptorMap = new HashMap<Class, EntityChangeDescriptor>();

    public ChangeDescriptorRegistryImpl(SessionFactory factory) {
        try {
            init(factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EntityChangeDescriptor getDescriptor(Object object) {
        Class clazz = Hibernate.getClass(object);
        return descriptorMap.get(clazz);
    }

    private void init(SessionFactory factory) throws Exception {
        initPersistentClasses(factory);
        initWrappers();
    }

    private void initPersistentClasses(SessionFactory factory) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        @SuppressWarnings({"unchecked"})
        Collection<ClassMetadata> classMetadata = factory.getAllClassMetadata().values();

        for (ClassMetadata metadata : classMetadata) {
            Class<?> mappedClass = metadata.getMappedClass(EntityMode.POJO);
            if (Modifier.isAbstract(mappedClass.getModifiers())) {
                continue;
            }

            // node factory
            Class<? extends ChangeNode.Factory> nodeFactoryClass = nodeFactory(mappedClass);

            if (nodeFactoryClass == ChangeNode.NullFactory.class) {
                continue;
            }

            ChangeNode.EntityChangeFactory nodeFactory = (ChangeNode.EntityChangeFactory) nodeFactoryClass.newInstance();

            // serializer
            AuditSerializer serializer = initAuditSerializer(mappedClass, EntityAuditSerializer.class);

            // fields
            Field[] fields = getMappedFields(metadata, mappedClass);
            FieldChangeDescriptor[] fieldChangeDescriptors = readFieldDescriptors(fields);

            EntityChangeDescriptor descriptor = new EntityChangeDescriptor(
                    mappedClass,
                    metadata,
                    nodeFactory,
                    serializer,
                    fieldChangeDescriptors
            );

            descriptorMap.put(mappedClass, descriptor);
        }
    }

    private void initWrappers() throws IOException, ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        ClassLoader classLoader = EntityBase.class.getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("classpath*:com/foros/model/**/*Wrapper.class");
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(classLoader);
        for (Resource resource : resources) {
            String wrapperClassName = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            Class<?> wrapperClazz = classLoader.loadClass(wrapperClassName);

            Auditable auditable = wrapperClazz.getAnnotation(Auditable.class);

            if (auditable == null) {
                continue;
            }

            // node factory
            Class<? extends ChangeNode.Factory> nodeFactoryClass = nodeFactory(wrapperClazz);
            ChangeNode.EntityChangeFactory entityChangeFactory = (ChangeNode.EntityChangeFactory) nodeFactoryClass.newInstance();

            // serializer
            AuditSerializer entityAuditSerializer = initAuditSerializer(wrapperClazz, WrapperAuditSerializer.class);

            // fields
            final List<Field> fields = new ArrayList<Field>(); 
            ReflectionUtils.doWithFields(wrapperClazz, new FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    fields.add(field);
                }
            });
            FieldChangeDescriptor[] fieldChangeDescriptors = readFieldDescriptors(fields.toArray(new Field [] {}));


            EntityChangeDescriptor descriptor = new EntityChangeDescriptor(
                    wrapperClazz,
                    null,
                    entityChangeFactory,
                    entityAuditSerializer,
                    fieldChangeDescriptors
            );
            descriptorMap.put(wrapperClazz, descriptor);
        }
    }

    private FieldChangeDescriptor[] readFieldDescriptors(Field ... fields) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        FieldChangeDescriptor[] fieldChangeDescriptors = new FieldChangeDescriptor[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // inspection type
            InspectionType inspectionType = InspectionType.DEFAULT;

            ChangesInspection changesInspection = field.getAnnotation(ChangesInspection.class);
            if (changesInspection != null) {
                inspectionType = changesInspection.type();
            }

            if (inspectionType == InspectionType.NONE) {
                continue;
            }

            // containment type
            ChangeObjectType containmentType = calculateContainment(field);
            // cascade
            boolean cascade = isCascaded(inspectionType, field);

            // serializer
            Class<? extends AuditSerializer> serializerClass = serializerClass(field);

            if (serializerClass == NullAuditSerializer.class) {
                continue;
            }

            if (serializerClass == null) {
                serializerClass = defaultSerializerClass(field, containmentType);
            }

            AuditSerializer serializer = serializerClass.newInstance();

            // node factory
            Class<? extends ChangeNode.Factory> nodeFactoryClass = readFactoryClassFromAnnotation(field);

            if (nodeFactoryClass == ChangeNode.NullFactory.class) {
                continue;
            }

            if (nodeFactoryClass == null) {
                nodeFactoryClass = containmentType.nodeFactory;
            }

            ChangeNode.FieldChangeFactory fieldChangeFactory = (ChangeNode.FieldChangeFactory) nodeFactoryClass.newInstance();

            fieldChangeDescriptors[i] = new FieldChangeDescriptor(field, fieldChangeFactory, cascade, serializer);
        }
        return fieldChangeDescriptors;
    }

    private Class<? extends AuditSerializer> defaultSerializerClass(Field field, ChangeObjectType containmentType) {
        Class<? extends AuditSerializer> serializerClass = null;
        if (containmentType == ChangeObjectType.PRIMITIVE) {
            if (field.getType().equals(BigDecimal.class)) {
                serializerClass = BigDecimalAuditSerializer.class;
            }
        }

        if (serializerClass == null) {
            serializerClass = containmentType.serializer;
        }

        return serializerClass;
    }

    private Class<? extends AuditSerializer> serializerClass(Class<?> clazz) {
        return readSerializerFromAnnotation(clazz);
    }

    private Class<? extends AuditSerializer> serializerClass(Field field) {
        Class<? extends AuditSerializer> res = readSerializerFromAnnotation(field);
        if (res == null) {
            res = readSerializerFromAnnotation(field.getType());
        }
        return res;
    }

    private Class<? extends AuditSerializer> readSerializerFromAnnotation(AnnotatedElement field) {
        Class<? extends AuditSerializer> serializerClass = null;
        Audit audit = field.getAnnotation(Audit.class);

        if (audit != null) {
            serializerClass = audit.serializer();
        }
        return serializerClass == AuditSerializer.class ? null : serializerClass;
    }

    private Class<? extends ChangeNode.Factory> readFactoryClassFromAnnotation(AnnotatedElement field) {
        Class<? extends ChangeNode.Factory> nodeFactory = null;
        Audit audit = field.getAnnotation(Audit.class);

        if (audit != null) {
            nodeFactory = audit.nodeFactory();
        }
        return nodeFactory == ChangeNode.Factory.class ? null : nodeFactory;
    }

    private boolean isCascaded(InspectionType inspectionType, Field field) {
        if (inspectionType == InspectionType.CASCADE) {
            return true;
        }

        if (inspectionType == InspectionType.FIELD) {
            return false;
        }

        if (field == null) {
            return true;
        }

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            return containsUpdateCascadeType(oneToMany.cascade());
        }

        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if (oneToOne != null) {
            return containsUpdateCascadeType(oneToOne.cascade());
        }

        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null) {
            return containsUpdateCascadeType(manyToOne.cascade());
        }

        return false;
    }

    private ChangeObjectType calculateContainment(Field field) {
        Class<?> fieldType = field.getType();
        if (Collection.class.isAssignableFrom(fieldType)) {
            return ChangeObjectType.COLLECTION;
        }

        if (Map.class.isAssignableFrom(fieldType)) {
            throw new IllegalArgumentException(field.toString());
        }

        if (EntityBase.class.isAssignableFrom(fieldType)) {
            return ChangeObjectType.ENTITY;
        }

        return ChangeObjectType.PRIMITIVE;
    }

    private boolean containsUpdateCascadeType(CascadeType[] cascadeTypes) {
        return CollectionUtils.containsAny(
                Arrays.asList(cascadeTypes), Arrays.asList(CascadeType.ALL, CascadeType.MERGE, CascadeType.PERSIST)
        );
    }

    private Class<? extends ChangeNode.Factory> nodeFactory(Class<?> clazz) {
        Class<? extends ChangeNode.Factory> nodeFactoryClass = readFactoryClassFromAnnotation(clazz);

        if (nodeFactoryClass == null) {
            nodeFactoryClass = EntityChange.Factory.class;
        }
        return nodeFactoryClass;
    }

    private AuditSerializer initAuditSerializer(Class<?> mappedClass, Class<? extends AuditSerializer> defaultSerializer) throws InstantiationException, IllegalAccessException {
        Class<? extends AuditSerializer> serializerClass = serializerClass(mappedClass);

        if (serializerClass == null) {
            serializerClass = defaultSerializer;
        }

        return serializerClass.newInstance();
    }

    private Field[] getMappedFields(ClassMetadata metadata, Class<?> mappedClass) throws NoSuchFieldException {
        String[] propertyNames = metadata.getPropertyNames();
        Field[] fields = new Field[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            fields[i] = ReflectionUtils.findField(mappedClass, propertyName);
        }
        return fields;
    }

    private enum ChangeObjectType {
        ENTITY(EntityFieldChange.Factory.class, EntityAuditSerializer.class),
        COLLECTION(CollectionFieldChange.Factory.class, CollectionAuditSerializer.class),
        PRIMITIVE(PrimitiveFieldChange.Factory.class, PrimitiveAuditSerializer.class);

        private Class<? extends ChangeNode.FieldChangeFactory> nodeFactory;
        private Class<? extends AuditSerializer> serializer;

        ChangeObjectType(Class<? extends ChangeNode.FieldChangeFactory> nodeFactory, Class<? extends AuditSerializer> serializer) {
            this.nodeFactory = nodeFactory;
            this.serializer = serializer;
        }
    }
}
