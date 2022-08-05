package com.foros.action.admin.triggerQA;

import com.foros.framework.Trim;
import com.foros.session.channel.exceptions.UpdateException;

@Trim(include = "fake")
public class SaveTriggersAction extends TriggersActionSupport {

    public String update() {
        try {
            getTriggerQAService().update(getTriggers());
            return SUCCESS;
        } catch (UpdateException e) {
            addActionError(getText("triggers.validation.cantUpdate"));
            return INPUT;
        }
    }

}
