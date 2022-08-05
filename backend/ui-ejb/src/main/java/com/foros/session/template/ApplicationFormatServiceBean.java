package com.foros.session.template;

import com.foros.model.template.ApplicationFormat;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessServiceBean;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless(name = "ApplicationFormatService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class ApplicationFormatServiceBean extends BusinessServiceBean<ApplicationFormat> implements ApplicationFormatService {

    public ApplicationFormatServiceBean() {
        super(ApplicationFormat.class);
    }

    @Override
    public ApplicationFormat findByName(String name) {
        return (ApplicationFormat)em.createNamedQuery("ApplicationFormat.findByName").setParameter("name", name).
                getSingleResult();
    }

    @Override
    @Restrict(restriction="ApplicationFormat.create")
    @Validate(validation = "ApplicationFormat.create", parameters = "#entity")
    public void create(ApplicationFormat entity) {
        super.create(entity);
    }

    @Override
    @Restrict(restriction="ApplicationFormat.update")
    @Validate(validation = "ApplicationFormat.update", parameters = "#entity")
    public ApplicationFormat update(ApplicationFormat entity) {
        return super.update(entity);
    }

    @Override
    public ApplicationFormat findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction="ApplicationFormat.view")
    public ApplicationFormat view(Long id) {
        return findById(id);
    }

    @Override
    @Restrict(restriction="ApplicationFormat.view")
    public List<ApplicationFormat> findAll() {
        return super.findAll();
    }

    @Override
    public List<ApplicationFormat> findAllUnrestricted() {
        return super.findAll();
    }
}
