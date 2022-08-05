package com.foros.session.channel.service;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersChannel;
import com.foros.model.channel.Channel;
import com.foros.util.CollectionMerger;
import com.foros.util.JpaCollectionMerger;

import javax.persistence.EntityManager;

public abstract class BehavioralParametersMerger<T extends Channel & BehavioralParametersChannel> {

    protected abstract EntityManager getEM();

    public void merge(T updated, final T existing) {
        if (!updated.isChanged("behavioralParameters")) {
            return;
        }

        final EntityManager em = getEM();
        CollectionMerger<BehavioralParameters> merger = new JpaCollectionMerger<BehavioralParameters>(existing.getBehavioralParameters(), updated.getBehavioralParameters()) {
            @Override
            protected EntityManager getEM() {
                return em;
            }

            @Override
            protected Object getId(BehavioralParameters bp, int index) {
                return bp.getTriggerType();
            }

            @Override
            protected boolean add(BehavioralParameters updated) {
                updated.setChannel(existing);
                return super.add(updated);
            }

            @Override
            protected void update(BehavioralParameters persistent, BehavioralParameters updated) {
                updated.setId(persistent.getId());
                if (updated.getVersion() == null) {
                    updated.setVersion(persistent.getVersion());
                }
                super.update(persistent, updated);
                existing.registerChange("behavioralParameters");
            }
        };
        merger.merge();

        updated.unregisterChange("behavioralParameters");
    }
}
