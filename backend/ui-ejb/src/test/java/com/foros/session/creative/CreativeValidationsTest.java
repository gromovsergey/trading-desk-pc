package com.foros.session.creative;

import com.foros.AbstractValidationsTest;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.TemplateFileType;
import com.foros.session.template.OptionGroupService;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;

import group.Db;
import group.Validation;
import javax.ejb.EJB;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({Db.class, Validation.class})
public class CreativeValidationsTest extends AbstractValidationsTest {

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private CreativeSizeTestFactory sizeTF;

    @Autowired
    private CreativeTemplateTestFactory templateTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @EJB
    private OptionGroupService optionGroupService;

    @Autowired
    private OptionTestFactory optionTF;

    @Test
    public void testFailFileURLPrepersistCreativeOptions() throws Exception {
        CreativeSize size = sizeTF.create();
        sizeTF.persist(size);
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);

        Option urlOption = optionTF.createPersistent(optionGroup, OptionType.FILE_URL);
        Option requiredOption = optionTF.create(optionGroup, OptionType.STRING);
        requiredOption.setRequired(true);
        optionTF.persist(requiredOption);

        CreativeTemplate template = templateTF.createPersistent();

        Creative creative = displayCreativeTF.create(template, size);
        templateTF.createPersistentTemplateFile(template, TemplateFileType.TEXT, "html", size, "/var/file");

        creative.getOptions().add(createOptionValue(creative, urlOption, "http://wrong URL"));
        creative.getOptions().add(createOptionValue(creative, requiredOption, ""));
        validate("Creative.create", creative);
        assertHasViolation("options[" + urlOption.getId() + "].value");
        assertHasViolation("options[" + requiredOption.getId() + "].value");

        CreativeOptGroupState state = new CreativeOptGroupState();
        state.setId(new CreativeOptGroupStatePK(optionGroup.getId(), 0));
        state.setEnabled(false);
        creative.getGroupStates().add(state);
        validate("Creative.create", creative);
        assertHasNoViolation("options[" + urlOption.getId() + "].value");
        assertHasNoViolation("options[" + requiredOption.getId() + "].value");
    }

    @Test
    public void testEmptySizeTemplate() throws Exception {
        Creative creative = displayCreativeTF.create();
        creative.setSize(null);
        creative.setTemplate(null);
        validate("Creative.create", creative);
        assertHasViolation("size");
        assertHasViolation("template");
    }

    @Test
    public void testInvalidTemplate() throws Exception {
        Creative creative = displayCreativeTF.create();
        creative.setSize(sizeTF.createPersistent());
        creative.setTemplate(templateTF.createPersistent());
        validate("Creative.create", creative);
        assertHasViolation("template");
    }

    private CreativeOptionValue createOptionValue(Creative creative, Option option, String value) {
        CreativeOptionValue cv1 = new CreativeOptionValue();
        cv1.setCreative(creative);
        cv1.setOption(option);
        cv1.setValue(value);
        return cv1;
    }
}
