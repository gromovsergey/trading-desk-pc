package com.foros.test.factory;

import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ApplicationFormatTestFactory extends TestFactory<ApplicationFormat> {
    @EJB
    private ApplicationFormatService applicationFormatService;

    public void populate(ApplicationFormat format) {
        format.setName(getTestEntityRandomName());
    }

    @Override
    public ApplicationFormat create() {
        ApplicationFormat format = new ApplicationFormat();
        populate(format);
        return format;
    }

    @Override
    public void persist(ApplicationFormat format) {
        applicationFormatService.create(format);
    }

    public void update(ApplicationFormat format) {
        applicationFormatService.update(format);
    }

    @Override
    public ApplicationFormat createPersistent() {
        ApplicationFormat format = create();
        persist(format);
        return format;
    }

    public ApplicationFormat findByName(String name) {
        return applicationFormatService.findByName(name);
    }
}
