package app.programmatic.ui.common.tool.javabean;

import app.programmatic.ui.common.model.EntityBase;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public class EntityChangesMap implements EntityChangesGetter {
    private IdentityHashMap<EntityBase<?>, List<PropertyChange>> changesByObject = new IdentityHashMap<>();

    public boolean isEmpty() {
        return changesByObject.isEmpty();
    }

    public void put(EntityBase entity, List<PropertyChange> changes) {
        changesByObject.put(entity, changes);
    }

    public List<PropertyChange> get(EntityBase<?> identity) {
        List<PropertyChange> result = changesByObject.get(identity);
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }

    public void merge(EntityChangesMap otherMap) {
        otherMap.changesByObject.entrySet().stream()
                .forEach( e -> changesByObject.put(e.getKey(), e.getValue()) );
    }


    private static final EmptyEntityChangesMap emptyEntityChanges = new EmptyEntityChangesMap();
    public static EntityChangesMap getEmpty() {
        return emptyEntityChanges;
    }

    private static class EmptyEntityChangesMap extends EntityChangesMap {
        @Override
        public void put(EntityBase entity, List<PropertyChange> changes) {
            throw new UnsupportedOperationException();
        }
    }
}
