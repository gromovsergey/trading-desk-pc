package app.programmatic.ui.flight.tool;

public class BlackWhiteIds {
    private Long whiteListId;
    private Long blackListId;

    public BlackWhiteIds(Long whiteListId, Long blackListId) {
        this.whiteListId = whiteListId;
        this.blackListId = blackListId;
    }

    public Long getWhiteListId() {
        return whiteListId;
    }

    public Long getBlackListId() {
        return blackListId;
    }
}
