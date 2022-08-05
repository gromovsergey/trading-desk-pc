package com.foros.action.action;

import com.foros.action.BaseActionSupport;
import com.foros.model.action.ConversionCategory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BaseActionAction extends BaseActionSupport {
    private static final List<ConversionCategory> conversionCategories = generateConversionCategories();

    public List<ConversionCategory> getConversionCategories() {
        return conversionCategories;
    }

    private static List<ConversionCategory> generateConversionCategories() {
        return Collections.unmodifiableList(Arrays.asList(ConversionCategory.sorted()));
    }
}
