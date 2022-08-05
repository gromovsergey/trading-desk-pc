package com.foros.session.query.channel;

import com.foros.security.AccountRole;
import com.foros.session.channel.DiscoverChannelListTO;
import com.foros.session.query.AbstractEntityTransformer;

import java.util.Map;

public class DiscoverChannelListsTOTransformer extends AbstractEntityTransformer<DiscoverChannelListTO> {

    @Override
    protected DiscoverChannelListTO transform(Map<String, Object> values) {

         DiscoverChannelListTO result = new DiscoverChannelListTO(
                (Long) values.get("id"),
                (String) values.get("name"),
                (Long) values.get("flags"),
                (Character) values.get("channelStatus"),
                (Character) values.get("qaStatus"),
                (Long) values.get("accountId"),
                (AccountRole) values.get("accountRole"),
                (String) values.get("accountName"),
                (Long) values.get("accountDisplayStatus"),
                (Long) values.get("accountManager"),
                (Long) values.get("displayStatus"),
                (String) values.get("country")
        );

        return result;
    }
}
