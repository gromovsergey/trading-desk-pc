package com.foros.util.helper;

import com.foros.cache.NamedCO;
import com.foros.cache.application.CountryCO;
import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.AccountType;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.security.AccountTO;
import com.foros.util.EntityUtils;
import com.foros.util.TimezoneHelper;
import com.foros.util.comparator.StatusLocalizableTOComparator;
import com.foros.util.messages.MessageProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IndexHelper {

    public static Collection<EntityTO> getAdvertisingChannelsByAccountAndStatuses(Long accountId, DisplayStatus[] displayStatuses) {
        return EntityUtils.sortByStatus(
                ServiceHelper.getSearchChannelService().findAdvertisingByAccountAndStatuses(accountId, displayStatuses));
    }

    public static List<EntityTO> getAdvertisersList(Long accountId) {
        if (accountId != null) {
            AccountService as = ServiceHelper.getAccountService();
            Account account = as.find(accountId);
            Collection<EntityTO> advertisers;
            if (account instanceof AdvertiserAccount) {
                advertisers = Arrays.asList(new EntityTO(account.getId(), account.getName(), account.getStatus().getLetter()));
            } else {
                advertisers = EntityUtils.sortByStatus(as.findAdvertisersTOByAgency(accountId));
            }
            return (List<EntityTO>) EntityUtils.applyStatusRules(advertisers, null, SecurityContext.isInternal());
        }
        return Collections.emptyList();
    }

    public static Collection<EntityTO> getCampaignsList(Long accId) {
        return getCampaignsList(accId, false);
    }

    public static Collection<EntityTO> getCampaignsList(Long accId, boolean onlyWithTextGroups) {
        CampaignService cs = ServiceHelper.getCampaingService();
        Collection<EntityTO> campaigns;
        if (accId != null) {
            campaigns = onlyWithTextGroups ?
                    cs.getTextCampaignsByAccount(accId) :
                    cs.getCampaignsByAccount(accId);
            return campaigns;
        }

        return Collections.emptyList();
    }

    public static Collection<EntityTO> getGroupsList(Long campaignId) {
        if (campaignId != null) {
            CampaignCreativeGroupService cs = ServiceHelper.getCampaignCreativeGroupService();
            return EntityUtils.sortByStatus(cs.getIndex(campaignId));
        }
        return Collections.emptyList();
    }

    public static Collection<EntityTO> getCreativesList(Long accountId, Long campaignId, Long ccgId) {
        return getCreativesList(accountId, campaignId, ccgId, false);
    }

    public static Collection<EntityTO> getCreativesList(Long accountId, Long campaignId, Long ccgId, boolean onlyTextAds) {
        DisplayCreativeService cs = ServiceHelper.getDisplayCreativeService();
        if (ccgId != null) {
            return EntityUtils.sortByStatus(cs.findByCreativeGroupId(ccgId));
        }

        if (campaignId != null) {
            return EntityUtils.sortByStatus(cs.findByCampaignId(campaignId, onlyTextAds));
        }
        if (accountId != null) {
            return EntityUtils.sortByStatus(cs.findEntityTOByAdvertiser(accountId, onlyTextAds));
        }
        return Collections.emptyList();
    }

    public static List<AccountTO> getAccountsList(AccountRole... roles) {
        return getAccountsList(!ServiceHelper.getUserService().getMyUser().isDeletedObjectsVisible(), roles);
    }

    public static List<AccountTO> getAccountsList(boolean excludeDeleted, AccountRole... roles) {
        return getAccountsList(excludeDeleted, null, roles);
    }

    public static List<AccountTO> getAccountsList(String countryCode, AccountRole... roles) {
        String[] countryCodes = null;
        if (countryCode != null && !countryCode.isEmpty()) {
            countryCodes = new String[] {countryCode};
        }
        return getAccountsList(!ServiceHelper.getUserService().getMyUser().isDeletedObjectsVisible(), countryCodes, roles);
    }

    public static List<AccountTO> getAccountsList(boolean excludeDeleted, String[] countryCodes, AccountRole... roles) {
        if (SecurityContext.isInternal()) {
            List<AccountTO> index = ServiceHelper.getAccountService().search(excludeDeleted, null, countryCodes, roles);
            EntityUtils.sortByStatus(index);
            return (List<AccountTO>) EntityUtils.applyStatusRules(index, null, true);
        }
        return null;
    }

    public static List<AccountTO> getInventoryPublisherAccountList() {
        if (SecurityContext.isInternal()) {
            List<AccountTO> index = ServiceHelper.getAccountService().searchByRoleAndTypeFlags(
                    AccountRole.PUBLISHER, AccountType.PUBLISHER_INVENTORY_ESTIMATION_FLAG);
            return (List<AccountTO>) EntityUtils.applyStatusRules(index, null, true);
        }
        return null;
    }

    public static Collection<CountryCO> getCountryList() {
        return ServiceHelper.getCountryService().getIndex();
    }

    public static Collection<CountryCO> getCountryList(Collection<Long> accountIds) {
        return ServiceHelper.getCountryService().getIndex(accountIds);
    }

    public static Collection<EntityTO> getColocationsList(Long accountId) {
        if (accountId != null) {
            return EntityUtils.sortByStatus(ServiceHelper.getColocationService().getIndex(accountId));
        }

        return Collections.emptyList();
    }

    public static List<EntityTO> getSitesList(Long accountId) {
        if (accountId != null) {
            return (List<EntityTO>) EntityUtils.sortByStatus(ServiceHelper.getSiteService().getIndex(accountId));
        }
        return Collections.emptyList();
    }

    public static Collection<EntityTO> getTagsList(Long siteId) {
        if (siteId != null) {
            return EntityUtils.sortByStatus(ServiceHelper.getTagsService().getList(siteId));
        }
        return Collections.emptyList();
    }

    public static List<CreativeSizeTO> getSizesList() {
        boolean withDeleted = ServiceHelper.getUserService().getMyUser().isDeletedObjectsVisible();
        List<CreativeSizeTO> sizes = new ArrayList<>(ServiceHelper.getCreativeSizeService().findAll(withDeleted));
        Collections.sort(sizes, StatusLocalizableTOComparator.INSTANCE);
        return sizes;
    }

    public static Collection<NamedCO<Long>> getTimezonesList() {
        return TimezoneHelper.sort(ServiceHelper.getAccountService().getTimeZoneIndex(),
                MessageProvider.createMessageProviderAdapter());
    }
}
