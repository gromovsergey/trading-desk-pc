package com.foros.session.admin.kwmTool;

public class KWMToolException extends Exception {
    private int toolExitCode;

    public KWMToolException(String message) {
        super(message);
    }

    public KWMToolException(String message, Throwable e) {
        super(message, e);
    }

    public KWMToolException(String message, int exitCode) {
        super(message);
        this.toolExitCode = exitCode;
    }

    public int getToolExitCode() {
        return toolExitCode;
    }
}