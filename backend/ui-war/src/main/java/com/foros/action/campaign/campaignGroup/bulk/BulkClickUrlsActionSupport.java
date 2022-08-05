package com.foros.action.campaign.campaignGroup.bulk;

public class BulkClickUrlsActionSupport extends CcgEditBulkActionSupport {
    public static enum Mode {
        Set,
        Append,
        Replace
    }

    protected Mode editMode = Mode.Set;

    public Mode getEditMode() {
        return editMode;
    }

    public void setEditMode(Mode editMode) {
        this.editMode = editMode;
    }
}
