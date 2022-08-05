package com.foros.model.security;

import com.foros.model.account.Account;

/**
 * @author Oleg
 * @ created Feb 8, 2008
 */
public interface OwnedEntity<T extends Account> {
    T getAccount();
}
