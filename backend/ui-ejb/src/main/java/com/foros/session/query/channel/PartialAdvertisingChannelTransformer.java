package com.foros.session.query.channel;

import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.security.AccountType;
import com.foros.security.AccountRole;

import java.util.Map;


public class PartialAdvertisingChannelTransformer extends DistinctChannelTOTransformer<Channel> {

    @Override
    protected Channel transform(Map<String, Object> values) {
        Character channelType = ((String) values.get("type")).charAt(0);
        Channel result;
        if (channelType == 'B') {
            result = new BehavioralChannel();
        } else if (channelType == 'E') {
            result = new ExpressionChannel();
            ((ExpressionChannel)result).setExpression((String) values.get("expression"));
        } else {
            throw new IllegalArgumentException("Invalid channel type: " + channelType);
        }

        result.setId((Long) values.get("id"));
        result.setName((String) values.get("name"));
        result.setDescription((String) values.get("description"));
        result.setFlags((Long)values.get("flags"));
        result.setStatus(Status.valueOf((Character) values.get("channelStatus")));
        result.setQaStatus(ApproveStatus.valueOf((Character) values.get("qaStatus")));
        result.setDisplayStatusId((Long) values.get("displayStatus"));
        result.setVisibility((ChannelVisibility) values.get("channelVisibility"));
        result.setCountry(new Country((String) values.get("country")));

        Account account = new AdvertiserAccount();
        account.setId((Long) values.get("accountId"));
        account.setName((String) values.get("accountName"));
        account.setDisplayStatusId((Long) values.get("accountDisplayStatus"));
        AccountType accountType = new AccountType();
        accountType.setAccountRole((AccountRole) values.get("accountRole"));
        account.setAccountType(accountType);

        result.setAccount(account);

        return result;
    }

}

