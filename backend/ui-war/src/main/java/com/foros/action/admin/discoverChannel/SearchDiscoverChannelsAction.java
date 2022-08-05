package com.foros.action.admin.discoverChannel;

import com.foros.action.LanguageBean;
import com.foros.action.admin.channel.ChannelSearchStatus;
import com.foros.action.admin.channel.SearchChannelsActionBase;
import com.foros.action.channel.ChannelsSearchParams;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.ServiceLocator;
import com.foros.session.channel.DiscoverChannelTO;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.session.query.PartialList;
import com.foros.util.StringUtil;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

public class SearchDiscoverChannelsAction extends SearchChannelsActionBase {

    @EJB
    private DiscoverChannelService discoverChannelService;

    private PartialList<DiscoverChannelTO> channels;
    private boolean hideCountryColumn;
    private boolean hideAccountColumn;
    private String newsGateUrl;
    private List<LanguageBean> availableLanguages;

    @Override
    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.view")
    public String main() {
        super.main();
        accounts = discoverChannelService.getAvailableAccounts();
        availableLanguages = populateAvailableLanguages();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "DiscoverChannel.view")
    public String search() {
        ChannelsSearchParams searchForm = searchParams;
        try {
            Long accountId = searchForm.getAccountId();
            hideAccountColumn = accountId != null && accountId != 0;

            String countryCode = searchForm.getCountryCode();
            hideCountryColumn = StringUtil.isPropertyNotEmpty(countryCode);

            channels = searchChannelService.searchDiscover(searchForm.getName(),
                                                 accountId,
                                                 countryCode,
                                                 searchForm.getLanguage(),
                                                 searchForm.getFirstResultCount(),
                                                 searchForm.getPageSize(),
                                                 searchForm.getPhrase(),
                                                 ChannelSearchStatus.toDisplayStatuses(searchForm.getStatus()));
            searchForm.setTotal((long) channels.getTotal());

            setNewsGateUrl();
        } catch (Exception e) {
            addActionError(getText("errors.serviceIsNotAvailable", Arrays.asList(getText("channel.channelSearchService"))));
        }

        return SUCCESS;
    }

    private void setNewsGateUrl() {
        ConfigService configService = ServiceLocator.getInstance().lookup(ConfigService.class);
        newsGateUrl = configService.get(ConfigParameters.NEWSGATE_BASE_URL) + "/" +
                      configService.get(ConfigParameters.DISCOVER_PATH);
    }

    public String getNewsGateUrl() {
        return newsGateUrl;
    }

    public boolean isHideAccountColumn() {
        return hideAccountColumn;
    }

    public boolean isHideCountryColumn() {
        return hideCountryColumn;
    }

    public PartialList<DiscoverChannelTO> getChannels() {
        return channels;
    }

    public List<LanguageBean> getAvailableLanguages() {
        return availableLanguages;
    }

    private List<LanguageBean> populateAvailableLanguages() {
        List<String> languages = discoverChannelService.getAvailableLanguages();
        List<LanguageBean> sortedList = AbstractDiscoverChannelActionSupport.sortLanguages(languages);
        LanguageBean noLanguage = new LanguageBean();
        noLanguage.setCode("none");
        noLanguage.setName(getText("form.select.notSpecified"));
        sortedList.add(0, noLanguage);

        return sortedList;
    }
}
