package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.DiscoverChannelListService;
import com.foros.session.channel.service.DiscoverChannelService;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class DiscoverChannelListsXmlAction extends AbstractOptionsAction<EntityTO> {

    @EJB
    private DiscoverChannelService discoverChannelService;

    @EJB
    private DiscoverChannelListService discoverChannelListService;

    private Long discoverChannelId;
    private Long currentChannelListId;
    private String query;

    public DiscoverChannelListsXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(false));
    }

    @RequiredFieldValidator(key = "errors.field.required", fieldName = "discoverChannelId")
    public Long getDiscoverChannelId() {
        return discoverChannelId;
    }

    public void setDiscoverChannelId(Long discoverChannelId) {
        this.discoverChannelId = discoverChannelId;
    }

    public Long getCurrentChannelListId() {
        return currentChannelListId;
    }

    public void setCurrentChannelListId(Long currentChannelListId) {
        this.currentChannelListId = currentChannelListId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        DiscoverChannel discoverChannel = discoverChannelService.view(discoverChannelId);
        //extract channel list from form
        if (currentChannelListId != null) {
            DiscoverChannelList discoverChannelList = discoverChannelListService.view(currentChannelListId);
            discoverChannel.setChannelList(discoverChannelList);
        }
        return discoverChannelService.findAvailableChannelLists(discoverChannel, query, AUTOCOMPLETE_SIZE);
    }
}
