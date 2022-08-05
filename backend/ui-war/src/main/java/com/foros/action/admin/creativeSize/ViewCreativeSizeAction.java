package com.foros.action.admin.creativeSize;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.OptionComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ViewCreativeSizeAction extends BaseActionSupport implements ModelDriven<CreativeSize>, BreadcrumbsSupport {

    @EJB
    private CreativeSizeService service;

    private Long id;

    private CreativeSize entity;

    @Override
    public CreativeSize getModel() {
        return entity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List getSortedOptions() {
        List<Option> options = new ArrayList<Option>(entity.getAllOptions());
        Collections.sort(options, new OptionComparator());
        return options;
    }

    @ReadOnly
    public String view() {
        entity = service.view(id);
        return SUCCESS;
    }
    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CreativeSizesBreadcrumbsElement()).add(new CreativeSizeBreadcrumbsElement(entity));
    }
}
