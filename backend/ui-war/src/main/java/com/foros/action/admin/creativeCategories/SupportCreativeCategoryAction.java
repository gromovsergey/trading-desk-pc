package com.foros.action.admin.creativeCategories;

import com.foros.action.BaseActionSupport;
import com.foros.action.bulk.CsvRow;
import com.foros.model.admin.DynamicResource;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.formatter.IdValueFormatter;
import com.foros.reporting.serializer.formatter.StringValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.session.admin.DynamicResourcesService;
import com.foros.session.creative.CreativeCategoryEditTO;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.session.creative.CreativeCategoryTO;
import com.foros.util.csv.CsvWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;

public abstract class SupportCreativeCategoryAction extends BaseActionSupport
        implements ModelDriven<CreativeCategoryEditTO> {

    private CreativeCategoryEditTO editTO = new CreativeCategoryEditTO();

    protected String categoriesText;

    // parameter
    private CreativeCategoryType type;

    @EJB
    protected CreativeCategoryService creativeCategoryService;

    @EJB
    protected DynamicResourcesService dynamicResourcesService;

    @Autowired
    private CreativeCategoryFieldCsvHelper creativeCategoryFieldCsvHelper;

    public CreativeCategoryEditTO getEditTO() {
        return editTO;
    }

    public void setEditTO(CreativeCategoryEditTO editTO) {
        this.editTO = editTO;
        categoriesText = null;
    }

    public String getCategoriesText() throws IOException {
        if (!StringUtils.isEmpty(categoriesText)) {
            return categoriesText;
        }

        if (type == CreativeCategoryType.TAG) {
            return joinTags(editTO.getCategories());
        }

        try (StringWriter sw = new StringWriter()) {
            CsvSerializer serializer = new CsvSerializer(new CsvWriter(sw, ','), getLocale(), Integer.MAX_VALUE);
            serializer.registry(newRegistry(), RowTypes.data());
            serializer.setMetaData(creativeCategoryFieldCsvHelper.getMetaData());
            for (CreativeCategoryTO creativeCategoryTO : editTO.getCategories()) {
                List<DynamicResource> resources = dynamicResourcesService.findResources("CreativeCategory." + creativeCategoryTO.getId());
                Map<String, String> localisedMap = new HashMap<>();
                for (DynamicResource resource : resources) {
                    localisedMap.put(resource.getLang(), resource.getValue());
                }
                creativeCategoryTO.setLocalisationMap(localisedMap);

                CsvRow row = new CsvRow(creativeCategoryFieldCsvHelper.getAllColumns().size());
                row.set(creativeCategoryFieldCsvHelper.getName(), creativeCategoryTO.getName());
                row.set(creativeCategoryFieldCsvHelper.getId(), creativeCategoryTO.getId());
                List<String> rtbCategories = creativeCategoryTO.getRtbCategories();
                List<CreativeCategoryFieldCsv> rtbKeys = creativeCategoryFieldCsvHelper.getRtbKeys();
                for (int i = 0; i < rtbKeys.size(); i++) {
                    CreativeCategoryFieldCsv fieldCsv = rtbKeys.get(i);
                    row.set(fieldCsv, rtbCategories.get(i));
                }

                row.set(creativeCategoryFieldCsvHelper.getLocalisation(), creativeCategoryTO.getLocalisationMapStr());
                serializer.row(row);
            }
            serializer.close();
            return sw.toString();
        }
    }

    public void setCategoriesText(String categoriesText) throws IOException {
        this.categoriesText = categoriesText;
    }

    private String joinTags(List<CreativeCategoryTO> categories) {
        StringBuilder sb = new StringBuilder();
        if (categories != null) {
            for (CreativeCategoryTO category : categories) {
                sb.append(category.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    public boolean isTagCategory() {
        return type == CreativeCategoryType.TAG;
    }

    public String getCreativeCategoryEditDescription() {
        return getText("CreativeCategory.edit.format.description", new String[] { getKey() });

    }

    private String getKey() {
        StringBuilder key = new StringBuilder();
        for (CreativeCategoryFieldCsv creativeCategoryFieldCsv : creativeCategoryFieldCsvHelper.getRtbKeys()) {
            key.append("\"").append(creativeCategoryFieldCsv.getName()).append("\",");
        }
        return key.toString();
    }

    public CreativeCategoryType getType() {
        return type;
    }

    public void setType(CreativeCategoryType type) {
        this.type = type;
    }

    @Override
    public CreativeCategoryEditTO getModel() {
        return editTO;
    }

    private ValueFormatterRegistry newRegistry() {
        ValueFormatterRegistryImpl custom = ValueFormatterRegistries.registry();
        custom.column(creativeCategoryFieldCsvHelper.getId(), new IdValueFormatter());
        for (CreativeCategoryFieldCsv fieldCsv : creativeCategoryFieldCsvHelper.getRtbKeys()) {
            custom.column(fieldCsv, new StringValueFormatter());
        }
        custom.column(creativeCategoryFieldCsvHelper.getLocalisation(), new StringValueFormatter());
        return ValueFormatterRegistries.bulkDefaultAnd(custom);
    }
}
