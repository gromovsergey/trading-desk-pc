package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.action.reporting.treeFilter.TreeFilterActionSupport;
import com.foros.action.xml.ProcessException;
import com.foros.framework.ReadOnly;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.ArrayList;

public abstract class AdvertiserReportAbstractTreeFilterAction extends TreeFilterActionSupport {

    private static final OlapDetailLevel.Filter[] ALL_LEVELS = OlapDetailLevel.Filter.values();

    protected Boolean display;
    protected OlapDetailLevel detailLevel;

    public AdvertiserReportAbstractTreeFilterAction() {
        selectedIds = new ArrayList<String>(ALL_LEVELS.length);
    }

    @Override
    @ReadOnly
    public String process() throws ProcessException {
        if (!root || isLevelAvailable()) {
            options = generateOptions();
            if (!isNextLevelAvailable()) {
                for (TreeFilterElementTO option : options) {
                    option.setHasChildren(false);
                }
            }
        }
        return root ? "root" : SUCCESS;
    }

    private boolean isNextLevelAvailable() {
        OlapDetailLevel.Filter currentLevel = getCurrentLevel();
        if (currentLevel.ordinal() == ALL_LEVELS.length - 1) {
            return false;
        }

        return isLevelAvailable(ALL_LEVELS[currentLevel.ordinal() + 1]);
    }

    private boolean isLevelAvailable(OlapDetailLevel.Filter level) {
        return detailLevel.getAvailableFilters().contains(level);
    }

    @Override
    public boolean isLevelAvailable() {
        return isLevelAvailable(getCurrentLevel());
    }

    public Boolean isDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public OlapDetailLevel getDetailLevel() {
        return detailLevel;
    }

    public void setDetailLevel(OlapDetailLevel detailLevel) {
        this.detailLevel = detailLevel;
    }

    protected abstract OlapDetailLevel.Filter getCurrentLevel();

    @Override
    public String getEntityFilterMessageKey() {
        switch (detailLevel) {
            case Keyword:
                return "report.advertising.select.TextAd";
            default:
                return "report.advertising.select." + detailLevel.name();
        }
    }

    @Override
    public String getSelectedId() {
        int index = getCurrentLevel().ordinal();
        return index >= selectedIds.size() ? null : selectedIds.get(index);
    }

}