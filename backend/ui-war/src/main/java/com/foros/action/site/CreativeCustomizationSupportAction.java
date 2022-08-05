package com.foros.action.site;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.TagOptionValue;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CreativeCustomizationSupportAction extends TagSupportAction implements BreadcrumbsSupport, RequestContextsAware {

    protected Map<Long, List<OptionGroup>> groups;
    protected Map<Long, TagOptionValue> optionValues;
    protected Map<Long, TagOptGroupState> groupStateValues;

    private List<CreativeTemplate> templates;
    private List<CreativeSize> sizes;

    public List<CreativeTemplate> getTemplates() {
        if (templates == null) {
            templates = templateService.findTemplatesWithPublisherOptions(getTag());
        }
        return templates;
    }

    public List<CreativeSize> getSizes() {
        if (sizes == null) {
            sizes = tagsService.findSizesWithPublisherOptions(getTag());
        }
        return sizes;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.add(new SiteBreadcrumbsElement(getTag().getSite())).add(new TagBreadcrumbsElement(getTag())).add("site.edittag.editCreativeCustomization");
        return breadcrumbs;
    }

    public Map<Long, TagOptionValue> getOptionValues() {
        if (optionValues == null) {
            optionValues = new HashMap<Long, TagOptionValue>();
            for (TagOptionValue v : getTag().getOptions()) {
                optionValues.put(v.getOption().getId(), v);
            }
        }

        return optionValues;
    }

    public void setOptionValues(Map<Long, TagOptionValue> tagOptionValues) {
        this.optionValues = tagOptionValues;
    }

    public Map<Long, TagOptGroupState> getGroupStateValues() {
        if (groupStateValues == null) {
            groupStateValues = new HashMap<Long, TagOptGroupState>();
            for (TagOptGroupState g : getTag().getGroupStates()) {
                groupStateValues.put(g.getId().getOptionGroupId(), g);
            }
        }
        return groupStateValues;
    }

    public void setGroupStateValues(Map<Long, TagOptGroupState> groupStateValues) {
        this.groupStateValues = groupStateValues;
    }

}
