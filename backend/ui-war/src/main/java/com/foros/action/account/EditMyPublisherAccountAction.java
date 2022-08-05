package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;

public class EditMyPublisherAccountAction extends EditAccountActionBase<PublisherAccount> implements PublisherSelfIdAware {

    public EditMyPublisherAccountAction() {
        account = new PublisherAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = (PublisherAccount) accountService.getMyAccount();

        return SUCCESS;
    }

    @Override
    public void setPublisherId(Long publisherId) {
        account.setId(publisherId);
    }
}
