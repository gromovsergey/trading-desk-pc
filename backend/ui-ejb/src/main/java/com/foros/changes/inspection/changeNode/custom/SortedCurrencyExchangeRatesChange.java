package com.foros.changes.inspection.changeNode.custom;

import com.foros.changes.inspection.ChangeNode;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.changes.inspection.changeNode.CollectionFieldChange;
import com.foros.model.currency.CurrencyExchangeRateAuditWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SortedCurrencyExchangeRatesChange extends CollectionFieldChange {

    protected SortedCurrencyExchangeRatesChange(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
        super(descriptor, oldValue, newValue);
    }

    @Override
    public void prepareInternal(PrepareChangesContext context) {
        super.prepareInternal(context);

        if (itemChanges == null) {
            return;
        }

        Collections.sort(itemChanges, new Comparator<ChangeNode>() {
            public int compare(ChangeNode node1, ChangeNode node2) {
                CurrencyExchangeRateAuditWrapper rate1 = (CurrencyExchangeRateAuditWrapper) node1.getLastDefinedValue();
                CurrencyExchangeRateAuditWrapper rate2 = (CurrencyExchangeRateAuditWrapper) node2.getLastDefinedValue();
                return rate1.getRate().getCurrency().getCurrencyCode().compareTo(rate2.getRate().getCurrency().getCurrencyCode());
            }
        });
    }

    public static class Factory extends CollectionFieldChange.Factory {
        @Override
        protected SortedCurrencyExchangeRatesChange newInstanceInternal(FieldChangeDescriptor descriptor, Collection oldValue, Collection newValue) {
            return new SortedCurrencyExchangeRatesChange(descriptor, oldValue, newValue);
        }
    }
}
