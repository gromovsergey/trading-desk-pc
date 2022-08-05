package com.foros.action.account;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.TnsAdvertiser;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.account.yandex.advertiser.YandexTnsAdvertiserService;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;

public class SaveAdvertiserAccountActionBase extends SaveAccountActionBase<AdvertiserAccount> {
    private Collection<Long> selectedContentCategories;

    private Long tnsAdvertiserId;
    private Long tnsBrandId;

    @EJB
    private YandexTnsAdvertiserService tnsAdvertiserService;

    @EJB
    private YandexTnsBrandService tnsBrandService;

    protected void prepareCategories() {
        Set<CreativeCategory> categories = new HashSet<CreativeCategory>();
        if (selectedContentCategories != null) {
            for (Long categoryId : selectedContentCategories) {
                CreativeCategory creativeCategory = new CreativeCategory(categoryId);
                creativeCategory.setType(CreativeCategoryType.CONTENT);
                categories.add(creativeCategory);
            }
        }
        account.setCategories(categories);
    }

    public void setSelectedContentCategories(Collection<Long> selectedContentCategories) {
        this.selectedContentCategories = selectedContentCategories;
    }

    protected void prepareTnsObjects() {
        if (TnsAdvertiser.COUNTRY_CODE.equalsIgnoreCase(getExistingAccount().getCountry().getCountryCode())) {
            if (getTnsAdvertiserId() != null) {
                account.setTnsAdvertiser(tnsAdvertiserService.find(tnsAdvertiserId));
            } else {
                account.setTnsAdvertiser(null);
            }

            if (getTnsBrandId() != null) {
                account.setTnsBrand(tnsBrandService.find(tnsBrandId));
            } else {
                account.setTnsBrand(null);
            }
        }
    }

    public Long getTnsAdvertiserId() {
        return tnsAdvertiserId;
    }

    public void setTnsAdvertiserId(Long tnsAdvertiserId) {
        this.tnsAdvertiserId = tnsAdvertiserId;
    }

    public Long getTnsBrandId() {
        return tnsBrandId;
    }

    public void setTnsBrandId(Long tnsBrandId) {
        this.tnsBrandId = tnsBrandId;
    }
}
