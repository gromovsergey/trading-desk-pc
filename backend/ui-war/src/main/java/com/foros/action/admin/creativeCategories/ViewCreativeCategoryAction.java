package com.foros.action.admin.creativeCategories;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.RTBConnector;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewCreativeCategoryAction extends BaseActionSupport {
    @EJB
    private CreativeCategoryService creativeCategoryService;

    @Autowired
    private CreativeCategoryFieldCsvHelper creativeCategoryFieldCsvHelper;

    private List<CreativeCategory> tags;
    private List<CreativeCategory> visuals;
    private List<CreativeCategory> contents;
    private List<RTBConnector> rtbConnectors;

    @ReadOnly
    @Restrict(restriction = "CreativeCategory.view")
    public String list() {
        tags = creativeCategoryService.findByType(CreativeCategoryType.TAG, false);
        visuals = creativeCategoryService.findByType(CreativeCategoryType.VISUAL, true);
        contents = creativeCategoryService.findByType(CreativeCategoryType.CONTENT, true);

        return SUCCESS;
    }

    public Collection<String> getRows(String categoriesText) {
        String[] rows = categoriesText.split("\r\n");
        Collection<String> rowsList = new LinkedList<String>(Arrays.asList(rows));
        CollectionUtils.filter(rowsList, new Filter<String>() {
            @Override
            public boolean accept(String element) {
                return StringUtil.isPropertyNotEmpty(element);
            }
        });
        return rowsList;
    }

    public List<CreativeCategory> getTags() {
        return tags;
    }

    public List<CreativeCategory> getVisuals() {
        return visuals;
    }

    public List<CreativeCategory> getContents() {
        return contents;
    }

    public List<CreativeCategoryFieldCsv> getRtbKeys() {
        return creativeCategoryFieldCsvHelper.getRtbKeys();
    }

    public List<RTBConnector> getConnectors() {
        if (rtbConnectors != null) {
            return rtbConnectors;
        }

        rtbConnectors = new ArrayList<>(creativeCategoryService.getRTBConnectors());
        return rtbConnectors;
    }
}
