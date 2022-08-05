package com.foros.session.account.yandex.advertiser;

import com.foros.model.account.TnsAdvertiser;

import java.util.List;

import javax.ejb.Local;

@Local
public interface YandexTnsAdvertiserService {
    void synchronize();

    List<TnsAdvertiser> searchAdvertisers(String query, int autocompleteSize);

    TnsAdvertiser find(Long id);
}
