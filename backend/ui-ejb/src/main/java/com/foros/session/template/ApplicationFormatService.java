package com.foros.session.template;

import com.foros.model.template.ApplicationFormat;
import com.foros.service.ByIdLocatorService;

import java.util.List;
import javax.ejb.Local;

@Local
public interface ApplicationFormatService extends ByIdLocatorService<ApplicationFormat> {
    void create(ApplicationFormat entity);

    ApplicationFormat update(ApplicationFormat entity);

    void refresh(Long id);

    ApplicationFormat findById(Long id);

    List<ApplicationFormat> findAll();

    List<ApplicationFormat> findAllUnrestricted();

    ApplicationFormat findByName(String name);
}
