package com.foros.util.comparator;

import com.foros.model.template.TemplateFile;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;

import java.util.Comparator;

public class CreativeTemplateFileComparator implements Comparator<TemplateFile> {
    @Override
    public int compare(TemplateFile o1, TemplateFile o2) {
        String name1 = LocalizableNameUtil.getLocalizedValue(o1.getCreativeSize().getName());
        String name2 = LocalizableNameUtil.getLocalizedValue(o2.getCreativeSize().getName());
        int compareRes = StringUtil.compareToIgnoreCase(name1, name2);
        if (compareRes != 0) {
            return compareRes;
        }

        name1 = o1.getApplicationFormat().getName();
        name2 = o2.getApplicationFormat().getName();
        compareRes = StringUtil.compareToIgnoreCase(name1, name2);
        if (compareRes != 0) {
            return compareRes;
        }
        
        return 0;
    }
}
