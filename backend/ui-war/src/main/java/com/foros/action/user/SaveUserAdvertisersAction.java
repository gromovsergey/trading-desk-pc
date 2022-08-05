package com.foros.action.user;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.GenericAccount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SaveUserAdvertisersAction extends UserActionSupport implements RequestContextsAware {

    private Collection<Long> selectedAdvertisers;

    public SaveUserAdvertisersAction() {
        user.setAccount(new GenericAccount());
        user.unregisterChange("account");
    }

    public String save() {
        userService.updateAdvertisers(user.getId(), user.getVersion(), unpackAdvertisers());
        return SUCCESS;
    }

    public void setSelectedAdvertisers(Collection<Long> selectedAdvertisers) {
        this.selectedAdvertisers = selectedAdvertisers;
    }

    Collection<AdvertiserAccount> unpackAdvertisers() {
        Collection<AdvertiserAccount> advertisers;
        if (selectedAdvertisers != null) {
            advertisers = new ArrayList<AdvertiserAccount>(selectedAdvertisers.size());
            for (Long id : selectedAdvertisers) {
                advertisers.add(new AdvertiserAccount(id));
            }
        } else {
            advertisers = Collections.emptyList();
        }
        return advertisers;
    }
}
