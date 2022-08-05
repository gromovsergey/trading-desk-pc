package app.programmatic.ui.account.service;

import app.programmatic.ui.account.dao.model.*;

import java.util.EnumSet;
import java.util.List;

public interface SearchAccountService {
    AdditionalSearchParams getAdditionalSearchParams();

    List<AccountStats> searchAdvertisingAccounts(String name, String countryCode,
        EnumSet<AccountDisplayStatus> displayStatuses, AccountRoleParam roleParam);

    List<AdvertiserInAgencyStats> searchInAgencyAdvertisingAccounts(Long agencyId);
}
