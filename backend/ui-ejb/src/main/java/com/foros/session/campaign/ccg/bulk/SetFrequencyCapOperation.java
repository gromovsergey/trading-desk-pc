package com.foros.session.campaign.ccg.bulk;

import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.session.bulk.BulkOperation;

public class SetFrequencyCapOperation<T extends FrequencyCapEntity> implements BulkOperation<T> {
    private FrequencyCap frequencyCap;

    public SetFrequencyCapOperation(FrequencyCap frequencyCap) {
        this.frequencyCap = frequencyCap;
    }

    @Override
    public void perform(T existing, T toUpdate) {
        FrequencyCap newFC = new FrequencyCap();
        newFC.setPeriod(frequencyCap.getPeriod());
        newFC.setWindowLength(frequencyCap.getWindowLength());
        newFC.setWindowCount(frequencyCap.getWindowCount());
        newFC.setLifeCount(frequencyCap.getLifeCount());
        toUpdate.setFrequencyCap(newFC);
    }

    public FrequencyCap getFrequencyCap() {
        return frequencyCap;
    }
}
