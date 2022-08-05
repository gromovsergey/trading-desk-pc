package com.foros.session.account;

import com.foros.cache.NamedCO;
import com.foros.model.DisplayStatus;
import com.foros.model.Timezone;
import com.foros.model.account.Account;
import com.foros.model.account.AccountSearchTestOption;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.Result;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.FileManager;
import com.foros.session.security.AccountStatsTO;
import com.foros.session.security.AccountTO;
import com.foros.session.security.ExtensionAccountTO;
import com.foros.session.security.ManagerAccountTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

@Local
public interface AccountService {

    String EMPTY_ADDRESS_CITY = " ";

    Long createInternalAccount(InternalAccount account);

    Long createExternalAccount(ExternalAccount account);

    Long createAgencyAccount(AgencyAccount account);

    Long createStandaloneAdvertiserAccount(AdvertiserAccount account);

    void updateInternalAccount(InternalAccount account);

    void updateExternalAccount(ExternalAccount account);

    void updateAgencyAccount(AgencyAccount account);

    void updateStandaloneAdvertiserAccount(AdvertiserAccount account);

    void delete(Long id);

    void undelete(Long id);

    void inactivate(Long id);

    void activate(Long id);

    void deleteAgencyAdvertiser(Long id);

    void undeleteAgencyAdvertiser(Long id);

    void inactivateAgencyAdvertiser(Long id);

    void activateAgencyAdvertiser(Long id);

    void refresh(Long id);

    Account getMyAccount();

    Account getMyAccountWithTerms();

    Account view(Long id);

    Account findForEdit(Long id);

    AdvertiserAccount viewAdvertiserAccount(Long id);

    AdvertiserAccount findAdvertiserAccount(Long id);

    InternalAccount findInternalAccount(Long id);

    AgencyAccount viewAgencyAccount(Long id);

    AgencyAccount findAgencyAccount(Long id);

    PublisherAccount findPublisherAccount(Long id);

    IspAccount findIspAccount(Long id);

    PublisherAccount viewPublisherAccount(Long id);

    IspAccount viewIspAccount(Long id);

    CmpAccount viewCmpAccount(Long id);

    InternalAccount viewInternalAccount(Long id);

    ExternalAccount viewExternalAccount(Long id);

    /**
     * This method is defined without any policies.
     * This behaves exactly as find(id) method, the only difference is this method doesnot expect
     * the user to have "VIEW_*" policies.
     *
     * @param id - account id
     * @return Account instance or throws EntityNotFoundException if no such exists
     */
    Account find(Long id);

    String getAccountName(Long id);

    String getAccountName(Long id, boolean appendStatusSuffix);


    List<AccountTO> searchByRoleAndTypeFlags(AccountRole role, long accountTypeFlag);

    List<AccountTO> search(AccountRole... roles);

    List<AccountTO> search(boolean excludeDeleted, AccountRole... roles);

    List<AccountTO> search(boolean excludeDeleted, Long intAccId, String[] countryCodes, AccountRole... roles);

    List<TreeFilterElementTO> searchAdvertisersWithCampaigns(Long agencyId, Boolean display);

    List<TreeFilterElementTO> searchAdvertisersBySizeTypeWithCampaigns(Long agencyId, Long sizeTypeId);

    List<TreeFilterElementTO> searchAdvertisersWithConversions(Long agencyId);

    List<EntityTO> getAccountUsers(Long id);

    List<EntityTO> getSalesManagers(AdvertiserAccount account);

    /**
     * Search all accounts whose accountRole is advertiser for the given criteria
     */
    List<AccountStatsTO> searchAdvertiserAccounts(String name, Long accountTypeId, Long internalAccountId,
                                                  String countryCode, Long accountManagerId, AccountSearchTestOption testOption, DisplayStatus... displayStatuses);

    /**
     * Search all accounts whose accountRole is publisher for the given criteria
     */
    List<AccountStatsTO> searchPublisherAccounts(String name, Long accountTypeId, Long internalAccountId,
                                                 String countryCode, Long accountManagerId, AccountSearchTestOption testOption, DisplayStatus... displayStatuses);

    /**
     * Search all accounts whose accountRole is ISP for the given criteria
     */
    List<AccountStatsTO> searchISPAccounts(String name, Long accountTypeId, Long internalAccountId,
                                           String countryCode, Long accountManagerId, AccountSearchTestOption testOption, DisplayStatus... displayStatuses);

    /**
     * Search all accounts whose accountRole is CMP for the given criteria
     */
    List<AccountStatsTO> searchCMPAccounts(String name, Long accountTypeId, Long internalAccountId,
                                           String countryCode, Long accountManagerId, DisplayStatus... displayStatuses);

    AccountTO findIndex(Long id);

    Collection<AdvertiserAccount> findAdvertisersByAgencyUser(Long accountId);

    Long addAdvertiser(AdvertiserAccount advertiser);

    void updateAdvertiser(AdvertiserAccount advertiser);

    void refreshAdvertiser(Long id);

    Collection<NamedCO<Long>> getTimeZoneIndex();

    List<User> findAccountUsers(Long accountId);

    boolean getAccountTestFlag(Long id);

    Timezone getAccountTimeZone(Long id);

    FileManager getOpportunitiesFileManager(Opportunity opportunity);

    FileManager getOpportunitiesFileManagerForView(Opportunity opportunity);

    FileManager getOpportunitiesFileManagerForUpdate(Opportunity opportunity);

    FileManager getDocumentsFileManagerForView(Long accountId);

    FileManager getDocumentsFileManagerForUpdate(Long accountId);

    FileManager getChannelReportFileManager(Long accountId);

    FileManager getCreativesFileManager(AdvertiserAccount account);

    FileManager getTextAdImagesFileManager(AdvertiserAccount account);

    FileManager getPublisherAccountFileManager(Account account);

    FileManager getTermsFileManager(Account account);

    ContentSource getTermContent(Account account, String file);

    void addTerm(Account account, String fileName, InputStream is) throws IOException;

    boolean deleteTerm(Account account, String file);

    boolean hasUsers(Long accountId);

    boolean hasAdvertisers(Long accountId);

    AdvertiserAccount viewAdvertiserInAgencyAccount(Long id);

    AdvertisingAccountBase viewAgencyOrStandAloneAdvertiser(Long id);

    Collection<EntityTO> findAdvertisersTOByAgency(Long agencyAccountId);

    <T extends Account> T findForSwitching(Class<T> expectedClass, Long accountId);

    List<ManagerAccountTO> getChannelOwners();

    List<EntityTO> getInternalAccounts(boolean excludeDeleted);

    List<EntityTO> getInternalAccountsWithoutRestricted(boolean excludeDeleted);

    List<ManagerAccountTO> getAllChannelOwners();

    void updateDisplayStatus(Account account, boolean recursive);

    Set<CreativeCategory> loadCategories(Account account);

    Result<ExtensionAccountTO> get(AccountSelector accountSelector);

    Result<ExtensionAccountTO> getAdvertiserAccounts(String name);

    Result<? super ExtensionAccountTO> getExtensionAccountTOByAgency(AccountInAgencySelector selector);
}
