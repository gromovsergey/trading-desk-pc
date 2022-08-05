package com.foros.action.reporting.treeFilter;

import com.foros.action.BaseActionSupport;
import com.foros.model.Identifiable;
import com.foros.session.TreeFilterElementTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TreeFilterActionSupport extends BaseActionSupport implements TreeFilterAction {
    protected List<TreeFilterElementTO> options;
    protected Long ownerId;
    protected boolean root;
    private String treeId;
    protected List<String> selectedIds = new ArrayList<String>();

    @Override
    public abstract String getParameterName();

    protected abstract List<TreeFilterElementTO> generateOptions();

    @Override
    public List<TreeFilterElementTO> getOptions() {
        return options;
    }

    @Override
    public boolean isShowRoot() {
        return true;
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
    public boolean isRoot() {
        return root;
    }

    @Override
    public void setRoot(boolean root) {
        this.root = root;
    }

    @Override
    public List<String> getSelectedIds() {
        return selectedIds;
    }

    @Override
    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
    }

    @Override
    public boolean isLevelAvailable() {
        return true;
    }

    @Override
    public String getTreeId() {
        return treeId;
    }

    @Override
    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public static class TreeFilterHelper {
        public static List<Long> getSelectedIds(List<Long> ids) {
            return Collections.frequency(ids, null) == ids.size() ? Collections.EMPTY_LIST : ids;
        }

        public static void addIdOrNull(List<Long> dst, List<Long> src) {
            if (!src.isEmpty()) {
                dst.add(src.get(0));
            } else {
                dst.add(null);
            }
        }

        public static void addIdOrNull(List<Long> dst, Identifiable... src) {
            for (Identifiable identifiable : src) {
                if (identifiable != null) {
                    dst.add(identifiable.getId());
                } else {
                    dst.add(null);
                }
            }
        }

    }

}
