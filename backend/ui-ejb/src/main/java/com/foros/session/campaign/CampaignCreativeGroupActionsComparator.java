package com.foros.session.campaign;

import com.foros.model.action.Action;
import com.foros.model.campaign.CampaignCreativeGroup;

import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

public class CampaignCreativeGroupActionsComparator implements Comparator<CampaignCreativeGroup> {

    @Override
    public int compare(CampaignCreativeGroup o1, CampaignCreativeGroup o2) {
        if ((o1 == null) && (o2 == null))
            return 0;
        if ((o1 == null) || (o2 == null))
            return -1;
        return compareActions(o1.getActions(), o2.getActions()) ? 0 : -1;
    }

    private boolean compareActions(Set<Action> o1, Set<Action> o2) {
        if (o1 == o2) {
            return true;
        }
        if ((o1 == null) || (o2 == null)) {
            return false;
        }
        if (o1.size() != o2.size()) {
            return false;
        }
        for (Action a1: o1) {
            boolean found = false;
            for (Action a2: o2) {
                if (ObjectUtils.equals(a1.getId(), a2.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
