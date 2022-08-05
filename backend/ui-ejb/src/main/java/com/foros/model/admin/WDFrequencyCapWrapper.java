package com.foros.model.admin;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.WDFrequencyCapWrapperChange;
import com.foros.model.FrequencyCap;

import java.util.Map;

@Auditable
@Audit(nodeFactory = WDFrequencyCapWrapperChange.Factory.class)
public class WDFrequencyCapWrapper {
    @ChangesInspection(type = InspectionType.CASCADE)
    private FrequencyCap eventsFrequencyCap;
    @ChangesInspection(type = InspectionType.CASCADE)
    private FrequencyCap channelsFrequencyCap;
    @ChangesInspection(type = InspectionType.CASCADE)
    private FrequencyCap categoriesFrequencyCap;

    @ChangesInspection(type = InspectionType.NONE)
    private Map<String, FrequencyCap> elementsToRemove;

    public FrequencyCap getEventsFrequencyCap() {
        return eventsFrequencyCap;
    }

    public void setEventsFrequencyCap(FrequencyCap eventsFrequencyCap) {
        this.eventsFrequencyCap = eventsFrequencyCap;
    }

    public FrequencyCap getChannelsFrequencyCap() {
        return channelsFrequencyCap;
    }

    public void setChannelsFrequencyCap(FrequencyCap channelsFrequencyCap) {
        this.channelsFrequencyCap = channelsFrequencyCap;
    }

    public FrequencyCap getCategoriesFrequencyCap() {
        return categoriesFrequencyCap;
    }

    public void setCategoriesFrequencyCap(FrequencyCap categoriesFrequencyCap) {
        this.categoriesFrequencyCap = categoriesFrequencyCap;
    }

    public Map<String, FrequencyCap> getElementsToRemove() {
        return elementsToRemove;
    }

    public void setElementsToRemove(Map<String, FrequencyCap> elementsToRemove) {
        this.elementsToRemove = elementsToRemove;
    }
}
