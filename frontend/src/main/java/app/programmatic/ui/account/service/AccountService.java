package app.programmatic.ui.account.service;

import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.account.dao.model.Account;
import app.programmatic.ui.account.dao.model.AccountEntity;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.dao.model.PublisherAccount;
import app.programmatic.ui.common.view.IdName;

import java.math.BigDecimal;
import java.util.List;


public interface AccountService {

    AdvertisingAccount findAdvertising(Long id);

    AdvertisingAccount findAdvertisingUnchecked(Long id);

    PublisherAccount findPublisherUnchecked(Long id);

    List<IdName> findPublishers();

    List<IdName> findPublishersForReferrerReport();

    List<IdName> findAllChannelOwners();

    List<IdName> findInternalAccounts();

    List<AccountEntity> findAdvertisingList();

    Account findAccountUnchecked(Long id);

    BigDecimal getAccountAvailableBudget(Long id);

    Long createAdvertiserInAgency(AdvertisingAccount advertiserInAgency);

    AdvertisingAccount updateAdvertiserInAgency(AdvertisingAccount advertiserInAgency);

    List<AdvertisingAccount> findAdvertisersByAgency(Long accountId);

    void uploadDocument(MultipartFile file, Long accountId);

    List<String> listDocuments(Long accountId);

    Boolean checkDocuments(Long accountId);

    byte[] downloadDocument(String name, Long accountId);

    void deleteDocument(String name, Long accountId);
}
