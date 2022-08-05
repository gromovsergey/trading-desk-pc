package com.foros.test.factory;

import com.foros.model.template.CreativeTemplate;
import com.foros.session.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TextCreativeTemplateTestFactory  extends TestFactory<CreativeTemplate> {
    @Autowired
    private TemplateService templateService;

    @Override
    public CreativeTemplate create() {
        CreativeTemplate template = findAny(CreativeTemplate.class, new QueryParam("defaultName", CreativeTemplate.TEXT_TEMPLATE));

        // may be only one text creative template per table
        // so create it when not found
        if (template == null) {
            template = new CreativeTemplate();
            template.setDefaultName(CreativeTemplate.TEXT_TEMPLATE);
        }

        return template;
    }

    @Override
    public void persist(CreativeTemplate template) {
        templateService.create(template);
    }

    public void update(CreativeTemplate template) {
        templateService.update(template);
    }

    @Override
    public CreativeTemplate createPersistent() {
        CreativeTemplate template = create();
        persist(template);
        return template;
    }
}
