package com.foros.changes.inspection.changeNode;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForceFieldChangeEntityChange extends EntityChange {
    /** @noinspection unchecked*/
    private static final ExtensionProperty<Map<String, FieldChangeFactory>> FIELD_CHANGE_PROPERTY = new ExtensionProperty(Map.class);

    public ForceFieldChangeEntityChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, changes);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        fillChanges();
        super.prepareInternal(context);
    }

    private void fillChanges() {
        Map<String, FieldChangeFactory> forcedChanges = ((EntityBase) object).getProperty(FIELD_CHANGE_PROPERTY);
        if (forcedChanges == null) {
            return;
        }
        for (int i = 0; i < descriptor.getFieldChangeDescriptors().length; i++) {
            FieldChangeDescriptor fieldChangeDescriptor = descriptor.getFieldChangeDescriptors()[i];
            if (fieldChangeDescriptor == null) {
                continue;
            }
            FieldChangeFactory fieldChangeFactory = forcedChanges.get(fieldChangeDescriptor.getName());
            if (fieldChangeFactory == null) {
                continue;
            }
            changes[i] = fieldChangeFactory.newInstance(fieldChangeDescriptor, null, null);
        }
    }

    public static void addCollectionChange(EntityBase entity, String property, Object child, ChangeType childChangeType) {
        Map<String, FieldChangeFactory> fieldChangeFactories = entity.getProperty(FIELD_CHANGE_PROPERTY);
        if (fieldChangeFactories == null) {
            fieldChangeFactories = new HashMap<>(2);
            entity.setProperty(FIELD_CHANGE_PROPERTY, fieldChangeFactories);
        }
        ForcedCollectionFieldChangeFactory fieldChangeFactory = (ForcedCollectionFieldChangeFactory) fieldChangeFactories.get(property);
        if (fieldChangeFactory == null) {
            fieldChangeFactory = new ForcedCollectionFieldChangeFactory();
            fieldChangeFactories.put(property, fieldChangeFactory);
        }
        fieldChangeFactory.add(child, childChangeType);
    }

    public static class Factory implements EntityChangeFactory {
        @Override
        public final EntityChange newInstance(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new ForceFieldChangeEntityChange(descriptor, object, changeType, changes);
        }
    }

    private static class ForcedCollectionFieldChangeFactory implements FieldChangeFactory {
        private final List<Object> newValues = new ArrayList<>();
        private final List<Object> oldValues = new ArrayList<>();


        public void add(Object child, ChangeType childChangeType) {
            switch (childChangeType) {
                case UNCHANGED:
                case UPDATE:
                    newValues.add(child);
                    oldValues.add(child);
                    break;
                case ADD:
                    newValues.add(child);
                    break;
                case REMOVE:
                    oldValues.add(child);
                    break;
            }
        }

        @Override
        public FieldChange newInstance(FieldChangeDescriptor descriptor, Object oldValue, Object newValue) {
            return new CollectionFieldChange(descriptor, oldValues, newValues);
        }
    }
}
