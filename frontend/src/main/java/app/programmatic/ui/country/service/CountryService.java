package app.programmatic.ui.country.service;

import app.programmatic.ui.country.model.Country;

import java.util.List;

public interface CountryService {
    Country getCountryByAccountId(Long accountId);

    Iterable<Country> findAll();

    List<Country> findAllOrdered();
}
