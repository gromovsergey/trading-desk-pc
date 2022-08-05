package com.foros.session.admin.accountType;

import com.foros.model.account.Account;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.security.AccountType;
import com.foros.service.ByIdLocatorService;
import com.foros.session.EntityTO;
import com.foros.session.NamedTO;
import com.foros.session.campaign.CCGEntityTO;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

@Local
public interface AccountTypeService extends ByIdLocatorService<AccountType> {
    void create(AccountType entity);

    AccountType update(AccountType entity);

    void refresh(Long id);

    AccountType findById(Long id);

    AccountType view(Long id);

    List<AccountType> findAll();

    Set<DeviceChannel> getAccountDeviceChannels(Long id);

    public List<AccountType> findByRole(String roleName);

    public List<NamedTO> findIndexByRole(String roleName);

    public List<EntityTO> findCreativeSizes(Long accountTypeId);

    public List<CCGEntityTO> getCampaignCreativeGroupsLinkedByFlag(AccountType entity, long flag);

    public List<EntityTO> getTagsLinkedByInventoryEstimationFlag(Long accountTypeId);

    public List<CCGEntityTO> getLinkedTextCampaignCreativeGroups(AccountType entity);

    public List<EntityTO> getSiteListForWDTagsFlag(Long accountTypeId);

    public List<EntityTO> getSitesLinkedByAdvExclusionFlag(Long accountTypeId);

    public List<EntityTO> getSitesLinkedByFrequencyCapsFlag(Long accountTypeId);

    public List<CCGEntityTO> getCCGRateTypeListLinkedToAccountType(AccountType entity, RateType rateType, CCGType ccgType, TGTType tgtType);

    public List<EntityTO> getDisplayCreativesLinkedToAccountType(AccountType entity);

    public List<EntityTO> getDisplayCampaignsLinkedToAccountType(AccountType entity);

    public List<EntityTO> getTextCampaignsLinkedToAccountType(AccountType entity);

    public List<EntityTO> getTagsLinkedByAdvExclusionFlag(Long accountTypeId);

    public boolean hasAccountLinkedByAccountType(AccountType entity);

    public List<EntityTO> getAccountLinkedByAccountType(AccountType entity);

    AccountTypeDisabledFields getAccountTypeChangesCheck(AccountType at);

    boolean checkAccountCanMoved(Account account, AccountType from, AccountType to);

    public void validateFieldChanges(AccountType existingAccountType, AccountType accountType);
    
    boolean hasSitesLinkedByExclusionApproval(AccountType entity);
}
