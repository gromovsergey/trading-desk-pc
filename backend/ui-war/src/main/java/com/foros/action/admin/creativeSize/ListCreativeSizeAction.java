package com.foros.action.admin.creativeSize;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.session.security.UserService;
import com.foros.util.comparator.LocalizableTOComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

public class ListCreativeSizeAction extends BaseActionSupport {

    @EJB
    private CreativeSizeService service;

    @EJB
    private UserService userService;

    private List<CreativeSizeTO> entities;

    @ReadOnly
    public String list() {
        boolean withDeleted = userService.getMyUser().isDeletedObjectsVisible();
        entities = new ArrayList<>(service.findAll(withDeleted));
        Collections.sort(entities, LocalizableTOComparator.INSTANCE);
        return SUCCESS;
    }

    public List<CreativeSizeTO> getEntities() {
        return entities;
    }
}
