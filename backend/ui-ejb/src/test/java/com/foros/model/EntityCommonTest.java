package com.foros.model;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.AccountFinancialSettings;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialData;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelExpressionLink;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.PlacementBlacklistChannel;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.site.SiteCreativePK;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.model.site.Tag;
import com.foros.model.site.TagAuctionSettings;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroup;
import com.foros.util.bean.BeanHelper;
import com.foros.util.changes.ChangesSupportList;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassSearcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Unit;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class EntityCommonTest {
    private static Logger logger = Logger.getLogger(EntityCommonTest.class.getName());
    private static Set<Class> entityClasses;
    private Map<Class, List<AssertionError>> errors;

    @BeforeClass
    public static void classSetUp() throws Exception {
        ClassSearcher classSearcher = new ClassSearcher("com.foros.model", true);

        entityClasses = classSearcher.search(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                int mod = clazz.getModifiers();

                boolean isTestClass = clazz.getEnclosingClass() != null && clazz.getEnclosingClass().getSimpleName().endsWith("Test");
                boolean filteredClass = clazz == OptionGroup.class;

                return EntityBase.class.isAssignableFrom(clazz) &&
                        !EntityBase.class.getName().equals(clazz.getName()) &&
                        !ApprovableEntity.class.getName().equals(clazz.getName()) &&
                        !Modifier.isInterface(mod) &&
                        Modifier.isPublic(mod) &&
                        !Modifier.isAbstract(mod) &&
                        !isTestClass &&
                        !filteredClass;
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        errors = new HashMap<Class, List<AssertionError>>();
    }

    public EntityBase newEntityInstance(Class clazz)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return (EntityBase) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Constructor constructor = clazz.getDeclaredConstructor();
            boolean isAccessible = constructor.isAccessible();
            try {
                constructor.setAccessible(true);
                return (EntityBase) constructor.newInstance();
            } finally {
                constructor.setAccessible(isAccessible);
            }
        }
    }

    @Test
    public void setters() throws Exception {
        for (Class entityClass : entityClasses) {
            EntityBase entity = newEntityInstance(entityClass);

            Method[] methods = entityClass.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                // TODO setTextAdservingMode and setFinancialSettings of AdvertiserAccount are added in exclusion while fixing tests because of different behaviour in respective getter and / setter
                // Needs to fix once implementation will be generalize
                if (methodName.startsWith("set") && !methodName.equals("setChanges") &&
                    !methodName.endsWith("Flag") && !(methodName.equals("setRole")) &&
                    !(entityClass.getSimpleName().equals(AdvertiserAccount.class.getSimpleName()) &&
                        (methodName.equals("setAgency"))) &&
                    !(entityClass.getSimpleName().equals(ExpressionChannel.class.getSimpleName()) &&
                        (methodName.equals("setUsedChannels"))) &&
                    !(methodName.equals("setPublicChannel") &&
                        Arrays.equals(method.getParameterTypes(), new Class[]{Boolean.TYPE})) &&
                    !(entityClass.getSimpleName().equals(ChannelExpressionLink.class.getSimpleName())) &&
                        methodName.equals("setExpression")) {
                    String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                    Field field;
                    try {
                        field = getField(entityClass, fieldName);
                    } catch (NoSuchFieldException e) {
                        try {
                            fieldName = methodName.substring(3);
                            field = getField(entityClass, fieldName);
                        } catch (NoSuchFieldException oops) {
                            continue;
                        }
                    }
                    if (field.getAnnotation(Transient.class) != null) {
                        continue;
                    }

                    Method getMethod;
                    if (Arrays.equals(method.getParameterTypes(), new Class[]{Boolean.TYPE})) {
                        getMethod = entityClass.getMethod("is" + methodName.substring(3));
                    } else {
                        getMethod = entityClass.getMethod("get" + methodName.substring(3));
                    }
                    try {
                        Object value = generateValue(method, field, entity, getMethod);
                        if (value instanceof Collection && getMethod.invoke(entity) != null) {
                            field.setAccessible(true);
                            field.set(entity, null);
                            field.setAccessible(false);
                        }

                        method.invoke(entity, value);
                        try {
                            assertEquals(method + " didn't updated object's value", value, getMethod.invoke(entity));
                            assertTrue(method + " didn't called registerChange() or called it incorrectly", entity.getChanges().contains(fieldName));
                        } catch (AssertionError error) {
                            addError(entityClass, error);
                        }
                    } catch (Exception e) {
                        if (!(e.getCause() instanceof UnsupportedOperationException)) {
                            addError(entityClass, new AssertionError(e.getMessage()));
                        }
                    }
                }
            }
        }

        verifyErrors();
    }

    @Test
    public void getters() throws Exception {
        for (Class entityClass : entityClasses) {
            EntityBase entity = newEntityInstance(entityClass);

            List<Method> methods = new ArrayList<Method>();

            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(entityClass, Object.class).getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : descriptors) {
                if (propertyDescriptor.getWriteMethod() == null) {
                    continue;
                }

                Method method = propertyDescriptor.getReadMethod();
                if (method!= null && method.getParameterTypes().length == 0) {
                    methods.add(propertyDescriptor.getReadMethod());
                }
            }

            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get") && !isGetterIgnored(entityClass, method)) {
                    String info = "Invoke method : " + method.getName();
                    try {
                        Object result = method.invoke(entity);
                        if (Collection.class.isAssignableFrom(method.getReturnType())) {
                            if (result != null) {
                                if (!(result instanceof ChangesSupportSet || result instanceof ChangesSupportList)) {
                                    addError(entityClass, new AssertionError(info + ": collection is not support changes"));
                                } else {
                                    Object obj = new Object();
                                    ((Collection) result).add(obj);
                                    ((Collection) result).remove(obj);
                                    String propertyName = fetchPropertyName(methodName);
                                    if (!entity.isChanged(propertyName)) {
                                        addError(entityClass, new AssertionError(info + ": registration change for collection " + propertyName + " failed."));
                                    }
                                }
                            } else {
                                addError(entityClass, new AssertionError(info + ": returned collection is null."));
                            }
                        }
                    } catch (Exception error) {
                        if (!(error.getCause() instanceof UnsupportedOperationException)) {
                            addError(entityClass, new AssertionError(info));
                        }
                    }
                }
            }
        }

        verifyErrors();
    }

    @Test
    public void tableNames() throws Exception {
        for (Class entityClass : entityClasses) {
            Table table = (Table) entityClass.getAnnotation(Table.class);
            if (table != null) {
                String tableName = table.name();

                try {
                    assertEquals("Table name " + tableName + " should contain only capitals", tableName.toUpperCase(), tableName);
                } catch (AssertionError error) {
                    addError(entityClass, error);
                }
            }
        }

        verifyErrors();
    }

    @Test
    public void equalsAndHashCodeEqualObjects() throws Exception {
        for (Class entityClass : filterClassesForEqualsAndHashcodeTests(entityClasses)) {
            Method equalsMethod = entityClass.getMethod("equals", Object.class);
            Method hashCodeMethod = entityClass.getMethod("hashCode");

            EntityBase entity1 = createEntity(entityClass, 1L);
            EntityBase entity2 = createEntity(entityClass, 1L);


            try {
                assertTrue(equalsMethod + " didn't return true", (Boolean) equalsMethod.invoke(entity1, entity2));
                assertTrue(equalsMethod + " isn't symmetric", (Boolean) equalsMethod.invoke(entity2, entity1));
                assertFalse(equalsMethod + " didn't return false for different classes", (Boolean) equalsMethod.invoke(entity1, new EntityCommonTest()));
                assertEquals(hashCodeMethod + " didn't return equal hashCode", hashCodeMethod.invoke(entity1), hashCodeMethod.invoke(entity2));
            } catch (AssertionError error) {
                addError(entityClass, error);
            }
        }

        verifyErrors();
    }

    @Test
    public void equalsObjects() throws Exception {
        for (Class entityClass : filterClassesForEqualsAndHashcodeTests(entityClasses)) {
            Method equalsMethod = entityClass.getMethod("equals", Object.class);

            EntityBase entityWithIdLeft = createEntity(entityClass, 1L);
            EntityBase entityWithIdRight = createEntity(entityClass, 1L);

            EntityBase entityWithNoIdLeft = createEntityWithName(entityClass, "test");
            EntityBase entityWithNoIdRight = createEntityWithName(entityClass, "test");

            try {
                assertTrue(equalsMethod + " didn't return true with Id", (Boolean) equalsMethod.invoke(entityWithIdLeft, entityWithIdRight));
                assertTrue(equalsMethod + " isn't symmetric with Id", (Boolean) equalsMethod.invoke(entityWithIdRight, entityWithIdLeft));

                if (entityWithNoIdLeft != null) {
                    assertTrue(equalsMethod + " didn't return true with no Id", (Boolean) equalsMethod.invoke(entityWithNoIdLeft, entityWithNoIdRight));
                    assertTrue(equalsMethod + " isn't symmetric with no Id", (Boolean) equalsMethod.invoke(entityWithNoIdRight, entityWithNoIdLeft));
                }

            } catch (AssertionError error) {
                addError(entityClass, error);
            }
        }

        verifyErrors();
    }

    @Test
    public void equalsAndHashCodeUnEqualObjects() throws Exception {
        for (Class entityClass : filterClassesForEqualsAndHashcodeTests(entityClasses)) {
            Method equalsMethod = entityClass.getMethod("equals", Object.class);
            Method hashCodeMethod = entityClass.getMethod("hashCode");

            EntityBase entity1 = createEntity(entityClass, 1L);
            EntityBase entity2 = createEntity(entityClass, 2L);

            try {
                assertFalse(equalsMethod + " didn't return false", (Boolean) equalsMethod.invoke(entity1, entity2));
                assertFalse(equalsMethod + " isn't symmetric", (Boolean) equalsMethod.invoke(entity2, entity1));

                if (hashCodeMethod.invoke(entity1).equals(hashCodeMethod.invoke(entity2))) {
                    logger.warning(hashCodeMethod + " returned equal hashCode for different objects. It's better to return different hasCodes.");
                }
            } catch (AssertionError error) {
                addError(entityClass, error);
            }
        }

        verifyErrors();
    }

    @Test
    public void identifable() throws Exception {
        boolean failed = false;

        for (Class clazz : entityClasses) {
            Field id = BeanHelper.findField(clazz, "id");
            if (id == null || id.getType() != Long.class) {
                continue;
            }

            if (!Identifiable.class.isAssignableFrom(clazz)) {
                logger.severe(clazz.getName() + " doesn't implement Identifable");
                failed = true;
            }
        }
        assertFalse("See errors above", failed);
    }

    private String fetchPropertyName(String methodName) {
        String firstLetter = methodName.substring(3, 4);
        String name = methodName.substring(4);

        return firstLetter.toLowerCase() + name;
    }

    private Set<Class> filterClassesForEqualsAndHashcodeTests(Set<Class> entityClasses) {
        Set<Class> classes = new HashSet<Class>(entityClasses);
        HashSet<Class> doNotTest = new HashSet<Class>() {{
            add(VersionEntityBase.class);
            add(Tag.class);
            add(CampaignCreative.class);
            add(CCGKeyword.class);
            add(CampaignCreativeGroup.class);
            add(BehavioralParameters.class);
            add(CampaignAllocation.class);
            add(CampaignCredit.class);
            add(CampaignCreditAllocation.class);
            add(AccountAuctionSettings.class);
            add(TagAuctionSettings.class);
            add(GeoChannel.class);
            add(AdvertisingFinancialData.class);
            add(SizeType.class);
            add(CreativeSize.class);
            add(PlacementBlacklist.class);
            add(OptionEnumValue.class);
        }};
        classes.removeAll(doNotTest);
        return classes;
    }

    private Object generateValue(Method setMethod, Field field, EntityBase entity, Method getMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object value = null;
        Class clazz = setMethod.getParameterTypes()[0];
        if (entity instanceof CampaignCreativeGroup && field.getName().equals("flags")) {
            value = ((CampaignCreativeGroup) entity).getFlags() | CampaignCreativeGroup.OPTIMIZE_CREATIVE_WEIGHT;
        } else if (String.class.isAssignableFrom(clazz)) {
            value = "new value";
        } else if (Number.class.isAssignableFrom(clazz)) {
            value = clazz.getConstructor(String.class).newInstance("3");
        } else if (Boolean.class.equals(clazz)) {
            value = Boolean.TRUE;
        } else if (Set.class.isAssignableFrom(clazz)) {
            value = new LinkedHashSet();
        } else if (List.class.isAssignableFrom(clazz)) {
            value = new LinkedList();
        } else if (Collection.class.isAssignableFrom(clazz)) {
        } else if (clazz.isEnum()) {
            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            field.setAccessible(false);

            Object[] enumConstants;
            if (Status.class.isAssignableFrom(clazz)) {
                AllowedStatuses allowedStatusesAnnotation = entity.getClass().getAnnotation(AllowedStatuses.class);
                if (allowedStatusesAnnotation == null) {
                    throw new RuntimeException("Class :" + entity.getClass().getName() + " has no AllowedStatuses annotation");
                }
                enumConstants = allowedStatusesAnnotation.values();
            } else {
                enumConstants = clazz.getEnumConstants();
            }

            if (fieldValue instanceof Character ? (Character) fieldValue != '\u0000' : fieldValue != null) {
                fieldValue = getMethod.invoke(entity);
                for (Object enumConstant : enumConstants) {
                    if (!enumConstant.equals(fieldValue)) {
                        value = enumConstant;
                        break;
                    }
                }
            } else {
                value = enumConstants[0];
            }
        } else if (Timestamp.class.isAssignableFrom(clazz)) {
            value = new Timestamp(System.currentTimeMillis());
        } else if (Character.class.isAssignableFrom(clazz)) {
            value = 'n';
        } else if (clazz.isPrimitive()) {
            if (clazz.equals(Character.TYPE)) {
                value = 'n';
            } else if (clazz.equals(Byte.TYPE)) {
                value = (byte) 3;
            } else if (clazz.equals(Short.TYPE)) {
                value = (short) 3;
            } else if (clazz.equals(Integer.TYPE)) {
                value = 3;
            } else if (clazz.equals(Long.TYPE)) {
                value = (long) 3;
            } else if (clazz.equals(Float.TYPE)) {
                value = (float) 3;
            } else if (clazz.equals(Double.TYPE)) {
                value = (double) 3;
            } else if (clazz.equals(Boolean.TYPE)) {
                value = true;
            }
        } else if (LocalizableName.class.isAssignableFrom(clazz)) {
            value = new LocalizableName("new value", "new key");
        } else if (AccountFinancialSettings.class.equals(clazz)) {
            if (entity instanceof AdvertisingAccountBase) {
                value = new AdvertisingFinancialSettings();
            } else {
                value = new AccountsPayableFinancialSettings();
            }
        } else {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                if (Channel.class.isAssignableFrom(clazz)) {
                    value = new BehavioralChannel();
                }
            } else if (field.getAnnotation(EmbeddedId.class) != null) {
                value = generateEmbeddedId(1l, field);
            } else {
                value = clazz.newInstance();
            }
        }

        return value;
    }

    private void setPkFieldValue(Object entity, Field field, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class clazz = field.getType();
        field.setAccessible(true);
        if (String.class.isAssignableFrom(clazz)) {
            field.set(entity, value.toString());
        } else if (clazz.isPrimitive()) {
            if (clazz.equals(Integer.TYPE)) {
                field.setInt(entity, Integer.valueOf(value.toString()));
            } else if (clazz.equals(Long.TYPE)) {
                field.setLong(entity, Long.valueOf(value.toString()));
            }
        } else {
            field.set(entity, value);
        }
        field.setAccessible(false);
    }

    private Field getField(Class clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (!Object.class.getName().equals(superClass.getName())) {
                return getField(superClass, name);
            } else {
                throw e;
            }
        }
    }

    private EntityBase createEntity(Class entityClass, Long id) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        EntityBase entity = newEntityInstance(entityClass);
        try {
            entityClass.getMethod("setId", Long.class).invoke(entity, id);
        } catch (NoSuchMethodException e) {
            Class baseEntityClass = entityClass;
            while (!baseEntityClass.equals(Object.class)) {
                boolean fieldFound = false;
                for (Field field : baseEntityClass.getDeclaredFields()) {
                    if (field.getAnnotation(Id.class) != null) {
                        setPkFieldValue(entity, field, id);
                        fieldFound = true;
                    } else if (field.getAnnotation(EmbeddedId.class) != null) {
                        Object pk = generateEmbeddedId(id, field);
                        field.setAccessible(true);
                        field.set(entity, pk);
                        fieldFound = true;
                    }
                }
                if (fieldFound) {
                    break;
                }
                baseEntityClass = baseEntityClass.getSuperclass();
            }
        }

        if (entity instanceof CreativeCategory) {
            CreativeCategory creativeCategory = (CreativeCategory) entity;
            creativeCategory.setDefaultName("name " + id);
            creativeCategory.setType(CreativeCategoryType.CONTENT);
        } else if (entity instanceof CCGKeyword) {
            CCGKeyword ccgKeyword = (CCGKeyword) entity;
            ccgKeyword.setCreativeGroup(new CampaignCreativeGroup(id));
            ccgKeyword.setOriginalKeyword("original keyword " + id);
        } else if (entity instanceof Timezone) {
            Timezone timezone = (Timezone) entity;
            timezone.setKey("key" + id);
        } else if (entity instanceof ThirdPartyCreative) {
            ((ThirdPartyCreative) entity).setId(new SiteCreativePK(id, id));
        }
        return entity;
    }

    private Object generateEmbeddedId(Long id, Field field) throws InstantiationException, IllegalAccessException {
        Object pk;
        pk = field.getType().newInstance();
        for (Field pkField : field.getType().getDeclaredFields()) {
            Object pkFieldValue;
            if (Identifiable.class.isAssignableFrom(pkField.getType())) {
                Identifiable objectPK = (Identifiable) pkField.getType().newInstance();
                objectPK.setId(id);
                pkFieldValue = objectPK;
            } else if (pkField.getType() == ClobParamType.class) {
                pkFieldValue = ClobParamType.ADV_TERMS;
            } else if (pkField.getType() == String.class) {
                pkFieldValue = String.valueOf(id);
            } else {
                pkFieldValue = id;
            }
            pkField.setAccessible(true);
            pkField.set(pk, pkFieldValue);
        }
        return pk;
    }

    private EntityBase createEntityWithName(Class entityClass, String name)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        EntityBase entity = newEntityInstance(entityClass);
        try {
            entityClass.getMethod("setName", String.class).invoke(entity, name);
        } catch (NoSuchMethodException e) {
            return null;
        }

        return entity;
    }

    private void verifyErrors() {
        if (errors.isEmpty()) {
            return;
        }

        logger.warning("Test FAILED! Details:");
        int failedCount = 0;
        for (Map.Entry<Class, List<AssertionError>> error : errors.entrySet()) {
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < error.getValue().size(); i++) {
                AssertionError errorDetail = error.getValue().get(i);
                msg.append("\n       ").append(errorDetail.getMessage());
                failedCount++;
            }
            logger.warning("Class: " + error.getKey() + msg);
        }

        fail(failedCount + " Tests failed.");
    }

    private void addError(Class entityClass, AssertionError error) {
        List<AssertionError> assertionErrors = errors.get(entityClass);
        if (assertionErrors == null) {
            assertionErrors = new LinkedList<AssertionError>();
            errors.put(entityClass, assertionErrors);
        }
        errors.get(entityClass).add(error);
    }

    @Test
    public void primaryKeyPropertyReadable() throws Exception {
        for (Class clazz : entityClasses) {

            String idName = null;
            for (Field field: clazz.getDeclaredFields()) {
                Id id = field.getAnnotation(Id.class);
                if (id != null) {
                    idName = field.getName();
                    break;
                }
            }

            if (idName == null) {
                continue;
            }

            Method getterMethod = null;
            for (PropertyDescriptor descriptor: Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (descriptor.getName().equals(idName) && descriptor.getReadMethod() != null) {
                    getterMethod = descriptor.getReadMethod();
                    break;
                }
            }

            if (getterMethod == null) {
                StringBuilder error = new StringBuilder();
                error.append("There is no appropriate getter for primary key property '");
                error.append(clazz.toString());
                error.append(".");
                error.append(idName);
                error.append("'");

                addError(clazz, new AssertionError(error) );
            }
        }
        verifyErrors();
    }

    private boolean isGetterIgnored(Class entityClass, Method method) {
        return entityClass == PlacementBlacklist.class && method.getName().equals("getReason") ||
                entityClass == PlacementBlacklistChannel.class && method.getName().equals("getPlacementsBlacklist");
    }

    @Test
    public void testEmbeddable() throws Exception {
        ClassSearcher classSearcher = new ClassSearcher("com.foros.model", true);

        Set<Class> embeddableClasses = classSearcher.search(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.getAnnotation(Embeddable.class) != null;
            }
        });

        for (Class clazz : embeddableClasses) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                assertTrue(clazz.getMethod("get" + WordUtils.capitalize(field.getName()), null) != null);
            }
        }
    }
}
