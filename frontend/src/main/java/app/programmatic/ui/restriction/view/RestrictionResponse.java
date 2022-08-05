package app.programmatic.ui.restriction.view;

public class RestrictionResponse {
    private Long id;
    private boolean isAllowed;

    public RestrictionResponse(Long id, boolean isAllowed) {
        this.isAllowed = isAllowed;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isAllowed() {
        return isAllowed;
    }
}
