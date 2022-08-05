package app.programmatic.ui.creative.view;

import com.foros.rs.client.model.advertising.template.CreativeSize;
import app.programmatic.ui.creative.dao.model.CreativeCategory;
import app.programmatic.ui.creative.dao.model.CreativeTemplate;

import java.util.List;

public class TemplateSizeView {
    private CreativeTemplate template;
    private CreativeSize size;
    private List<CreativeCategory> accountContentCategories;

    public TemplateSizeView() {
    }

    public CreativeTemplate getTemplate() {
        return template;
    }

    public void setTemplate(CreativeTemplate template) {
        this.template = template;
    }

    public CreativeSize getSize() {
        return size;
    }

    public void setSize(CreativeSize size) {
        this.size = size;
    }

    public List<CreativeCategory> getAccountContentCategories() {
        return accountContentCategories;
    }

    public void setAccountContentCategories(List<CreativeCategory> accountContentCategories) {
        this.accountContentCategories = accountContentCategories;
    }
}
