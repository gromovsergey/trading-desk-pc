package com.foros.action.site;

import com.foros.model.site.WDTagOptGroupState;
import com.foros.model.site.WDTagOptionValue;
import com.foros.session.template.OptionService;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

public class PreviewWDTagActionBase extends WDTagActionSupport {

    protected static final long DEFAULT_PREVIEW_WIDTH = 300L;
    protected static final long DEFAULT_PREVIEW_HEIGHT = 250L;

    private String optedInUrls = "";
    private String optedOutUrls = "";

    @EJB
    protected OptionService optionService;

    private Map<Long, WDTagOptionValue> optionValues;
    private Map<Long, WDTagOptGroupState> groupStateValues;

    public Map<Long, WDTagOptionValue> getOptionValues() {
        if (optionValues == null) {
            optionValues = new HashMap<Long, WDTagOptionValue>();
            for (WDTagOptionValue v : wdTag.getOptions()) {
                optionValues.put(v.getOption().getId(), v);
            }
        }
        return optionValues;
    }

    public void setOptionValues(Map<Long, WDTagOptionValue> wdOptionValues) {
        this.optionValues = wdOptionValues;
    }

    public Map<Long, WDTagOptGroupState> getGroupStateValues() {
        if (groupStateValues == null) {
            groupStateValues = new HashMap<Long, WDTagOptGroupState>();
            for (WDTagOptGroupState g : wdTag.getGroupStates()) {
                groupStateValues.put(g.getId().getOptionGroupId(), g);
            }
        }
        return groupStateValues;
    }

    public void setGroupStateValues(Map<Long, WDTagOptGroupState> groupStateValues) {
        this.groupStateValues = groupStateValues;
    }

    public String getOptedInUrls() {
        return optedInUrls;
    }

    public void setOptedInUrls(String optedInUrls) {
        this.optedInUrls = optedInUrls;
    }

    public String getOptedOutUrls() {
        return optedOutUrls;
    }

    public void setOptedOutUrls(String optedOutUrls) {
        this.optedOutUrls = optedOutUrls;
    }

    public Long getPreviewWidth() {
        if (wdTag.getWidth() != null) {
            return wdTag.getWidth();
        } else {
            return DEFAULT_PREVIEW_WIDTH;
        }
    }

    public Long getPreviewHeight() {
        if (wdTag.getHeight() != null) {
            return wdTag.getHeight();
        } else {
            return DEFAULT_PREVIEW_HEIGHT;
        }
    }

}

