package com.foros.changes.inspection.changeNode;

import com.foros.AbstractUnitTest;
import com.foros.audit.serialize.serializer.NullAuditSerializer;
import com.foros.changes.inspection.ChangeDescriptorRegistry;
import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import group.Unit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class EntityChangeTest extends AbstractUnitTest {
    private final NullAuditSerializer serializer = new NullAuditSerializer();
    private EntityChangeDescriptor entityChangeDescriptor;
    private EntityChangeDescriptor entity2ChangeDescriptor;
    private ChangeDescriptorRegistry registry = new ChangeDescriptorRegistryMock();
    private HashMap<Object, EntityChangeNode> changes;

    @Before
    public void setUp() throws Exception {
        entityChangeDescriptor = new EntityChangeDescriptor(Entity.class, null, new EntityChange.Factory(), serializer, new FieldChangeDescriptor[]{
                fcd(Entity.class, "num", new PrimitiveFieldChange.Factory(), false),

                fcd(Entity.class, "entity", new EntityFieldChange.Factory(), false),
                fcd(Entity.class, "entityCascade", new EntityFieldChange.Factory(), true),

                fcd(Entity.class, "entities", new CollectionFieldChange.Factory(), false),
                fcd(Entity.class, "entitiesCascade", new CollectionFieldChange.Factory(), true),
        });

        entity2ChangeDescriptor = new EntityChangeDescriptor(Entity2.class, null, new EntityChange.Factory(), serializer, new FieldChangeDescriptor[]{
                fcd(Entity2.class, "value", new PrimitiveFieldChange.Factory(), false)
        });

        changes = new HashMap<>();
    }

    @Test
    public void add() {
        Entity entity = new Entity();
        entity.num = 12L;

        entity.entity = new Entity2("str1");
        entity.entityCascade = new Entity2("str2");

        List<Entity2> entitiesList = Arrays.asList(new Entity2("str3"), new Entity2("str4"));
        entity.entities = new LinkedHashSet<Entity2>(entitiesList);

        List<Entity2> entitiesCascadeList = Arrays.asList(new Entity2("str4"), new Entity2("str5"));
        entity.entitiesCascade = new LinkedHashSet<Entity2>(entitiesCascadeList);

        EntityChangeNode rootChange = addEntityChange(entity);

        addEntity2Change(entity.entityCascade, ChangeType.ADD);
        addEntity2Change(entitiesList.get(0), ChangeType.ADD);
        addEntity2Change(entitiesCascadeList.get(0), ChangeType.ADD);
        addEntity2Change(entitiesCascadeList.get(1), ChangeType.ADD);

        fields(entity.entityCascade);

        rootChange.prepare(new PrepareChangesContext(changes, registry));

        assertEquals(ChangeType.ADD, rootChange.getChangeType());
        assertSame(entity, rootChange.getLastDefinedValue());

        List<ChangeNode> children = childChanges(rootChange);
        assertNotNull(children);
        assertEquals(5, children.size());

        // num
        ChangeNode numChangeNode = children.get(0);
        assertNotNull(numChangeNode);
        assertEquals(ChangeType.ADD, numChangeNode.getChangeType());
        assertSame(entity.num, numChangeNode.getLastDefinedValue());
        assertTrue(childChanges(numChangeNode).isEmpty());

        // entity
        ChangeNode entityChangeNode = children.get(1);
        assertEquals(ChangeType.ADD, entityChangeNode.getChangeType());
        assertSame(entity.entity, entityChangeNode.getLastDefinedValue());
        assertEquals(1, childChanges(entityChangeNode).size());

        // entityCascade
        ChangeNode entityCascadeChangeNode = children.get(2);
        assertEquals(ChangeType.ADD, entityCascadeChangeNode.getChangeType());
        assertSame(entity.entityCascade, entityCascadeChangeNode.getLastDefinedValue());
        List<ChangeNode> entityChanges = childChanges(entityCascadeChangeNode);
        assertEquals(1, entityChanges.size());

        // entities
        ChangeNode entitiesChangeNode = children.get(3);
        assertEquals(ChangeType.ADD, entitiesChangeNode.getChangeType());
        assertSame(entity.entities, entitiesChangeNode.getLastDefinedValue());
        List<ChangeNode> entitiesChanges = childChanges(entitiesChangeNode);
        assertEquals(2, entitiesChanges.size());
        for (ChangeNode node : entitiesChanges) {
            assertNotNull(node);
            assertTrue(childChanges(node).size() == 1);
        }

        // entityCascade
        ChangeNode entitiesCascadeChangeNode = children.get(4);
        assertEquals(ChangeType.ADD, entitiesCascadeChangeNode.getChangeType());
        assertSame(entity.entitiesCascade, entitiesCascadeChangeNode.getLastDefinedValue());
        List<ChangeNode> entitiesCascadeChanges = childChanges(entitiesCascadeChangeNode);
        assertEquals(2, entitiesCascadeChanges.size());
        for (ChangeNode node : entitiesCascadeChanges) {
            assertNotNull(node);
            assertEquals(1, childChanges(node).size());
        }
    }

    public static class Entity  {
        private Long num;
        private Entity2 entity;
        private Entity2 entityCascade;
        private Set<Entity2> entities = new LinkedHashSet<Entity2>();
        private Set<Entity2> entitiesCascade = new LinkedHashSet<Entity2>();
    }

    public static class Entity2  {
        public Entity2(String value) {
            this.value = value;
        }

        private String value;
    }

    private class ChangeDescriptorRegistryMock implements ChangeDescriptorRegistry {
        @Override
        public EntityChangeDescriptor getDescriptor(Object object) {
            if (object instanceof Entity) {
                return entityChangeDescriptor;
            } else if (object instanceof Entity2) {
                return entity2ChangeDescriptor;
            }
            return null;
        }

    }

    private EntityChangeNode addEntityChange(Entity entity) {
        EntityChangeNode entityChange = entityChangeDescriptor.newEntityChange(entity, ChangeType.ADD,
                allNull(entityChangeDescriptor),
                fields(entity)
        );

        changes.put(entity, entityChange);
        return entityChange;
    }

    private void addEntity2Change(Entity2 entityCascade, ChangeType changeType) {
        EntityChangeNode entity2Change = entity2ChangeDescriptor.newEntityChange(entityCascade, changeType,
                allNull(entity2ChangeDescriptor),
                fields(entityCascade)
        );
        changes.put(entityCascade, entity2Change);
    }

    @SuppressWarnings({"unchecked"})
    private List<ChangeNode> childChanges(ChangeNode changeNode) {
        return IteratorUtils.toList(changeNode.getChildNodes());
    }


    private Object[] allNull(EntityChangeDescriptor entityChangeDescriptor) {
        return new Object[entityChangeDescriptor.fieldsCount()];
    }

    private Object[] fields(Entity entity) {
        return new Object[] {
                entity.num,
                entity.entity,
                entity.entityCascade,
                entity.entities,
                entity.entitiesCascade
        };
    }

    private Object[] fields(Entity2 entity) {
        return new Object[] {
                entity.value
        };
    }

    private FieldChangeDescriptor fcd(Class<?> entityClass, String field, ChangeNode.FieldChangeFactory fieldChangeFactory, boolean cascade) {
        try {
            return new FieldChangeDescriptor(entityClass.getDeclaredField(field), fieldChangeFactory, cascade, serializer);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
