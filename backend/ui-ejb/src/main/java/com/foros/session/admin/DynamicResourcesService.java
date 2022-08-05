package com.foros.session.admin;

import com.foros.model.admin.DynamicResource;
import java.util.List;
import javax.ejb.Local;

@Local
public interface DynamicResourcesService {
    DynamicResource findResources(String resourceKey, String lang);

    List<DynamicResource> findResources(String resourceKey);

    List<DynamicResource> findLangResources(String lang);

    void saveResources(List<DynamicResource> created, List<DynamicResource> updated,
                              List<DynamicResource> deleted);

    void refresh();

}
