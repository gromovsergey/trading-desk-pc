package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.session.EntityTO;
import com.foros.session.NameTOComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BulkSiteTargetingActionSupport extends CcgEditBulkActionSupport {
    protected Mode editMode = Mode.Set;

    // set
    protected boolean includeSpecificSitesFlag;
    protected Set<Long> selectedSites = new HashSet<>();
    private List<EntityTO> groupSites;

    // add
    protected Set<Long> addIds = new HashSet<>();
    private List<EntityTO> addSites;

    // remove
    protected Set<Long> removeIds = new HashSet<>();
    private List<EntityTO> removeSites;

    private List<EntityTO> availableSites;

    public Mode getEditMode() {
        return editMode;
    }

    public void setEditMode(Mode editMode) {
        this.editMode = editMode;
    }

    public boolean isIncludeSpecificSitesFlag() {
        return includeSpecificSitesFlag;
    }

    public void setIncludeSpecificSitesFlag(boolean includeSpecificSitesFlag) {
        this.includeSpecificSitesFlag = includeSpecificSitesFlag;
    }

    public Set<Long> getSelectedSites() {
        return selectedSites;
    }

    public void setSelectedSites(Set<Long> selectedSites) {
        this.selectedSites = selectedSites;
    }

    public boolean canEditSites() {
        return true;
    }

    public List<EntityTO> getAvailableSites() {
        if (availableSites == null) {
            availableSites = new ArrayList<>(ccgService.fetchTargetableSites(getAccount().isTestFlag(), getAccount().getCountry().getCountryCode()));
            Collections.sort(availableSites, new NameTOComparator<EntityTO>());
        }
        return availableSites;
    }

    public List<EntityTO> getGroupSites() {
        return groupSites = fillTos(groupSites, selectedSites);
    }

    public Set<Long> getAddIds() {
        return addIds;
    }

    public void setAddIds(Set<Long> addIds) {
        this.addIds = addIds;
    }

    public List<EntityTO> getAddSites() {
        return addSites = fillTos(addSites, addIds);
    }

    public Set<Long> getRemoveIds() {
        return removeIds;
    }

    public List<EntityTO> getRemoveSites() {
        return removeSites = fillTos(removeSites, removeIds);
    }

    public void setRemoveIds(Set<Long> removeIds) {
        this.removeIds = removeIds;
    }

    private List<EntityTO> fillTos(List<EntityTO> res, Set<Long> selected) {
        if (res == null) {
            res = new ArrayList<>(selected.size());
            for (EntityTO to : getAvailableSites()) {
                if (selected.contains(to.getId())) {
                    res.add(to);
                }
            }
        }
        return res;
    }

    public void setGroupSites(List<EntityTO> groupSites) {
        this.groupSites = groupSites;
    }

    public static enum Mode {
        Set,
        Add,
        Remove
    }
}
