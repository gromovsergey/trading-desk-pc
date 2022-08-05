package com.foros.session.template;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.*;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;

import group.Db;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class OptionIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    protected OptionGroupService optionGroupService;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private DisplayCreativeTestFactory creativeTF;
    @Autowired
    private OptionTestFactory optionTF;
    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;
    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;
    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Test
    public void testHibernateUpdate() {
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        //create enum option
        Option option = optionTF.createPersistent(optionGroup, OptionType.ENUM);
        String enumOptionValue = findNonDefaultValue(option);

        Option to = new Option(option.getId());
        template.getAllOptions().add(to);
        template.setDefaultName("NewName");

        templateService.update(template);
        CreativeSize size = creativeSizeTF.createPersistent();

        //create creative
        Creative creative = creativeTF.create(template, size);
        CreativeOptionValue cov = new CreativeOptionValue();
        cov.setCreative(creative);
        cov.setOption(option);
        cov.setValue(enumOptionValue);
        creative.getOptions().add(cov);
        creativeTF.persist(creative);
        List resultList = entityManager.createQuery("from CreativeOptionValue where option = :option").setParameter("option", option).getResultList();
        assertEquals(enumOptionValue, ((CreativeOptionValue)resultList.get(0)).getValue());
        String value = "test";

        String ejbqlString = "update versioned CreativeOptionValue set value = :value where option = :option";
        entityManager.createQuery(ejbqlString).setParameter("value", value).setParameter("option", option).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        Creative existing = entityManager.find(Creative.class, creative.getId());
        assertEquals(value, existing.getOptions().iterator().next().getValue());
        //change option enum value
    }

    private String findNonDefaultValue(Option option) {
        for (OptionEnumValue val : option.getValues()) {
            if (!val.getValue().equals(option.getDefaultValue())) {
                return val.getValue();
            }
        }

        throw new RuntimeException("Can't find non default value");
    }
}
