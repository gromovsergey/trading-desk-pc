package com.foros.test.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.Template;
import com.foros.session.template.OptionGroupService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class OptionGroupTestFactory extends TestFactory<OptionGroup> {
    @EJB
    private OptionGroupService optionGroupService;

    public void populate(OptionGroup optionGroup) {
        optionGroup.setAvailability(OptionGroup.Availability.ALWAYS_ENABLED);
        optionGroup.setCollapsability(OptionGroup.Collapsability.COLLAPSED_BY_DEFAULT);
        optionGroup.setDefaultLabel(getTestEntityRandomName());
        optionGroup.setDefaultName(getTestEntityRandomName());
        optionGroup.setSortOrder(1);
        optionGroup.setType(OptionGroupType.Advertiser);

        optionGroup.setOptions(new HashSet<Option>());
    }

    @Override
    public OptionGroup create() {
        OptionGroup optionGroup = new OptionGroup();
        populate(optionGroup);
        return optionGroup;
    }

    public OptionGroup create(CreativeSize creativeSize, Template template, OptionGroupType groupType, Option ... options) {
        OptionGroup optionGroup = new OptionGroup();
        populate(optionGroup);

        optionGroup.setCreativeSize(creativeSize);
        optionGroup.setTemplate(template);
        optionGroup.setType(groupType == null ? OptionGroupType.Advertiser : groupType);

        optionGroup.getOptions().clear();
        if (options != null) {
            Collection<Option> optionList = Arrays.asList(options);
            optionGroup.getOptions().addAll(optionList);
        }

        return optionGroup;
    }

    public OptionGroup create(CreativeSize creativeSize) {
        return create(creativeSize, null, null);
    }

    public OptionGroup create(Template template) {
        return create(null, template, null);
    }

    @Override
    public OptionGroup createPersistent() {
        OptionGroup optionGroup = create();
        optionGroupService.create(optionGroup);
        return optionGroup;
    }

    public OptionGroup createPersistent(CreativeSize creativeSize, Template template, OptionGroupType groupType, Option... options) {
        OptionGroup optionGroup = create(creativeSize, template, groupType, options);
        optionGroupService.create(optionGroup);
        return optionGroup;
    }

    public OptionGroup createPersistent(CreativeSize creativeSize) {
        return createPersistent(creativeSize, null, null);
    }

    public OptionGroup createPersistent(CreativeSize creativeSize, OptionGroupType type) {
        return createPersistent(creativeSize, null, type);
    }

    public OptionGroup createPersistent(Template template) {
        return createPersistent(null, template, null);
    }

    public OptionGroup createPersistent(Template template, OptionGroupType type) {
        return createPersistent(null, template, type);
    }

    @Override
    public void persist(OptionGroup optionGroup) {
        optionGroupService.create(optionGroup);
    }

    @Override
    public void update(OptionGroup optionGroup) {
        optionGroupService.update(optionGroup);
    }

    public void remove(OptionGroup optionGroup) {
        optionGroupService.remove(optionGroup.getId());
    }
}
