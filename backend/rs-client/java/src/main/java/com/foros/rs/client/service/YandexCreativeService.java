package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.campaign.YandexCreative;
import com.foros.rs.client.model.advertising.campaign.YandexCreativeSelector;

public class YandexCreativeService extends ReadonlyServiceSupport<YandexCreativeSelector, YandexCreative> {
    public YandexCreativeService(RsClient rsClient) {
        super(rsClient, "/yandexCreatives");
    }
}