package com.foros.util.comparator;

import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.TemplateFile;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DiscoverTemplateFileComparator implements Comparator<TemplateFile> {
    private static final List<String> APPLICATION_FORMATS = Arrays.asList(
            ApplicationFormat.DISCOVER_TAG_FORMAT,
            ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT,
            ApplicationFormat.PREVIEW_FORMAT);

    @Override
    public int compare(TemplateFile o1, TemplateFile o2) {
        String appFormat1 = o1.getApplicationFormat().getName();
        String appFormat2 = o2.getApplicationFormat().getName();

        return APPLICATION_FORMATS.indexOf(appFormat1) - APPLICATION_FORMATS.indexOf(appFormat2);
    }
}
