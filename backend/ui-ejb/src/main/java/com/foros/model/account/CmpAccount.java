package com.foros.model.account;

import com.foros.security.AccountRole;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("5")
public class CmpAccount extends AccountsPayableAccountBase {

    @Override
    public AccountRole getRole() {
        return AccountRole.CMP;
    }
}
