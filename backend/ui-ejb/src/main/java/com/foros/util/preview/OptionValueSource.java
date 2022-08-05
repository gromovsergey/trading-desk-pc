package com.foros.util.preview;

import com.foros.model.template.OptionValue;

public interface OptionValueSource {
    OptionValueSource NULL = new OptionValueSource() {
        @Override
        public OptionValue get(Long optionId) {
            return null;
        }

        @Override
        public String getImagesPath() {
            return null;
        }
    };

    OptionValue get(Long optionId);
    String getImagesPath();
}
