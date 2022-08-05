package com.foros.session.account.yandex.brand;

import com.foros.model.account.TnsBrand;

import java.util.List;

import javax.ejb.Local;

@Local
public interface YandexTnsBrandService {
    public void synchronize();

    TnsBrand find(Long tnsBrandId);

    List<TnsBrand> searchBrands(String name, int maxResults);

}
