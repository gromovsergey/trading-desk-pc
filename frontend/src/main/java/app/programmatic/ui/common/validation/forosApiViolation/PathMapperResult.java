package app.programmatic.ui.common.validation.forosApiViolation;

public class PathMapperResult {
    private String newPath;
    private boolean isChanged;

    public PathMapperResult(String newPath, boolean isChanged) {
        this.newPath = newPath;
        this.isChanged = isChanged;
    }

    public String getNewPath() {
        return newPath;
    }

    public boolean isChanged() {
        return isChanged;
    }
}
