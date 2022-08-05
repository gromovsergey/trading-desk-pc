package com.foros.action.account;

import com.foros.action.IdNameBean;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.LocalizableNameEntityHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;

public class EditAdvertiserAccountActionBase extends EditAdvertisingAccountActionBase<AdvertiserAccount> {

    @EJB
    protected DisplayCreativeService displayCreativeService;

    private List<IdNameBean> contentCategories;
    private List<IdNameBean> availableContentCategories;

    public List<IdNameBean> getContentCategories() {
        if (contentCategories == null) {
            Set<CreativeCategory> categories = accountService.loadCategories(account);
            contentCategories = prepareCategories(new ArrayList<>(categories));
        }
        return contentCategories;
    }

    public List<IdNameBean> getAvailableContentCategories() {
        if (availableContentCategories == null) {
            availableContentCategories = prepareCategories(
                    displayCreativeService.findCategoriesByType(CreativeCategoryType.CONTENT));
        }
        return availableContentCategories;
    }

    private List<IdNameBean> prepareCategories(List<CreativeCategory> categories) {
        Collections.sort(categories, new LocalizableNameEntityComparator());
        return LocalizableNameEntityHelper.convertToIdNameBeans(categories);
    }
}
