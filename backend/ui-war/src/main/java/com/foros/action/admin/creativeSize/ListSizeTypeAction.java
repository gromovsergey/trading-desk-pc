package com.foros.action.admin.creativeSize;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.SizeType;
import com.foros.session.creative.SizeTypeService;

import java.util.List;
import javax.ejb.EJB;

public class ListSizeTypeAction extends BaseActionSupport {

    @EJB
    private SizeTypeService sizeTypeService;

    private List<SizeType> types;

    @ReadOnly
    public String list() {
        types = sizeTypeService.findAll();
        return SUCCESS;
    }

    public List<SizeType> getTypes() {
        return types;
    }
}
