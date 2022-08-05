package com.foros.session.quicksearch;

import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.CurrentUserService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class QuickSearchRestrictions {
    @EJB
    private CurrentUserService currentUserService;

    @Restriction
    public boolean canSearch() {
        return currentUserService.isInternal();
    }
}
