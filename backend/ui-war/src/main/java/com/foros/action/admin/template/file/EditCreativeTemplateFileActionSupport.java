package com.foros.action.admin.template.file;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.template.ApplicationFormatService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

public class EditCreativeTemplateFileActionSupport extends CreativeTemplateFileActionSupport {
    @EJB
    private ApplicationFormatService applicationFormatService;
    @EJB
    private CreativeSizeService creativeSizeService;

    public Collection<CreativeSize> getAvailableSizes() {
        List<CreativeSize> sizes = creativeSizeService.findAllNotDeleted();
        Collections.sort(sizes, new LocalizableNameEntityComparator());
        return sizes;
    }

    public Collection<ApplicationFormat> getAvailableAppFormats() {
        return applicationFormatService.findAllUnrestricted();
    }
}
