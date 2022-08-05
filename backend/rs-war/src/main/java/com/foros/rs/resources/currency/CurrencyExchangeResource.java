package com.foros.rs.resources.currency;

import com.foros.model.currency.CurrencyExchange;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/currency/exchange/")
public class CurrencyExchangeResource {

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @GET
    public CurrencyExchange get(@QueryParam("exchange.id") Long exchangeId) {
        return currencyExchangeService.get(exchangeId);
    }
}
