package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.CreativeCategoryService;
import com.foros.rs.client.service.CreativeService;
import com.foros.rs.client.service.CreativeSizeService;
import com.foros.rs.client.service.CreativeTemplateService;


public interface ForosCreativeService {
    CreativeService getCreativeService();

    CreativeSizeService getSizeService();

    CreativeSizeService getAdminSizeService();

    CreativeTemplateService getTemplateService();

    CreativeTemplateService getAdminTemplateService();

    CreativeCategoryService getCreativeCategoryService();

    CreativeCategoryService getAdminCreativeCategoryService();
}
