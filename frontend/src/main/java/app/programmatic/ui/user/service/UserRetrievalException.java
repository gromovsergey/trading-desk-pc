package app.programmatic.ui.user.service;

public class UserRetrievalException extends Exception {
    public enum Type {
        MULTI_LOGIN,
        INACTIVE,
        DELETED
    }

    private Type type;
    private String message;

    public UserRetrievalException(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return "UserRetrievalException{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
