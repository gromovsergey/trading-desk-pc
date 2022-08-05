package com.foros.session.action;

import com.foros.model.DisplayStatus;
import com.foros.model.action.Action;
import com.foros.model.action.ConversionCategory;


public class ActionTO {
    private Long id;
    private String name;
    private ConversionCategory conversionCategory;
    private DisplayStatus displayStatus;
    private String url;
    private Long conversions;

    public ActionTO(Long id, String name, Integer conversionCategoryId, Long displayStatusId, String url, Long conversions) {
        this.id = id;
        this.name = name;
        this.conversionCategory = ConversionCategory.valueOf(conversionCategoryId);
        this.displayStatus = Action.displayStatusMap.get(displayStatusId);
        this.url = url;
        this.conversions = conversions;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConversionCategory getConversionCategory() {
        return conversionCategory;
    }

    public void setConversionCategory(ConversionCategory conversionCategory) {
        this.conversionCategory = conversionCategory;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getConversions() {
        return this.conversions;
    }

    public void setConversions(Long conversions) {
        this.conversions = conversions;
    }
}
