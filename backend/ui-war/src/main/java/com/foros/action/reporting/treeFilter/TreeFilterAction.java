package com.foros.action.reporting.treeFilter;

import com.foros.action.xml.ProcessException;
import com.foros.session.TreeFilterElementTO;

import java.util.List;

public interface TreeFilterAction {

    String process() throws ProcessException;

    boolean isLevelAvailable();

    String getParameterName();

    List<TreeFilterElementTO> getOptions();

    Long getOwnerId();

    void setOwnerId(Long ownerId);

    boolean isRoot();

    void setRoot(boolean root);

    String getEntityFilterMessageKey();

    /* must be String not Long (struts 2 can not convert empty string to null
     * http://struts.apache.org/release/2.0.x/docs/type-conversion.html#TypeConversion-NullandBlankValues)
     */
    String getSelectedId();

    /* must be String not Long (struts 2 can not convert empty string to null
     * http://struts.apache.org/release/2.0.x/docs/type-conversion.html#TypeConversion-NullandBlankValues)
     */
    List<String> getSelectedIds();

    void setSelectedIds(List<String> selectedIds);

    String getTreeId();

    void setTreeId(String treeId);

    boolean isShowRoot();

}