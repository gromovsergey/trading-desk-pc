package com.foros.action.xml.options;

import com.foros.action.creative.display.CreativeTemplateHelper;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.IdNameEntityConverter;
import com.foros.model.IdNameEntity;
import com.foros.session.template.TemplateService;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;

public class CreativeTemplateXmlAction extends AbstractOptionsAction<IdNameEntity> {
    @EJB
    private TemplateService templateService;

    private Long sizeId;
    private Long accountTypeId;

    public CreativeTemplateXmlAction() {
        super(new IdNameEntityConverter(false));
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    @Override
    protected Collection<? extends IdNameEntity> getOptions() throws ProcessException {
        if (sizeId != null) {
            return CreativeTemplateHelper.getTemplatesForSize(templateService, accountTypeId, sizeId);
        }
        
        return new ArrayList<IdNameEntity>();
    }
}
