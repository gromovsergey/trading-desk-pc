package com.foros.session.admin.categoryChannel;

import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.model.channel.CategoryChannel;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelTO;

import java.io.Serializable;

public class CategoryChannelTO extends ChannelTO implements Serializable {
    private DisplayStatus accountStatus;

    private int level;

    public CategoryChannelTO() {
        super();
    }

    public CategoryChannelTO(Long id, String name, long flags, char status, char qaStatus,
            Long accountId, String accountName, Long accountDisplayStatusId, AccountRole accountRole, Long accountManagerId, Long displayStatusId, int level) {
        super(id, name, flags, status, qaStatus, accountId, accountRole, accountName, accountManagerId, displayStatusId);
        this.accountStatus = Account.getDisplayStatus(accountDisplayStatusId);
        this.level = level;
    }

    @Override
    protected String getProvidedResKey() {
        return CategoryChannel.getResoureKey(getId().toString());
    }

    public DisplayStatus getAccountStatus() {
        return accountStatus;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public Character getChannelType() {
        return 'C';
    }
}
