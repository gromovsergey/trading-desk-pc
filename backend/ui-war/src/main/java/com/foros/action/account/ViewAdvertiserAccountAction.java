package com.foros.action.account;

import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.CreativeCategory;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.util.LocalizableNameEntityHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAdvertiserAccountAction extends ViewAdvertiserAccountActionBase {

    private Long id;

    private List<IdNameBean> contentCategories;

    protected void prepareCategories() {
        List<CreativeCategory> categories = new ArrayList<>(accountService.loadCategories(account));
        Collections.sort(categories, new LocalizableNameEntityComparator());
        contentCategories = LocalizableNameEntityHelper.convertToIdNameBeans(categories);
    }

    @ReadOnly
    public String view() {
        account = accountService.viewAgencyOrStandAloneAdvertiser(id);
        prepareCategories();
        return SUCCESS;
    }

    @ReadOnly
    public String viewAgencyAdvertiser() {
        account = accountService.viewAdvertiserInAgencyAccount(id);
        prepareCategories();
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<IdNameBean> getContentCategories() {
        return contentCategories;
    }
}
