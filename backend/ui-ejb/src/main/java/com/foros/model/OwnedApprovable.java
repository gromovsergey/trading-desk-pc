package com.foros.model;

import com.foros.model.account.Account;
import com.foros.model.security.OwnedStatusable;

public interface OwnedApprovable<T extends Account> extends OwnedStatusable<T>, Approvable {
}
