package com.foros.action.admin.template.discover;

import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.TemplateFile;
import com.foros.util.CollectionUtils;
import java.util.Comparator;
import java.util.Map;

class FilesComparator implements Comparator<TemplateFile> {

    private static final Map<String, Integer> FORMATS = CollectionUtils
            .map(ApplicationFormat.DISCOVER_TAG_FORMAT, 0)
            .map(ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT, 1)
            .map(ApplicationFormat.PREVIEW_FORMAT, 2)
            .build();

    @Override
    public int compare(TemplateFile o1, TemplateFile o2) {
        Integer pos1 = FORMATS.get(o1.getApplicationFormat().getName());
        Integer pos2 = FORMATS.get(o2.getApplicationFormat().getName());
        return pos1.compareTo(pos2);
    }
    
}
