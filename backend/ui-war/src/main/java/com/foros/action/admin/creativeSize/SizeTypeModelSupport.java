package com.foros.action.admin.creativeSize;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.SizeType;
import com.foros.session.creative.SizeTypeService;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public abstract class SizeTypeModelSupport extends BaseActionSupport implements ModelDriven<SizeType>, BreadcrumbsSupport {
    @EJB
    protected SizeTypeService sizeTypeService;
    protected SizeType sizeType = new SizeType();

    @Override
    public SizeType getModel() {
        return sizeType;
    }
}
