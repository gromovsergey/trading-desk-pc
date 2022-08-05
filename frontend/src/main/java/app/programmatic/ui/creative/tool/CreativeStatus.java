package app.programmatic.ui.creative.tool;

public enum CreativeStatus {
    LIVE(1, 'A'),
    INACTIVE(5, 'I');

    private final int displayStatusId;
    private final char status;

    CreativeStatus(int displayStatusId, char status) {
        this.displayStatusId = displayStatusId;
        this.status = status;
    }

    public int getDisplayStatusId() {
        return displayStatusId;
    }

    public char getStatus() {
        return status;
    }
}