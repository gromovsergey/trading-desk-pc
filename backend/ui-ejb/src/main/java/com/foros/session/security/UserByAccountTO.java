package com.foros.session.security;

import com.foros.session.EntityTO;

/**
 * User: mbhikyagolu
 * Date: Apr 2, 2009
 */

public class UserByAccountTO extends EntityTO {
    public UserByAccountTO(Long id, String firstName, String lastName, char status) {
        super(id, firstName + " " + lastName, status);
    }
}