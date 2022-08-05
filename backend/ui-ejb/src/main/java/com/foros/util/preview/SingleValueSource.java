package com.foros.util.preview;

import com.foros.model.template.OptionValue;

public class SingleValueSource implements OptionValueSource {
    private OptionValue optionValue;
    private String imagesPath;

    public SingleValueSource(OptionValue optionValue, String imagesPath) {
        this.optionValue = optionValue;
        this.imagesPath = imagesPath;
    }

    public SingleValueSource(OptionValue optionValue) {
        this(optionValue, null);
    }

    @Override
    public OptionValue get(Long optionId) {
        return optionId.equals(optionValue.getOptionId()) ? optionValue : null;
    }

    @Override
    public String getImagesPath() {
        return imagesPath;
    }
}
