package app.programmatic.ui.flight.dao.model;

import java.util.Set;

public class FieldsToReset {
    private Set<String> fieldsToReset;

    public FieldsToReset(Set<String> fieldsToReset) {
        this.fieldsToReset = fieldsToReset;
    }

    public Set<String> getFieldsToReset() {
        return fieldsToReset;
    }
}
