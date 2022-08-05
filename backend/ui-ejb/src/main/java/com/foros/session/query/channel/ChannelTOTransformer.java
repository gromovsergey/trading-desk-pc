package com.foros.session.query.channel;

import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelTO;

import java.math.BigDecimal;
import java.util.Map;

public class ChannelTOTransformer extends DistinctChannelTOTransformer<ChannelTO> {

    @Override
    protected ChannelTO transform(Map<String, Object> values) {
        ChannelTO result = new ChannelTO(
                (Long) values.get("id"),
                (String) values.get("name"),
                (Long) values.get("flags"),
                (Character) values.get("channelStatus"),
                (Character) values.get("qaStatus"),
                (Long) values.get("displayStatus"),
                (Long) values.get("accountId"),
                (AccountRole) values.get("accountRole"),
                (String) values.get("accountName"),
                (Long) values.get("accountDisplayStatus"),
                (Long) values.get("accountManager"),
                (Long) values.get("imps"),
                (BigDecimal) values.get("userCount"),
                (String) values.get("country")
        );

        result.setChannelType(((String)values.get("type")).charAt(0));
        result.setVisibility((ChannelVisibility) values.get("channelVisibility"));
        result.setAccountTestFlag(((Long) values.get("accountFlag")));

        return result;
    }

}
