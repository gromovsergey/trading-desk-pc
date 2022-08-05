package com.foros.session;

import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.security.Statusable;
import com.foros.util.LocalizableNameUtil;

import java.util.Comparator;

public class LocalizableNameEntityComparator implements Comparator<LocalizableNameEntity> {

    private final boolean isSortByStatus;

    private Comparator<LocalizableName> comparator = LocalizableNameUtil.getComparator();

    public LocalizableNameEntityComparator() {
        this(false);
    }

    public LocalizableNameEntityComparator(boolean isSortByStatus) {
        this.isSortByStatus = isSortByStatus;
    }

    @Override
    public int compare(LocalizableNameEntity o1, LocalizableNameEntity o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException("Can't compare null entities");
        }
        if (isSortByStatus) {
            int statusOrder = statusOrder((Statusable) o2) - statusOrder((Statusable) o1);
            if (statusOrder != 0) {
                return statusOrder;
            }
        }
        return comparator.compare(o1.getName(), o2.getName());
    }

    private int statusOrder(Statusable o) {
        switch (o.getStatus()) {
            case DELETED:
                return -2;
            case INACTIVE:
                return -1;
            default:
                return 0;
        }
    }
}
