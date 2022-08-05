package com.foros.action.admin.channel;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.action.channel.ChannelsSearchParams;
import com.foros.cache.application.CountryCO;
import com.foros.model.account.Account;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.security.ManagerAccountTO;
import com.foros.session.security.UserService;
import com.foros.util.CountryHelper;

import javax.ejb.EJB;
import java.util.List;

public abstract class SearchChannelsActionBase extends BaseActionSupport implements ModelDriven<ChannelsSearchParams> {
    private static final ChannelSearchStatus[] statuses = new ChannelSearchStatus[]{
            ChannelSearchStatus.ALL,
            ChannelSearchStatus.ALL_BUT_DELETED,
            ChannelSearchStatus.LIVE,
            ChannelSearchStatus.NOT_LIVE,
            ChannelSearchStatus.DECLINED,
            ChannelSearchStatus.PENDING_FOROS,
            ChannelSearchStatus.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
            ChannelSearchStatus.INACTIVE,
            ChannelSearchStatus.DELETED};

    private static final ChannelSearchStatus[] noDeletedStatuses = new ChannelSearchStatus[]{
            ChannelSearchStatus.ALL_HIDE_DELETED,
            ChannelSearchStatus.LIVE,
            ChannelSearchStatus.NOT_LIVE,
            ChannelSearchStatus.DECLINED,
            ChannelSearchStatus.PENDING_FOROS,
            ChannelSearchStatus.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
            ChannelSearchStatus.INACTIVE};

    @EJB
    protected SearchChannelService searchChannelService;

    @EJB
    protected AccountService accountService;

    @EJB
    protected CurrentUserService currentUserService;

    @EJB
    private CountryService countryService;

    @EJB
    protected CategoryChannelService categoryChannelService;

    @EJB
    protected UserService userService;

    protected ChannelsSearchParams searchParams = new ChannelsSearchParams();
    protected List<CountryCO> countries;
    protected Account myAccount;
    protected List<ManagerAccountTO> accounts;
    protected List<EntityTO> categoryChannels;

    public String main() {
        countries = CountryHelper.sort(countryService.getIndex());
        return SUCCESS;
    }

    @Override
    public ChannelsSearchParams getModel() {
        return searchParams;
    }

    public ChannelSearchStatus[] getStatuses() {
        return (userService.getMyUser().isDeletedObjectsVisible()) ? statuses : noDeletedStatuses;
    }

    public AccountSearchTestOption[] getTestOptions() {
        return AccountSearchTestOption.values();
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public Account getMyAccount() {
        if (myAccount == null) {
            myAccount = accountService.getMyAccount();
        }
        return myAccount;
    }

    public List<EntityTO> getCategoryChannels() {
        return categoryChannels;
    }

    public List<ManagerAccountTO> getAccounts() {
        return accounts;
    }
}
