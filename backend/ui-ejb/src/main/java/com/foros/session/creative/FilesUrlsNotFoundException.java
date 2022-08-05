package com.foros.session.creative;

import com.foros.model.template.OptionType;
import com.foros.session.fileman.FilesNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class FilesUrlsNotFoundException extends FilesNotFoundException {
    private Map<String, String> urls;
    private Map<String, OptionType> urlOptionTypes;

    public FilesUrlsNotFoundException() {
        urls = new HashMap<String, String>();
        urlOptionTypes = new HashMap<String, OptionType>();
    }

    public void addUrl(String key, String url, OptionType optionType) {
        getUrls().put(key, url);
        getUrlOptionTypes().put(key, optionType);
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public Map<String, OptionType> getUrlOptionTypes() {
        return urlOptionTypes;
    }
}
