package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.model.site.Tag;
import com.foros.restriction.annotation.Restrict;

public class EditCreativeCustomizationAction extends CreativeCustomizationSupportAction {

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.updateOptions", parameters = "find('Tag', #target.tag.id)")
    public String edit() {
        Tag tag = tagsService.viewFetchedForEdit(getTag().getId());
        setTag(tag);
        clearLazyData();
        return SUCCESS;
    }

    private void clearLazyData() {
        groups = null;
        optionValues = null;
        groupStateValues = null;
    }
}
