package com.foros.test.factory;

import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.creative.SizeType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.SizeTypeService;
import com.foros.util.RandomUtil;

import java.util.Arrays;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class CreativeSizeTestFactory extends TestFactory<CreativeSize> {
    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private OptionGroupTestFactory optionGroupTF;

    @EJB
    private OptionTestFactory optionTF;

    @EJB
    private SizeTypeTestFactory sizeTypeTF;

    @EJB
    private SizeTypeService sizeTypeService;

    public void populate(CreativeSize size, boolean needPopulateName) {
        if (needPopulateName) {
            size.setDefaultName(getTestEntityRandomName().replaceAll("-", "_"));
        }
        size.setHeight(100L); // do not use random values, as they should be in [1 .. 9,999]
        size.setWidth(100L);
        size.setMaxHeight(size.getHeight() + 1);
        size.setMaxWidth(size.getWidth() + 1);
        size.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.DOWN_LEFT, CreativeSizeExpansion.DOWN_RIGHT, CreativeSizeExpansion.UP_LEFT, CreativeSizeExpansion.UP_RIGHT)));
        size.setProtocolName(getTestEntityRandomName().replaceAll("-", "_"));
        size.setStatus(Status.ACTIVE);

        size.setSizeType(sizeTypeTF.createPersistent());
    }

    @Override
    public CreativeSize create() {
        CreativeSize size = new CreativeSize();
        populate(size, true);
        return size;
    }

    public CreativeSize createText() {
        CreativeSize size = new CreativeSize();
        size.setDefaultName(CreativeSize.TEXT_SIZE);
        populate(size, false);
        return size;
    }

    @Override
    public void persist(CreativeSize size) {
        creativeSizeService.create(size);
        entityManager.flush();
    }

    public void update(CreativeSize size) {
        creativeSizeService.update(size);
        entityManager.flush();
    }

    @Override
    public CreativeSize createPersistent() {
        CreativeSize size = create();
        persist(size);
        OptionGroup optionGroup = optionGroupTF.createPersistent(size, OptionGroupType.Publisher);
        size.getOptionGroups().add(optionGroup);
        optionTF.createPersistent(optionGroup, OptionType.STRING);
        return size;
    }

    public CreativeSize createNotExpandablePersistent() {
        CreativeSize size = create();
        size.setMaxWidth(size.getWidth());
        size.setMaxHeight(size.getHeight());
        size.setExpansions(new HashSet<CreativeSizeExpansion>());
        persist(size);
        return size;
    }

    public CreativeSize findText() {
        CreativeSize size = findAny(CreativeSize.class, new QueryParam("defaultName", CreativeSize.TEXT_SIZE));

        // may be only one text creative size per table
        // so create it when not found
        if (size == null) {
            size = createText();
            persist(size);
        }

        return size;
    }
}
