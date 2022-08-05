package app.programmatic.ui.external_services.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException() {
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(Throwable cause) {
        super(cause);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
