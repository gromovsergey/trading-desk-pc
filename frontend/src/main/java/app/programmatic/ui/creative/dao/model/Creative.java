package app.programmatic.ui.creative.dao.model;

import com.foros.rs.client.model.advertising.campaign.AdOption;

import java.util.List;

public class Creative extends CreativeStat {
    private List<AdOption> options;
    private List<CreativeCategory> contentCategories;
    private List<CreativeCategory> visualCategories;
    private Long version;

    public List<AdOption> getOptions() {
        return options;
    }

    public void setOptions(List<AdOption> options) {
        this.options = options;
    }

    public List<CreativeCategory> getContentCategories() {
        return contentCategories;
    }

    public void setContentCategories(List<CreativeCategory> contentCategories) {
        this.contentCategories = contentCategories;
    }

    public List<CreativeCategory> getVisualCategories() {
        return visualCategories;
    }

    public void setVisualCategories(List<CreativeCategory> visualCategories) {
        this.visualCategories = visualCategories;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
