package com.foros.session.channel;

import com.foros.model.account.Account;
import com.foros.security.AccountRole;

public class DiscoverChannelListTO extends ChannelTO {

    public DiscoverChannelListTO() {
        super();
    }

    public DiscoverChannelListTO(Long id, String name, long flags, char status, char qaStatus,
                                 Long accountId, AccountRole accountRole, String accountName, Long accountDisplayStatusId, Long accountManagerId, Long displayStatusId, String country) {
        super(id, name, flags, status, qaStatus, accountId, accountRole, accountName, accountManagerId, displayStatusId);
        setAccountDisplayStatus(Account.getDisplayStatus(accountDisplayStatusId));
        setCountry(country);
    }

}
