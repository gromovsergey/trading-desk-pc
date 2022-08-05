package com.foros.session.creative;

import com.foros.session.campaign.ImpClickStatsTO.Builder;

import java.util.ArrayList;
import java.util.List;

public class CreativeSetTO extends BaseLinkedTO {

    public CreativeSetTO(Builder builder) {
        super(builder);
    }

    private List<BaseLinkedTO> linkedTOs = new ArrayList<>();

    public List<BaseLinkedTO> getLinkedTOs() {
        return linkedTOs;
    }

    public void addTO(BaseLinkedTO baseLinkedTO) {
        linkedTOs.add(baseLinkedTO);
    }
}
