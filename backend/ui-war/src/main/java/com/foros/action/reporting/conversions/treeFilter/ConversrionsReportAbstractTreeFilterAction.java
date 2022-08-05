package com.foros.action.reporting.conversions.treeFilter;

import com.foros.action.reporting.treeFilter.TreeFilterActionSupport;
import com.foros.action.xml.ProcessException;
import com.foros.framework.ReadOnly;

public abstract class ConversrionsReportAbstractTreeFilterAction extends TreeFilterActionSupport {

    private String entityFilterMessageKey;

    @Override
    @ReadOnly
    public String process() throws ProcessException {
        options = generateOptions();
        return root ? "root" : SUCCESS;
    }

    @Override
    public String getEntityFilterMessageKey() {
        return entityFilterMessageKey;
    }

    public void setEntityFilterMessageKey(String entityFilterMessageKey) {
        this.entityFilterMessageKey = entityFilterMessageKey;
    }

    @Override
    public String getSelectedId() {
        int index = getLevel();
        return selectedIds.size() > index ? selectedIds.get(index) : null;
    }

    protected abstract int getLevel();

}
