package app.programmatic.ui.creative.dao.model;

import com.foros.rs.client.model.advertising.template.OptionGroup;

import java.util.ArrayList;
import java.util.List;


public class CreativeTemplate {
    private Long id;
    private String name;
    private List<OptionGroup> optionGroups;
    private Boolean expandable;
    private List<CreativeCategory> visualCategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OptionGroup> getOptionGroups() {
        return optionGroups;
    }

    public void setOptionGroups(List<OptionGroup> optionGroups) {
        this.optionGroups = optionGroups;
    }

    public Boolean getExpandable() {
        return expandable;
    }

    public void setExpandable(Boolean expandable) {
        this.expandable = expandable;
    }

    public List<CreativeCategory> getVisualCategories() {
        return visualCategories;
    }

    public void setVisualCategories(List<CreativeCategory> visualCategories) {
        this.visualCategories = visualCategories;
    }
}
