package com.foros.model.quicksearch;

import com.foros.model.DisplayStatus;

public class QuickSearchResultItem {
    private Long id;
    private Type type;
    private String name;
    private DisplayStatus displayStatus;

    public QuickSearchResultItem(Type type, Long id, String name, DisplayStatus displayStatus) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.displayStatus = displayStatus;
    }

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

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        QuickSearchResultItem that = (QuickSearchResultItem) o;

        if (type != that.type)
            return false;
        if (!id.equals(that.id))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}

