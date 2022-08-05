package com.foros.session.template;

import com.foros.model.template.OptionGroup;
import com.foros.service.ByIdLocatorService;

import java.util.Set;
import javax.ejb.Local;

@Local
public interface OptionGroupService extends ByIdLocatorService<OptionGroup> {
    void create(OptionGroup optionGroup);

    OptionGroup update(OptionGroup optionGroup);

    void remove(Long id);

    Set<OptionGroup> copyGroups(Set<OptionGroup> optionGroups);
}
