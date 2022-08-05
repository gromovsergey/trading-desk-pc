package com.foros.util.comparator;

import com.foros.session.EntityTO;

import java.util.Comparator;

public abstract class StatusTOComparatorBase<TO extends EntityTO> implements Comparator<TO> {

    abstract protected int otherCompare(TO to1, TO to2);

    @Override
    public int compare(TO to1, TO to2) {
        int statusOrder = statusOrder(to2) - statusOrder(to1);
        if (statusOrder != 0) {
            return statusOrder;
        }
        return otherCompare(to1, to2);
    }

    private int statusOrder(EntityTO to) {
        switch (to.getStatus()) {
            case DELETED:
                return -2;
            case INACTIVE:
                return -1;
            default:
                return 0;
        }
    }
}
