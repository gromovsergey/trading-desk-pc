package com.foros.action.creative.display.treefilter;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterAction;
import com.foros.action.xml.ProcessException;
import com.foros.framework.ReadOnly;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

public abstract class AbstractCreativeLinkTreeFilterAction extends BaseActionSupport implements TreeFilterAction {
    private List<TreeFilterElementTO> options;

    private Long ownerId;

    private String treeId;

    private String entityFilterMessageKey;

    @EJB
    private CampaignService campaignService;

    protected abstract List<TreeFilterElementTO> generateOptions();

    @Override
    @ReadOnly
    public String process() throws ProcessException {
        options = generateOptions();
        return SUCCESS;
    }

    @Override
    public Long getOwnerId() {
        return ownerId;
    }

    @Override
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean isLevelAvailable() {
        return true;
    }


    @Override
    public List<TreeFilterElementTO> getOptions() {
        return options;
    }

    @Override
    public void setRoot(boolean root) {
    }

    @Override
    public String getEntityFilterMessageKey() {
        return entityFilterMessageKey;
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
    public void setSelectedIds(List<String> selectedIds) {
    }

    @Override
    public String getTreeId() {
        return treeId;
    }

    @Override
    public void setTreeId(String treeId) {
        this.treeId = treeId;

    }

    public void setEntityFilterMessageKey(String entityFilterMessageKey) {
        this.entityFilterMessageKey = entityFilterMessageKey;
    }

    @Override
    public boolean isShowRoot() {
        return false;
    }

}
