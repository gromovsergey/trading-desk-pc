package com.foros.session.admin;

import com.foros.model.admin.DynamicResource;

import java.util.Collections;
import java.util.List;

public class DynamicResourcesServiceMock implements DynamicResourcesService {
    @Override
    public DynamicResource findResources(String resourceKey, String lang) {
        return new DynamicResource();
    }

    @Override
    public List<DynamicResource> findResources(String resourceKey) {
        return Collections.emptyList();
    }

    @Override
    public List<DynamicResource> findLangResources(String lang) {
        return Collections.emptyList();
    }

    @Override
    public void saveResources(List<DynamicResource> created, List<DynamicResource> updated, List<DynamicResource> deleted) {
    }

    @Override
    public void refresh() {
    }

}
