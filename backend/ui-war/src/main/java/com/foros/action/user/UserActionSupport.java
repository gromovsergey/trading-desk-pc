package com.foros.action.user;

import com.foros.action.BaseActionSupport;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.User;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.security.UserService;
import com.foros.session.site.SiteService;
import com.foros.util.EntityUtils;
import com.foros.util.comparator.LocalizableTOComparator;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

abstract class UserActionSupport extends BaseActionSupport implements ModelDriven<User> {
    @EJB
    protected UserService userService;

    @EJB
    protected AccountService accountService;

    @EJB
    private ConfigService configService;

    @EJB
    protected SiteService siteService;

    @EJB
    protected CurrentUserService currentUserService;

    protected User user = new User();

    private List<EntityTO> userAdvertisers;
    private List<EntityTO> accountAdvertisers;

    // Site-level access control
    private List<EntityTO> accountSites; // Access not allowed to
    private List<EntityTO> userSites;    // Access allowed to

    private Map<String, Boolean> showDeletedObjectsOption = new LinkedHashMap<String, Boolean>() {{
        put("user.deleted.objects.hide", false);
        put("user.deleted.objects.show", true);
    }};

    @Override
    public User getModel() {
        return user;
    }

    public Map<String, Boolean> getShowDeletedObjectsOption() {
        return showDeletedObjectsOption;
    }

    public void switchContext(RequestContexts contexts) {
        Account account = accountService.find(user.getAccount().getId());
        contexts.switchTo(account);
    }

    public boolean isInternalPasswordAuthorizationAllowed() {
        return configService.get(ConfigParameters.PASSWORD_AUTHENTICATION_ALLOWED);
    }

    public List<EntityTO> getUserAdvertisers() {
        if (user.isAdvLevelAccessFlag() && userAdvertisers == null) {
            List<AdvertiserAccount> advertisers = userService.findUserAdvertisers(user.getId());
            userAdvertisers = EntityUtils.convertWithStatusRules(advertisers, null, currentUserService.getUser().isDeletedObjectsVisible());
            Collections.sort(userAdvertisers, new LocalizableTOComparator<>());
        }
        return userAdvertisers;
    }

    public List<EntityTO> getAccountAdvertisers() {
        if (accountAdvertisers == null) {
            Collection<AdvertiserAccount> advertisersByAgency = accountService.findAdvertisersByAgencyUser(user.getId());
            accountAdvertisers = EntityUtils.convertWithStatusRules(advertisersByAgency, null, currentUserService.getUser().isDeletedObjectsVisible());
            Collections.sort(accountAdvertisers, new LocalizableTOComparator<>());
        }
        return accountAdvertisers;
    }

    public List<EntityTO> getUserSites() {
        if (user.isSiteLevelAccessFlag() && userSites == null) {
            userSites = userService.findUserSites(user.getId());
            Collections.sort(userSites, new LocalizableTOComparator<>());
        }
        return userSites;
    }

    public List<EntityTO> getAccountSites() {
        if (accountSites == null) {
            accountSites = siteService.getIndex(user.getAccount().getId());
            Collections.sort(accountSites, new LocalizableTOComparator<>());
        }
        return accountSites;
    }
}
