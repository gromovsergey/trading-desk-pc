package com.foros.session.query.channel;

import com.foros.jaxb.adapters.EntityLink;
import com.foros.model.Status;
import com.foros.model.channel.ApiDeviceChannelTO;
import com.foros.session.query.AbstractEntityTransformer;

import java.sql.Timestamp;
import java.util.Map;

public class DeviceChannelApiTOTransformer extends AbstractEntityTransformer<ApiDeviceChannelTO> {

    @Override
    protected ApiDeviceChannelTO transform(Map<String, Object> values) {
        Long id = (Long) values.get("id");
        String name = (String) values.get("name");
        ApiDeviceChannelTO to = new ApiDeviceChannelTO(id, name);
        to.setStatus(Status.valueOf((char) values.get("status")));
        to.setUpdated((Timestamp) values.get("version"));
        to.setParentChannel(new EntityLink((Long) values.get("parentChannelId")));
        to.setExpression((String) values.get("expression"));
        return to;
    }
}
