package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;

import org.junit.Test;

public class CurrencyServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        assertTrue(currencyService.getLastCurrencyExchange() != null);
    }
}
