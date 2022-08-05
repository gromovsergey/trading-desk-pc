package app.programmatic.ui.common.validation.exception;

public class EntityNotFoundException extends javax.persistence.EntityNotFoundException {
    private Object id;

    public EntityNotFoundException(Object id) {
        this.id = id;
    }

    public Object getId() {
        return id;
    }
}
