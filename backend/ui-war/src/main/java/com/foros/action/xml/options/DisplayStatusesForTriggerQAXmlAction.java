package com.foros.action.xml.options;

import com.foros.action.IdNameBean;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.IdNameBeanConverter;
import com.foros.model.DisplayStatus;
import com.foros.model.channel.Channel;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DisplayStatusesForTriggerQAXmlAction extends AbstractOptionsAction<IdNameBean> {

    private String role;

    public DisplayStatusesForTriggerQAXmlAction() {
        super(new IdNameBeanConverter(false));
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    protected Collection<? extends IdNameBean> getOptions() throws ProcessException {
        List<DisplayStatus> displayStatuses;
        if (StringUtil.isPropertyEmpty(role) || "EXTERNAL".equals(role)) {
            displayStatuses = Arrays.asList(
                        Channel.LIVE,
                        Channel.LIVE_PENDING_INACTIVATION,
                        Channel.LIVE_TRIGGERS_NEED_ATT,
                        Channel.LIVE_AMBER_PENDING_INACTIVATION,
                        Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
                        Channel.DECLINED,
                        Channel.PENDING_FOROS,
                        Channel.INACTIVE,
                        Channel.DELETED);
        } else {
            displayStatuses = Arrays.asList(
                        Channel.LIVE,
                        Channel.LIVE_TRIGGERS_NEED_ATT,
                        Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
                        Channel.DECLINED,
                        Channel.PENDING_FOROS,
                        Channel.INACTIVE,
                        Channel.DELETED);
        }

        return CollectionUtils.convert(new Converter<DisplayStatus, IdNameBean>() {
            @Override
            public IdNameBean item(DisplayStatus value) {
                return new IdNameBean(value.getId().toString(), StringUtil.getLocalizedString(value.getDescription()));
            }
        }, displayStatuses);
    }
}
