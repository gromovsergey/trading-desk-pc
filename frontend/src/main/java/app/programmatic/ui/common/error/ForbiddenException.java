package app.programmatic.ui.common.error;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
    }

    public ForbiddenException(String msg) {
        super(msg);
    }
}
