package com.foros.model.campaign;

import com.foros.annotations.Audit;
import com.foros.annotations.Auditable;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.CampaignCreditContainerWrapperChange;
import com.foros.model.account.AdvertisingAccountBase;

@Auditable
@Audit(nodeFactory = CampaignCreditContainerWrapperChange.Factory.class)
public class CampaignCreditContainerWrapper {
    @ChangesInspection(type = InspectionType.CASCADE)
    private AdvertisingAccountBase account;

    public CampaignCreditContainerWrapper(AdvertisingAccountBase account) {
        this.account = account;
    }

    public AdvertisingAccountBase getAccount() {
        return account;
    }

    public Long getId() {
        return getAccount().getId();
    }
}