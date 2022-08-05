package com.foros.action.admin.creativeCategories;

import com.foros.model.creative.RTBConnector;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.MetaDataImpl;
import com.foros.session.creative.CreativeCategoryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

public class CreativeCategoryFieldCsvHelper {
    @Autowired
    private CreativeCategoryService creativeCategoryService;

    private CreativeCategoryFieldCsv name;
    private CreativeCategoryFieldCsv id;
    private CreativeCategoryFieldCsv localisation;
    private List<CreativeCategoryFieldCsv> rtbKeys;
    private List<CreativeCategoryFieldCsv> allColumns;
    private MetaData<CreativeCategoryFieldCsv> metaData;

    @PostConstruct
    public void init() {

        int count = 0;
        name = new CreativeCategoryFieldCsv(count++, "NAME");
        id = new CreativeCategoryFieldCsv(count++, "ID");
        List<CreativeCategoryFieldCsv> keys = new ArrayList<>();
        for (RTBConnector rtbConnector : creativeCategoryService.getRTBConnectors()) {
            keys.add(new CreativeCategoryFieldCsv(count++, rtbConnector.getName()));
        }
        rtbKeys = Collections.unmodifiableList(keys);

        localisation = new CreativeCategoryFieldCsv(count, "LOCALISATION");

        allColumns = Collections.unmodifiableList(new ArrayList<CreativeCategoryFieldCsv>() {
            {
                add(name);
                add(id);
                addAll(rtbKeys);
                add(localisation);
            }
        });

        metaData = new MetaDataImpl<>(allColumns);
    }

    public List<CreativeCategoryFieldCsv> getAllColumns() {
        return allColumns;
    }

    public CreativeCategoryService getCreativeCategoryService() {
        return creativeCategoryService;
    }

    public CreativeCategoryFieldCsv getName() {
        return name;
    }

    public CreativeCategoryFieldCsv getId() {
        return id;
    }

    public CreativeCategoryFieldCsv getLocalisation() {
        return localisation;
    }

    public List<CreativeCategoryFieldCsv> getRtbKeys() {
        return rtbKeys;
    }

    public MetaData<CreativeCategoryFieldCsv> getMetaData() {
        return metaData;
    }
}
