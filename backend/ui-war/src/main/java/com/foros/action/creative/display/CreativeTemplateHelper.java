package com.foros.action.creative.display;

import com.foros.model.template.Template;
import com.foros.session.EntityTO;
import com.foros.session.template.TemplateService;
import com.foros.util.EntityUtils;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.comparator.IdNameComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreativeTemplateHelper {
    public static List<EntityTO> getTemplatesForSize(TemplateService service, Long accountTypeId , Long sizeId) {
        if (accountTypeId == null || sizeId == null) {
            return new ArrayList<EntityTO>();
        }

        List<Template> templates = service.findJsHTMLTemplatesBySize(accountTypeId, sizeId);
        
        List<EntityTO> templatesTO = new ArrayList<EntityTO>();
        
        for (Template template : templates) {
            String name = LocalizableNameUtil.getLocalizedValue(template.getName());
            EntityTO entityTO = new EntityTO(template.getId(), name, template.getStatus().getLetter());
            templatesTO.add(entityTO);
        }

        EntityUtils.applyStatusRules(templatesTO, null);

        Collections.sort(templatesTO, new IdNameComparator());

        return templatesTO;
    }
}
