package com.foros.action.action.treefilter;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterAction;
import com.foros.action.xml.ProcessException;
import com.foros.framework.ReadOnly;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;

public abstract class AbstractConversionLinkTreeFilterAction extends BaseActionSupport implements TreeFilterAction {
    private List<TreeFilterElementTO> options;

    private Long ownerId;

    private String treeId;

    private String entityFilterMessageKey;

    @EJB
    private CampaignService campaignService;

    protected abstract List<TreeFilterElementTO> generateOptions();

    @Override
    public String getEntityFilterMessageKey() {
        return entityFilterMessageKey;
    }

    @Override
    public List<TreeFilterElementTO> getOptions() {
        return options;
    }

    @Override
    public Long getOwnerId() {
        return ownerId;
    }

    @Override
    public String getSelectedId() {
        return "";
    }

    @Override
    public List<String> getSelectedIds() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getTreeId() {
        return treeId;
    }

    @Override
    public boolean isLevelAvailable() {
        return true;
    }

    @Override
    public boolean isShowRoot() {
        return false;
    }

    @Override
    @ReadOnly
    public String process() throws ProcessException {
        options = generateOptions();
        return SUCCESS;
    }

    public void setEntityFilterMessageKey(String entityFilterMessageKey) {
        this.entityFilterMessageKey = entityFilterMessageKey;
    }

    @Override
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void setRoot(boolean root) {
    }

    @Override
    public void setSelectedIds(List<String> selectedIds) {
    }

    @Override
    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }
}
