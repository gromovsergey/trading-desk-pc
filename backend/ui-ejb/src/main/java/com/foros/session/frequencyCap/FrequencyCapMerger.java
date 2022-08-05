package com.foros.session.frequencyCap;

import com.foros.model.EntityBase;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;

import javax.persistence.EntityManager;

public abstract class FrequencyCapMerger<T extends EntityBase & FrequencyCapEntity> {

    protected abstract EntityManager getEm();

    public boolean merge(T entity, T existing) {
        boolean result = false;
        if (!entity.isChanged("frequencyCap")) {
            return result;
        }

        FrequencyCap existingCap = existing.getFrequencyCap();
        FrequencyCap frequencyCap = entity.getFrequencyCap();

        if (existingCap != null) {
            if (frequencyCap != null) {
                // merge update
                frequencyCap.setId(existingCap.getId());
                if (frequencyCap.getVersion() == null) {
                    frequencyCap.setVersion(existingCap.getVersion());
                }
                removeEmptySpans(frequencyCap);
                frequencyCap = getEm().merge(frequencyCap);
                existing.registerChange("frequencyCap");
                result = frequencyCap.isChanged();
            } else {
                // delete orphan
                getEm().remove(existingCap);
                existing.setFrequencyCap(null);
                result = true;
            }
        } else {
            if (frequencyCap != null) {
                // add
                frequencyCap.setId(null);
                removeEmptySpans(frequencyCap);
                getEm().persist(frequencyCap);
                existing.setFrequencyCap(frequencyCap);
                result = true;
            }
        }

        entity.unregisterChange("frequencyCap");
        return result;
    }

    private void removeEmptySpans(FrequencyCap frequencyCap) {
        if (frequencyCap.getPeriodSpan() != null && frequencyCap.getPeriodSpan().getValue() == null) {
            frequencyCap.setPeriodSpan(null);
        }
        if (frequencyCap.getWindowLengthSpan() != null && frequencyCap.getWindowLengthSpan().getValue() == null) {
            frequencyCap.setWindowLengthSpan(null);
        }
    }
}
