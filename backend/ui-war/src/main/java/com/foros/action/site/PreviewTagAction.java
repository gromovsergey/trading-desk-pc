package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;

import javax.persistence.EntityNotFoundException;

public class PreviewTagAction extends TagSupportAction {
    private boolean tagPreviewAvailable;
    private String previewText;

    @ReadOnly
    public String execute() {
        if (getTag().getId() == null) {
            throw new EntityNotFoundException("Site with id = null not found");
        }

        setTag(tagsService.find(getTag().getId()));

        populatePreview();

        return SUCCESS;
    }

    public boolean isTagPreviewAvailable() {
        return tagPreviewAvailable;
    }

    public void setTagPreviewAvailable(boolean tagPreviewAvailable) {
        this.tagPreviewAvailable = tagPreviewAvailable;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    private void populatePreview() {
        Tag tag = getTag();

        setTagPreviewAvailable(Status.ACTIVE == tag.getInheritedStatus() && Site.LIVE == tag.getSite().getDisplayStatus()
                && Account.LIVE == tag.getSite().getAccount().getDisplayStatus());

        if (isTagPreviewAvailable()) {
            setPreviewText(tagsService.generateTagPreviewHtml(tag));
        }
    }
}
