package app.programmatic.ui.common.model;

public class StatusChangeOperation<T, E> {
    private T id;
    private E newStatus;
    private String reason;

    public StatusChangeOperation(T id, E newStatus, String reason) {
        this(id, newStatus);
        this.reason = reason;
    }

    public StatusChangeOperation(T id, E newStatus) {
        this.id = id;
        this.newStatus = newStatus;
    }

    public T getId() {
        return id;
    }

    public E getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }
}
