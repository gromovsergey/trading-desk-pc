package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.creative.CreativeCategoryEditTO;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.session.creative.CreativeCategoryTO;

import java.util.logging.Logger;

public class CreativeCategoryGeneratorFactory {
    private static final Logger logger = Logger.getLogger(CreativeCategoryGeneratorFactory.class.getName());

    private CreativeCategoryService creativeCategoryService;

    public CreativeCategoryGeneratorFactory(CreativeCategoryService creativeCategoryService) {
        this.creativeCategoryService = creativeCategoryService;
    }

    public CreativeCategory findOrCreate(String entityName, CreativeCategoryType type) {
        CreativeCategory result = creativeCategoryService.findByTypeName(type, entityName);
        if (result != null) {
            logger.info("Appropriate entity CreativeCategory{ " + type + " } found. Id: " + result.getId());
            return result;
        }

        return create(entityName, type);
    }

    public CreativeCategory create(String entityName, CreativeCategoryType type) {
        CreativeCategoryTO to = new CreativeCategoryTO();
        to.setName(entityName);

        CreativeCategoryEditTO editTO = creativeCategoryService.getForEdit(type);
        editTO.getCategories().add(to);
        creativeCategoryService.update(editTO);

        CreativeCategory result = creativeCategoryService.findByTypeName(type, entityName);
        logger.info("New entity CreativeCategory{ " + type + " } created. Id: " + result.getId());
        return result;
    }
}
