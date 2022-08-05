package com.foros.action.admin.template.discover;

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

public class ListDiscoverTemplateAction extends BaseActionSupport {

    @EJB
    private TemplateService templateService;

    @EJB
    private UserService userService;

    // model
    private List<TemplateTO> discoverTemplates;

    @ReadOnly
    public String list() {
        this.discoverTemplates = userService.getMyUser().isDeletedObjectsVisible() ? templateService.findAllDiscoverTemplates() : templateService.findAllNonDeletedDiscoverTemplates();
        Collections.sort(discoverTemplates, new Comparator<TemplateTO>() {
            @Override
            public int compare(TemplateTO t1, TemplateTO t2) {
                return StringUtil.lexicalCompare(t1.getLocalizedName(),t2.getLocalizedName());
            }
        });
        return SUCCESS;
    }

    public List<TemplateTO> getDiscoverTemplates() {
        return discoverTemplates;
    }

}
