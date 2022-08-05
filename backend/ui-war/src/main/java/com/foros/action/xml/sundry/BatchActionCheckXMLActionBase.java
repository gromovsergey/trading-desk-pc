package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;

import java.util.Arrays;
import java.util.List;

public abstract class BatchActionCheckXMLActionBase extends AbstractXmlAction<Boolean> {
    private Long[] ids;
    private String action;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    protected List<Long> getIdsAsList() {
        return Arrays.asList(ids);
    }

    @Override
    protected Boolean generateModel() throws ProcessException {
        return isBatchActionPossible(action);
    }

    protected abstract boolean isBatchActionPossible(String action);
}
