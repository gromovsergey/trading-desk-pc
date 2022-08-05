package app.programmatic.ui.fileNew.exception;

public class MoveFileException extends RuntimeException {
    public MoveFileException(String message) {
        super(message);
    }

    public MoveFileException(String message, Throwable cause) {
        super(message, cause);
    }
}