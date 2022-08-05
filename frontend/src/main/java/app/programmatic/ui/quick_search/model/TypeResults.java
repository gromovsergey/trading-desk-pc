package app.programmatic.ui.quick_search.model;

import java.util.ArrayList;
import java.util.List;

public class TypeResults {
    private Type type;
    private List<QuickSearchResultItem> items = new ArrayList<>();

    public TypeResults(Type type) {
        this.type = type;
    }

    public void addItem(QuickSearchResultItem item) {
        items.add(item);
    }

    public List<QuickSearchResultItem> getItems() {
        return items;
    }

    public Type getType() {
        return type;
    }
}
