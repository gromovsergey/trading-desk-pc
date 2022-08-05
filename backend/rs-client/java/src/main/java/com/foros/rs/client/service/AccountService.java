package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.JAXBEntity;
import com.foros.rs.client.model.account.Account;
import com.foros.rs.client.model.account.AccountInAgencySelector;
import com.foros.rs.client.model.account.AccountSelector;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.util.UrlBuilder;

public class AccountService {

    private RsClient rsClient;

    private ReadonlyServiceSupport<AccountSelector, Account> commonSearch;

    private ReadonlyServiceSupport<AccountInAgencySelector, Account> searchInAgency;

    public AccountService(RsClient rsClient) {
        this.rsClient = rsClient;
        commonSearch = new ReadonlyServiceSupport<AccountSelector, Account>(rsClient, "/account/search") {};

        searchInAgency = new ReadonlyServiceSupport<AccountInAgencySelector, Account>(rsClient, "/account/searchInAgency") {};
    }

    public Result<Account> search(AccountSelector selector) {
        return commonSearch.get(selector);
    }

    public Result<Account> searchInAgency(AccountInAgencySelector selector) {
        return searchInAgency.get(selector);
    }

    public Account get(Long id) {
        String uri = UrlBuilder.path("/account").setQueryParameter("id", id).build();
        return rsClient.get(uri);
    }

    public Result<Account> get(String name) {
        String uri = UrlBuilder.path("/account/searchAdvertiserByName").setQueryParameter("name", name).build();
        return rsClient.get(uri);
    }

    public OperationsResult perform(Operations<Account> accountOperations) {
        return rsClient.post("/account/advertiserInAgency", new JAXBEntity(accountOperations));
    }
}