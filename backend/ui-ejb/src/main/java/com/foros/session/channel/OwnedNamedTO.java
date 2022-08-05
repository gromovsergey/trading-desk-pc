package com.foros.session.channel;

import com.foros.session.bulk.IdNameTO;

public class OwnedNamedTO extends IdNameTO {

    private Long accountId;

    public OwnedNamedTO(Long id, String name, Long accountId) {
        super(id, name);
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }
}
