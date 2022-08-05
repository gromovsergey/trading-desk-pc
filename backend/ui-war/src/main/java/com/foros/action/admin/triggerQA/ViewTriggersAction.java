package com.foros.action.admin.triggerQA;

import com.foros.cache.application.CountryCO;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.TriggerChannelType;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.channel.triggerQA.TriggerQASearchFilter;
import com.foros.session.channel.triggerQA.TriggerQASearchParameters;
import com.foros.session.channel.triggerQA.TriggerQASortType;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.util.CollectionUtils;
import com.foros.util.CountryHelper;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;
import com.foros.util.jpa.DetachedList;
import com.foros.util.mapper.DisplayStatusMapper;

import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class ViewTriggersAction extends TriggersActionSupport {
    @EJB
    AccountService accountService;

    @EJB
    private ConfigService configService;

    private boolean saved = false;
    private List<EntityTO> internalAccounts;

    @Validations(
        requiredFields = {
            @RequiredFieldValidator(fieldName = "page", key = "errors.field.required")
        },
        intRangeFields = {
            @IntRangeFieldValidator(fieldName = "page", key = "errors.field.number")
        }
    )
    @ReadOnly
    @Restrict(restriction = "TriggerQA.view")
    public String search() {
        searchParams.setPageSize(configService.get(ConfigParameters.TRIGGER_QA_PAGE_SIZE));
        DetachedList<TriggerQATO> triggers = getTriggerQAService().search(createSearchParameter());
        setTriggers(triggers);
        searchParams.setTotal(Long.valueOf(triggers.getTotal()));
        return SUCCESS;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    private DisplayStatus[] getDisplayStatuses() {
        Long displayStatusId = searchParams.getType() == TriggerChannelType.DISCOVER ? searchParams.getDiscoverDisplayStatusId()
                : searchParams.getDisplayStatusId();

        return displayStatusId == null || displayStatusId == 0L ?
               null :
               new DisplayStatus[] {Channel.getDisplayStatus(displayStatusId)};
    }

    @ReadOnly
    @Restrict(restriction = "TriggerQA.view")
    public String list() {
        // load account's country
        initCountry();
        return SUCCESS;
    }

    public Map<String, String> getAccountRoles() {
        return CollectionUtils
                .localizeMap("", "form.all")
                .map("EXTERNAL", "enum.accountRole.EXTERNAL")
                .map("INTERNAL", "enum.accountRole.INTERNAL")
                .build();
    }

    public Map<String, String> getTriggerTypes() {
        return CollectionUtils
                .localizeMap("", "TriggersApproval.all")
                .map(TriggerQAType.URL.toString(), "TriggersApproval.urls")
                .map(TriggerQAType.KEYWORD.toString(), "TriggersApproval.keywords")
                .build();
    }

    public Map<String, String> getTypes() {
        return CollectionUtils
                .localizeMap("A", "TriggersApproval.channel.type.A")
                .map("D", "TriggersApproval.channel.type.D")
                .build();
    }

    public Map<String, String> getApprovalTypes() {
        return CollectionUtils
                .localizeMap("", "searchParams.approval.all")
                .map("E", "searchParams.approval.reviewed")
                .map("A", "searchParams.approval.approved")
                .map("D", "searchParams.approval.declined")
                .map("H", "searchParams.approval.pendingReview")
                .build();
    }

    public Map<ChannelVisibilityCriteria, String> getVisibilityTypes() {
        return CollectionUtils
                .localizeMap(ChannelVisibilityCriteria.ALL, "form.all")
                .map(ChannelVisibilityCriteria.PUBLIC, "channel.visibility.PUB")
                .map(ChannelVisibilityCriteria.PRIVATE, "channel.visibility.PRI")
                .map(ChannelVisibilityCriteria.CMP, "channel.visibility.CMP")
                .build();
    }

    public Map<Long, String> getChannelStatuses() {
        return CollectionUtils
                .map(0L, StringUtil.getLocalizedString("searchParams.status.all"))
                .items(new DisplayStatusMapper(), Arrays.asList(
                        Channel.LIVE,
                        Channel.LIVE_PENDING_INACTIVATION,
                        Channel.LIVE_TRIGGERS_NEED_ATT,
                        Channel.LIVE_AMBER_PENDING_INACTIVATION,
                        Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
                        Channel.DECLINED,
                        Channel.PENDING_FOROS,
                        Channel.INACTIVE,
                        Channel.DELETED
                ))
                .build();
    }

    public Map<Long, String> getDiscoverChannelStatuses() {
        return CollectionUtils
                .map(0L, StringUtil.getLocalizedString("searchParams.status.all"))
                .items(new DisplayStatusMapper(), Arrays.asList(
                        Channel.LIVE,
                        Channel.LIVE_TRIGGERS_NEED_ATT,
                        Channel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
                        Channel.DECLINED,
                        Channel.PENDING_FOROS,
                        Channel.INACTIVE,
                        Channel.DELETED
                ))
                .build();
    }

    public List<CountryCO> getCountries() {
        CountryService countrySvc = ServiceLocator.getInstance().lookup(CountryService.class);
        ArrayList<CountryCO> countries = new ArrayList<CountryCO>(CountryHelper.sort(countrySvc.getIndex()));

        return countries;
    }

    private void initCountry() {
        if (StringUtil.isPropertyEmpty(getSearchParams().getCountryCode())) {
            ApplicationPrincipal principal = SecurityContext.getPrincipal();
            AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
            Long accountId = principal.getAccountId();
            Account account = accountService.find(accountId);

            getSearchParams().setCountryCode(account.getCountry().getCountryCode());
        }
    }

    public Map<String, String> getOrderBy() {
        return CollectionUtils
                .localizeMap(TriggerQASortType.NEWEST.name(), TriggerQASortType.NEWEST.getOptionNameTranslation())
                .map(TriggerQASortType.OLDEST.name(), TriggerQASortType.OLDEST.getOptionNameTranslation())
                .map(TriggerQASortType.ATOZ.name(), TriggerQASortType.ATOZ.getOptionNameTranslation())
                .map(TriggerQASortType.ZTOA.name(), TriggerQASortType.ZTOA.getOptionNameTranslation())
                .map(TriggerQASortType.LASTREVIEWED.name(), TriggerQASortType.LASTREVIEWED.getOptionNameTranslation())
                .build();
    }

    public Map<TriggerQASearchFilter, String> getFilterBy() {
        return CollectionUtils
                .localizeMap(TriggerQASearchFilter.ALL, "form.all")
                .map(TriggerQASearchFilter.CHANNEL, "triggers.filterBy.channel")
                .map(TriggerQASearchFilter.CCG, "triggers.filterBy.ccg")
                .build();
    }

    public List<EntityTO> getInternalAccounts() {
        if (internalAccounts != null) {
            return internalAccounts;
        }

        internalAccounts = accountService.getInternalAccounts(false);

        EntityUtils.applyStatusRules(internalAccounts, null, true);

        return internalAccounts;
    }

    private TriggerQASearchParameters createSearchParameter() {
        TriggerQASearchParameters param;

        switch (searchParams.getType()) {
            case ADVERTISING:
                param = new TriggerQASearchParameters(
                    searchParams.getFirstResultCount(),
                    searchParams.getPageSize(),
                    searchParams.getTriggerType(),
                    searchParams.getFilterBy(),
                    searchParams.getCriteria(),
                    searchParams.getApproval(),
                    searchParams.getVisibility() == ChannelVisibilityCriteria.ALL ? null : searchParams.getVisibility(),
                    searchParams.getRoles(),
                    searchParams.getAccountId(),
                    searchParams.getChannelId(),
                    searchParams.getCountryCode(),
                    searchParams.getAdvertiserId(),
                    searchParams.getCampaignId(),
                    searchParams.getCcgId(),
                    getDisplayStatuses(),
                    TriggerQASortType.valueOf(searchParams.getOrderBy()));
                break;
            case DISCOVER:
                param = new TriggerQASearchParameters(
                    searchParams.getFirstResultCount(),
                    searchParams.getPageSize(),
                    searchParams.getTriggerType(),
                    searchParams.getCriteria(),
                    searchParams.getApproval(),
                    searchParams.getDiscoverAccountId(),
                    searchParams.getDiscoverChannelListId(),
                    searchParams.getDiscoverChannelId(),
                    searchParams.getCountryCode(),
                    getDisplayStatuses(),
                    TriggerQASortType.valueOf(searchParams.getOrderBy()));
                break;
            default:
                throw new IllegalArgumentException("Channel type is invalid");
        }

        return param;
    }
}
