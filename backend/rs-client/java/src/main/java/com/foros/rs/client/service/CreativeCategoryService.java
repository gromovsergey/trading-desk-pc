package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.creativeCategory.CreativeCategory;
import com.foros.rs.client.model.creativeCategory.CreativeCategorySelector;

public class CreativeCategoryService extends ReadonlyServiceSupport<CreativeCategorySelector, CreativeCategory> {

    public CreativeCategoryService(RsClient rsClient) {
        super(rsClient, "/creativeCategories");
    }
}
