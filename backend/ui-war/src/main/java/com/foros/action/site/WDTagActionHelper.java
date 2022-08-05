package com.foros.action.site;

import com.foros.action.IdNameBean;
import com.foros.action.IdNameBeanComparator;
import com.foros.model.feed.Feed;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class WDTagActionHelper {

    static Set<Feed> convertUrls(String[] urls) {
        LinkedHashSet<Feed> result = new LinkedHashSet<Feed>(urls.length);
        for (String url : urls) {
            Feed feed = new Feed();
            feed.setUrl(url);
            result.add(feed);
        }
        return result;
    }

    static List<IdNameBean> prepareEnumValues(Option option) {
        List<IdNameBean> values = new ArrayList<IdNameBean>(option.getValues().size());

        for (OptionEnumValue value : option.getValues()) {
            values.add(new IdNameBean(value.getId().toString(), value.getName()));
        }

        Collections.sort(values, new IdNameBeanComparator());

        return values;
    }

    public static List<String> prepareFileTypes(Option option) {
        List<String> fileTypes = new ArrayList<String>();

        for (OptionFileType optionFileType : option.getFileTypes()) {
            fileTypes.add(optionFileType.getFileType());
        }

        return fileTypes;
    }
}
