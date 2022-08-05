package com.foros.session.creative;

public class LivePreviewException extends RuntimeException {
    private final LivePreviewResult previewResult;

    public LivePreviewException(LivePreviewResult previewResult, Exception exception) {
        super("Can,t generate preview", exception);
        this.previewResult = previewResult;
    }

    public LivePreviewResult getPreviewResult() {
        return previewResult;
    }
}
