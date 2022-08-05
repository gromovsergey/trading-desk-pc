package com.foros.action.admin.creativeCategories;

import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.DynamicResource;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.session.creative.CreativeCategoryEditTO;
import com.foros.session.creative.CreativeCategoryTO;
import com.foros.util.StringUtil;
import com.foros.util.csv.CsvReader;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveCreativeCategoryAction extends SupportCreativeCategoryAction implements BreadcrumbsSupport, Invalidable {

    @Autowired
    private CreativeCategoryCsvReader.Factory creativeCategoryCsvReaderFactory;

    public String save() {
        CreativeCategoryEditTO editTO = getEditTO();
        editTO.setType(getType());
        if (editTO.getType() == CreativeCategoryType.TAG) {
            List<CreativeCategoryTO> categories = editTO.getCategories();
            for (CreativeCategoryTO cc : categories) {
                cc.setName(cc.getName().toLowerCase());
            }
        }
        creativeCategoryService.update(editTO);

        for (CreativeCategoryTO cct : editTO.getCategories()) {
            if (cct.getLocalisationMap() != null && !cct.getLocalisationMap().isEmpty()) {
                CreativeCategory creativeCategory = creativeCategoryService.findByTypeName(editTO.getType(), cct.getName());
                if (creativeCategory != null) {
                    updateDynamicResources(creativeCategory.getId(), cct.getLocalisationMap());
                }
            }
        }

        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CreativeCategoriesBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }

    private void updateDynamicResources(Long id, Map<String, String> locMap) {
        String finalKey = "CreativeCategory." + id;
        List<DynamicResource> created = new ArrayList<>();
        List<DynamicResource> updated = new ArrayList<>();
        List<DynamicResource> deleted = new ArrayList<>();

        for (String locale : locMap.keySet()) {
            String mess = locMap.get(locale);
            DynamicResource res = new DynamicResource();
            res.setKey(finalKey);
            res.setLang(locale);
            res.setValue(mess);
            if (StringUtil.isPropertyEmpty(res.getValue())) {
                if (StringUtil.isPropertyNotEmpty(res.getKey())) {
                    deleted.add(res);
                }
            } else {
                if (StringUtil.isPropertyNotEmpty(res.getKey())) {
                    updated.add(res);
                } else {
                    res.setKey(finalKey);
                    created.add(res);
                }
            }
        }
        dynamicResourcesService.saveResources(created, updated, deleted);
    }

    @Override
    public void validate() {
        super.validate();

        if (getType() == CreativeCategoryType.TAG) {
            List<CreativeCategoryTO> creativeCategories = new LinkedList<>();
            String[] names = StringUtil.splitAndTrim(categoriesText);
            for (String name : names) {
                CreativeCategoryTO creativeCategoryTO = new CreativeCategoryTO();
                creativeCategoryTO.setName(name);
                creativeCategories.add(creativeCategoryTO);
            }
            getEditTO().setCategories(creativeCategories);
        } else {
            try (CsvReader csvReader = new CsvReader(new StringReader(categoriesText), BulkFormat.CSV.getDelimiter())) {
                CreativeCategoryCsvReader creativeCategoryCsvReader = creativeCategoryCsvReaderFactory.newInstance(csvReader);
                List<CreativeCategoryTO> creativeCategories = creativeCategoryCsvReader.parse();
                if (creativeCategoryCsvReader.getCurrentStatus().hasErrors()) {
                    for (ConstraintViolation cv : creativeCategoryCsvReader.getCurrentStatus().getErrors()) {
                        addActionError(cv.getMessage());
                    }
                } else {
                    getEditTO().setCategories(creativeCategories);
                }
            } catch (IOException ex) {
                throw ConstraintViolationException.newBuilder("CreativeCategory.errors.cannot.parse.csv").build();
            }
        }
    }

    @Override
    public void invalid() throws Exception {
        setEditTO(creativeCategoryService.getForEdit(getType()));
    }
}
