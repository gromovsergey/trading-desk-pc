package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.session.security.UserCredentialService;

import javax.ejb.EJB;

public class ChangeRsCredentialsAction extends BaseActionSupport {

    @EJB
    private UserCredentialService userCredentialService;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String execute() {
        userCredentialService.changeRsCredentials(id);
        return SUCCESS;
    }

}
