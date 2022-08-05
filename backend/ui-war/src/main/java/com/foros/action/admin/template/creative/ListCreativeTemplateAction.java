package com.foros.action.admin.template.creative;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.template.TemplateTO;
import com.foros.session.security.UserService;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListCreativeTemplateAction extends BaseActionSupport {

    @EJB
    private TemplateService templateService;

    @EJB
    private UserService userService;

    // model
    private List<TemplateTO> creativeTemplates;

    @ReadOnly
    public String list() {
        creativeTemplates = userService.getMyUser().isDeletedObjectsVisible() ? templateService.findAllCreativeTemplates() : templateService.findAllNonDeletedCreativeTemplates();
        Collections.sort(creativeTemplates, new Comparator<TemplateTO>() {
            @Override
            public int compare(TemplateTO o1, TemplateTO o2) {
                return StringUtil.lexicalCompare(o1.getLocalizedName(),o2.getLocalizedName());
            }
        });

        return SUCCESS;
    }

    public List<TemplateTO> getCreativeTemplates() {
        return creativeTemplates;
    }

}
