package com.foros.action.channel;

import com.foros.framework.ReadOnly;
import com.foros.service.RemoteServiceException;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.util.jpa.DetachedList;

public class ContentChannelSearchAction extends ChannelSearchSupportAction {

    private DetachedList<ChannelTO> channels;

    private SearchCriteria searchCriteria = new SearchCriteria();

    @ReadOnly
    public String searchChannels() {
        prepare();
        validateSearch();
        if (hasErrors()) {
            return SUCCESS;
        }

        try {
            channels = channelService.searchAdvertisingChannels(
                account.getId(), searchCriteria.getSearchName(), searchCriteria.getContent(), country.getCountryCode(),
                searchCriteria.getSearchMyChannels(), searchCriteria.getSearchPublicChannels(), searchCriteria.getSearchCmpChannels());

            if (channels.isSizeReduced()) {
                addFieldError("searchErrors", getText("channel.search.toomanyresults"));
            }

        } catch (RemoteServiceException e) {
            addFieldError("searchErrors", getText("errors.serviceIsNotAvailable", new String[] {getText("channel.channelSearchService")}));
        }

        return SUCCESS;
    }

    @Override
    protected void prepare(){
        super.prepare();
        searchCriteria.populateConditionOfVisibility(account);
    }

    private void validateSearch() {
        if (!(searchCriteria.getSearchMyChannels() || searchCriteria.getSearchPublicChannels() || searchCriteria.getSearchCmpChannels())) {
            addFieldError("searchErrors", getText("channel.search.selectOneOption"));
        }
    }


    public DetachedList<ChannelTO> getChannels() {
        return channels;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }
}
