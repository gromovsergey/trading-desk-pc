package com.foros.birt.services.birt;

import com.foros.birt.utils.SearchTermsNormalization;

import org.springframework.stereotype.Component;

@Component
public class ScriptingContext {

    public String getNormalizedValue(String country, String filter) {
        return SearchTermsNormalization.getNormalizedValue(country, filter);
    }
}
