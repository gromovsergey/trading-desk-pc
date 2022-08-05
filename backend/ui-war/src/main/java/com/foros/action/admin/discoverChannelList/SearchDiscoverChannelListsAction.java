package com.foros.action.admin.discoverChannelList;

import com.foros.action.LanguageBean;
import com.foros.action.admin.channel.ChannelSearchStatus;
import com.foros.action.admin.channel.SearchChannelsActionBase;
import com.foros.action.admin.discoverChannel.AbstractDiscoverChannelActionSupport;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.ServiceLocator;
import com.foros.session.channel.DiscoverChannelListTO;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.session.query.PartialList;
import com.foros.session.security.UserService;
import com.foros.util.StringUtil;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

public class SearchDiscoverChannelListsAction extends SearchChannelsActionBase {

    private static final ChannelSearchStatus[] statuses = new ChannelSearchStatus[]{
            ChannelSearchStatus.ALL,
            ChannelSearchStatus.ALL_BUT_DELETED,
            ChannelSearchStatus.LIVE,
            ChannelSearchStatus.INACTIVE,
            ChannelSearchStatus.DELETED};

    private static final ChannelSearchStatus[] noDeletedStatuses = new ChannelSearchStatus[]{
            ChannelSearchStatus.ALL_HIDE_DELETED,
            ChannelSearchStatus.LIVE,
            ChannelSearchStatus.INACTIVE};

    @EJB
    protected DiscoverChannelService discoverChannelService;

    @EJB
    protected UserService userService;

    private List<LanguageBean> availableLanguages;
    private PartialList<DiscoverChannelListTO> channelLists;
    private boolean hideCountryColumn;
    private boolean hideAccountColumn;
    private String newsGateUrl;

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
        try {
            Long accountId = searchParams.getAccountId();
            hideAccountColumn = accountId != null && accountId != 0;

            String countryCode = searchParams.getCountryCode();
            hideCountryColumn = StringUtil.isPropertyNotEmpty(countryCode);

            channelLists = searchChannelService.searchDiscoverLists(searchParams.getName(),
                                                 accountId,
                                                 countryCode,
                                                 searchParams.getLanguage(),
                                                 searchParams.getFirstResultCount(),
                                                 searchParams.getPageSize(),
                                                 searchParams.getPhrase(),
                                                 ChannelSearchStatus.toDisplayStatuses(searchParams.getStatus()));
            searchParams.setTotal((long) channelLists.getTotal());

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

    public List<LanguageBean> getAvailableLanguages() {
        return availableLanguages;
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

    public PartialList<DiscoverChannelListTO> getChannelLists() {
        return channelLists;
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

    @Override
    public ChannelSearchStatus[] getStatuses() {
        return (userService.getMyUser().isDeletedObjectsVisible()) ? statuses : noDeletedStatuses;
    }
}
