package app.programmatic.ui.quick_search.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

public class QuickSearchResultItem {
    private Long id;
    private Type type;
    private String name;
    private MajorDisplayStatus displayStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }
}
