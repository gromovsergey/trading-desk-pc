package com.foros.model.security;

import com.foros.model.account.Account;

public interface OwnedStatusable<T extends Account> extends OwnedEntity<T>, Statusable {

}
