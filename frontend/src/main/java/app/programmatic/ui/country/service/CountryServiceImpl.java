package app.programmatic.ui.country.service;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.country.model.Country;
import app.programmatic.ui.country.model.CountryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private AccountService accountService;

    @Autowired
    private CountryRepository countryRepository;


    @Override
    public Country getCountryByAccountId(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertising(accountId);
        return countryRepository.findByCountryCode(account.getCountryCode());
    }

    @Override
    public Iterable<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public List<Country> findAllOrdered() {
        return countryRepository.findBySortOrderIsNotNull();
    }
}
