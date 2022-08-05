package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.currency.CurrencyExchange;
import com.foros.rs.client.util.UrlBuilder;

public class CurrencyService {

    private RsClient rsClient;

    public CurrencyService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public CurrencyExchange getCurrencyExchange(Long id) {
        String uri = UrlBuilder.path("/currency/exchange").setQueryParameter("exchange.id", id).build();
        return rsClient.get(uri);
    }

    public CurrencyExchange getLastCurrencyExchange() {
        String uri = UrlBuilder.path("/currency/exchange").build();
        return rsClient.get(uri);
    }
}
