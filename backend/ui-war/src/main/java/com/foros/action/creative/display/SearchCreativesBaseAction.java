package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.model.DisplayStatus;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativeService;
import com.foros.session.creative.CreativeSortType;
import com.foros.session.creative.CreativeTO;
import com.foros.session.security.UserService;

import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;

public class SearchCreativesBaseAction extends BaseActionSupport implements AdvertiserSelfIdAware {
    @EJB
    private AccountService accountService;

    @EJB
    private CreativeService creativeService;

    @EJB
    protected UserService userService;

    private CreativeSearchForm searchParams = new CreativeSearchForm();
    private Long advertiserId;
    private AdvertiserAccount account;

    public CreativeSearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(CreativeSearchForm searchParams) {
        this.searchParams = searchParams;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public AdvertiserAccount getAccount() {
        if (account == null) {
            account = accountService.findAdvertiserAccount(getAdvertiserId());
        }
        return account;
    }

    protected List<CreativeTO> searchCreatives() {
        int creativesCount = creativeService.findCreativesCount(
                advertiserId,
                getDisplayStatuses(),
                searchParams.getCampaignId(),
                searchParams.getSizeId(),
                true,
                true
        );
        searchParams.setTotal((long) creativesCount);
        return creativeService.findCreatives(
                advertiserId,
                getDisplayStatuses(),
                searchParams.getCampaignId(),
                searchParams.getSizeId(),
                true,
                true,
                searchParams.getFirstResultCount(),
                searchParams.getPageSize(),
                CreativeSortType.valueOf(searchParams.getOrderBy())
        );
    }

    private List<DisplayStatus> getDisplayStatuses() {
        if (getSearchParams().getDisplayStatusId() != null) {
            if (getSearchParams().getDisplayStatusId() == -1L) {
                return Arrays.asList(Creative.DECLINED, Creative.INACTIVE, Creative.LIVE, Creative.PENDING_FOROS, Creative.PENDING_USER);
            } else if (getSearchParams().getDisplayStatusId() == 0L) {
                if (userService.getMyUser().isDeletedObjectsVisible()) {
                    return Arrays.asList(Creative.DECLINED, Creative.INACTIVE, Creative.LIVE, Creative.PENDING_FOROS, Creative.PENDING_USER, Creative.DELETED);
                } else {
                    return Arrays.asList(Creative.DECLINED, Creative.INACTIVE, Creative.LIVE, Creative.PENDING_FOROS, Creative.PENDING_USER);
                }
            } else {
                return Arrays.asList(Creative.getDisplayStatus(getSearchParams().getDisplayStatusId()));
            }
        } else {
            return Arrays.asList(Creative.DECLINED, Creative.INACTIVE, Creative.LIVE, Creative.PENDING_FOROS, Creative.PENDING_USER);
        }
    }
}
