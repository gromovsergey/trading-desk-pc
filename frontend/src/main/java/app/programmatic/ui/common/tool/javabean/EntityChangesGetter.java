package app.programmatic.ui.common.tool.javabean;

import app.programmatic.ui.common.model.EntityBase;

import java.util.List;

public interface EntityChangesGetter {
    List<PropertyChange> get(EntityBase<?> identity);
}
