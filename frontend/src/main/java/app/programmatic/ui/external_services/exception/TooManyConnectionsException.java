package app.programmatic.ui.external_services.exception;

public class TooManyConnectionsException extends ExternalServiceException {

    public TooManyConnectionsException() {
        super("Too many connections");
    }

    public TooManyConnectionsException(String message) {
        super(message);
    }

    public TooManyConnectionsException(Throwable cause) {
        super(cause);
    }

    public TooManyConnectionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
