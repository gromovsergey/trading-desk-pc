package com.foros.session.creative;

import com.foros.model.creative.Creative;

import java.io.Serializable;
import java.util.List;

public class CreativeCsvReaderResult implements Serializable {
    private List<Creative> creatives;
    private List<String> optionHeaderNames;
    private SizeTemplateBasedValueResolver columnTypeResolver;

    public CreativeCsvReaderResult(List<Creative> creatives, List<String> optionHeaderNames, SizeTemplateBasedValueResolver columnTypeResolver) {
        this.creatives = creatives;
        this.optionHeaderNames = optionHeaderNames;
        this.columnTypeResolver = columnTypeResolver;
    }

    public List<Creative> getCreatives() {
        return creatives;
    }

    public List<String> getOptionHeaderNames() {
        return optionHeaderNames;
    }

    public SizeTemplateBasedValueResolver getColumnTypeResolver() {
        return columnTypeResolver;
    }
}
