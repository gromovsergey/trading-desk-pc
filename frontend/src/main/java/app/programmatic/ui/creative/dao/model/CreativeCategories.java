package app.programmatic.ui.creative.dao.model;

import java.util.List;

public class CreativeCategories {
    private List<CreativeCategory> contentCategories;
    private List<CreativeCategory> visualCategories;

    public CreativeCategories(List<CreativeCategory> contentCategories, List<CreativeCategory> visualCategories) {
        this.contentCategories = contentCategories;
        this.visualCategories = visualCategories;
    }

    public List<CreativeCategory> getContentCategories() {
        return contentCategories;
    }

    public List<CreativeCategory> getVisualCategories() {
        return visualCategories;
    }
}
