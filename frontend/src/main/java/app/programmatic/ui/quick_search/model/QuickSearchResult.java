package app.programmatic.ui.quick_search.model;

import java.util.*;

public class QuickSearchResult {
    List<TypeResults> types = new ArrayList<>();

    public void add(QuickSearchResultItem item) {
        TypeResults type = types.stream().filter(t -> t.getType().equals(item.getType())).findFirst().orElse(null);

        if (type == null) {
            type = new TypeResults(item.getType());
            types.add(type);
        }

        type.addItem(item);
    }

    public List<TypeResults> getTypes() {
        return types;
    }

}
