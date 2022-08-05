package com.foros.session.creative;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeCategoryTypeEntity;
import com.foros.model.creative.RTBConnector;
import com.foros.session.bulk.Result;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CreativeCategoryService {

    void refreshAll();

    CreativeCategory findById(Long id);

    List<CreativeCategory> findByIds(Collection<Long> ids);

    List<CreativeCategory> findByType(CreativeCategoryType type, boolean showHold);

    CreativeCategory findByTypeName(CreativeCategoryType type, String name);

    List<CreativeCategory> findAll(boolean showHold);

    CreativeCategoryTypeEntity findCreativeCategoryType(CreativeCategoryType type);

    void update(CreativeCategoryEditTO editTO);

    void deleteCategory(Long categoryId);

    CreativeCategoryEditTO getForEdit(CreativeCategoryType type);

    void removeUnlinkedVisualCategories(Long templateId);

    List<RTBConnector> getRTBConnectors();

    Result<CreativeCategory> get(CreativeCategorySelector selector);
}
