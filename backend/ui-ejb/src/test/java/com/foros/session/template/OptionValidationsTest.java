package com.foros.session.template;

import static com.foros.config.ConfigParameters.ALLOWED_FILE_TYPES;
import static com.foros.util.RandomUtil.getRandomString;
import com.foros.AbstractValidationsTest;
import com.foros.config.MockConfigService;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.util.RandomUtil;

import group.Db;
import group.Validation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class OptionValidationsTest extends AbstractValidationsTest {
    @Autowired
    protected OptionGroupService optionGroupService;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private DisplayCreativeTestFactory creativeTestFactory ;

    @Autowired
    private MockConfigService configService;

    @Autowired
    public CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    public CreativeSizeTestFactory creativeSizeTF;

    private CreativeTemplate template;

    private CreativeSize size;

    @Override
    @Before
    public void setUp() throws Exception {
        configService.set(ALLOWED_FILE_TYPES, Arrays.asList("bmp", "gif", "jpeg"));
        super.setUp();

        template = creativeTemplateTF.createPersistent();
        size = creativeSizeTF.createPersistent();
    }

    @Test
    public void testDefaultCreate() {
        testDefaultInternal("Option.create", null);
    }

    @Test
    public void testDefaultUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.STRING);
        testDefaultInternal("Option.update", option.getId());
    }

    private void testDefaultInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.STRING);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Default Name
        option.setDefaultName(null);
        validate(validationName, option);
        assertHasViolation("defaultName");

        option.setDefaultName("<Default Value>");
        validate(validationName, option);
        assertHasViolation("defaultName");

        option.setDefaultName(RandomUtil.getRandomString(101));
        validate(validationName, option);
        assertHasViolation("defaultName");

        // Validate Default Tooltip
        option = createValid(optionId, OptionType.STRING);

        option.setDefaultLabel(RandomUtil.getRandomString(1001));
        validate(validationName, option);
        assertHasViolation("defaultLabel");

        // Validate Token
        option = createValid(optionId, OptionType.STRING);

        option.setToken(null);
        validate(validationName, option);
        assertHasViolation("token");

        option.setToken("TokenValue123%");
        validate(validationName, option);
        assertHasViolation("token");

        option.setToken("123TokenValue");
        validate(validationName, option);
        assertHasViolation("token");

        option.setToken("TokenValue12345678901234567890123456789012345678901234567890");
        validate(validationName, option);
        assertHasViolation("token");

        // Validate Default Value
        option = createValid(optionId, OptionType.STRING);

        option.setDefaultValue(getRandomString(2001, RandomUtil.Alphabet.LETTERS));
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testIntegerCreate() {
        testIntegerInternal("Option.create", null);
    }

    @Test
    public void testIntegerUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.INTEGER);
        testIntegerInternal("Option.update", option.getId());
    }

    private void testIntegerInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.INTEGER);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Min Value
        option.setMinValue(-99999999999L);
        validate(validationName, option);
        assertHasViolation("minValue");

        option.setMinValue(99999999999L);
        validate(validationName, option);
        assertHasViolation("minValue");

        option.setMinValue(300L); // greater than max value
        validate(validationName, option);
        assertHasViolation("minValue");

        // Validate Max Value
        option = createValid(optionId, OptionType.INTEGER);

        option.setMaxValue(-99999999999L);
        validate(validationName, option);
        assertHasViolation("maxValue");

        option.setMaxValue(99999999999L);
        validate(validationName, option);
        assertHasViolation("maxValue");

        // Validate Default Value
        option = createValid(optionId, OptionType.INTEGER);

        option.setDefaultValue("abc");
        validate(validationName, option);
        assertHasViolation("defaultValue");

        option.setDefaultValue("99999999999");
        validate(validationName, option);
        assertHasViolation("defaultValue");

        option.setDefaultValue("-300"); // less than min value
        validate(validationName, option);
        assertHasViolation("defaultValue");

        option.setDefaultValue("300"); // greater than max value
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testCreateRequiredOption() {
        creativeTestFactory.createPersistent(template, size);

        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option requiredOption = optionTF.create(optionGroup, OptionType.INTEGER);
        requiredOption.setToken("token");
        requiredOption.setRequired(true);
        requiredOption.setDefaultValue(null);
        validate("Option.create", requiredOption);
        // Required checks are gone: OUI-26501
        assertViolationsCount(0);
    }

    @Test
    public void testEnumCreate() {
        testEnumInternal("Option.create", null);
    }

    @Test
    public void testEnumUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.ENUM);
        testEnumInternal("Option.update", option.getId());
    }

    private void testEnumInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.ENUM);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Values
        option.setValues(new LinkedHashSet<OptionEnumValue>());
        validate(validationName, option);
        assertHasViolation("values");

        // too small
        Set<OptionEnumValue> values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("Name 1", "Value 1", true));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values");

        // empty value
        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("Name 1", "", true));
        values.add(createOptionEnumValue("Name 2", "Value 2", false));
        values.add(createOptionEnumValue("Name 3", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[0].value");

        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("", "Value 1", true));
        values.add(createOptionEnumValue("Name 2", "Value 2", false));
        values.add(createOptionEnumValue("Name 3", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[0].name");

        // too long
        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("Name 1", "Value 1 123456789 123456789 123456789 123456789 123456789", true));
        values.add(createOptionEnumValue("Name 2", "Value 2", false));
        values.add(createOptionEnumValue("Name 3", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[0].value");

        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("Name 1 123456789 123456789 123456789 123456789 123456789", "Value 1", true));
        values.add(createOptionEnumValue("Name 2", "Value 2", false));
        values.add(createOptionEnumValue("Name 3", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[0].name");

        // duplicate
        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue(null, "Name 1", "Value 1", true));
        values.add(createOptionEnumValue(null, "Name 2", "Value 2", false));
        values.add(createOptionEnumValue(null, "Name 3", "Value 1", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[2].value");

        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue(null, "Name 1", "Value 1", true));
        values.add(createOptionEnumValue(null, "Name 2", "Value 2", false));
        values.add(createOptionEnumValue(null, "Name 1", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values[2].name");

        // no default
        values = new LinkedHashSet<OptionEnumValue>();
        values.add(createOptionEnumValue("Name 1", "Value 1", false));
        values.add(createOptionEnumValue("Name 2", "Value 2", false));
        values.add(createOptionEnumValue("Name 3", "Value 3", false));
        option.setValues(values);
        validate(validationName, option);
        assertHasViolation("values");

        // required
        option = createValid(optionId, OptionType.ENUM);

        option.setRequired(false);
        validate(validationName, option);
        assertHasViolation("required");

        // Validate Default Value
        option = createValid(optionId, OptionType.ENUM);

        option.setDefaultValue("Default Value");
        validate(validationName, option);
        assertHasViolation("defaultValue");

    }

    @Test
    public void testColorCreate() {
        testColorInternal("Option.create", null);
    }

    @Test
    public void testColorUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.COLOR);
        testColorInternal("Option.update", option.getId());
    }

    private void testColorInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.COLOR);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Default Value
        option.setDefaultValue("invalid color");
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testFileCreate() {
        testFileInternal("Option.create", OptionType.FILE, null);
    }

    @Test
    public void testFileUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.FILE);
        testFileInternal("Option.update", OptionType.FILE, option.getId());
    }

    @Test
    public void testDynamicFileCreate() {
        testFileInternal("Option.create", OptionType.DYNAMIC_FILE, null);
    }

    @Test
    public void testDynamicFileUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.DYNAMIC_FILE);
        testFileInternal("Option.update", OptionType.DYNAMIC_FILE, option.getId());
    }

    private void testFileInternal(String validationName, OptionType optionType, Long optionId) {
        Option option = createValid(optionId, optionType);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate File Types
        List<OptionFileType> fileTypes = new ArrayList<OptionFileType>();
        OptionFileType fileTypesOption = new OptionFileType();
        fileTypesOption.setFileType("invalid");
        fileTypes.add(fileTypesOption);
        option.setFileTypes(fileTypes);
        validate(validationName, option);
        assertHasViolation("fileTypes");

        // Validate Default Value
        option = createValid(optionId, optionType);

        option.setDefaultValue("Default Value");
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testRequiredChange() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.INTEGER);
        option = createValid(option.getId(), OptionType.INTEGER, optionGroup);
        Creative creative = createCreative(template, size, option);

        option.setRequired(true);
        validate("Option.update", option);
        assertTrue(violations.isEmpty());

        for (CreativeOptionValue creativeOptionValue : creative.getOptions()) {
            creativeOptionValue.setValue(null);
        }
        getEntityManager().flush();

        option.setRequired(true);
        validate("Option.update", option);
        // Required checks are gone: OUI-26501
        assertHasNoViolation("required");

        for (CreativeOptionValue creativeOptionValue : creative.getOptions()) {
            entityManager.remove(creativeOptionValue);
        }
        creative.getOptions().clear();
        getEntityManager().flush();

        option.setRequired(true);
        validate("Option.update", option);
        assertTrue(violations.isEmpty());

        option.setRequired(true);
        option.setDefaultValue(null);
        validate("Option.update", option);
        // Required checks are gone: OUI-26501
        assertHasNoViolation("required");
    }

    @Test
    public void testTypeChange() {
        // Change type from "integer", "color", "URL" to "string" and "text"
        Option integerOption = createValid(null, OptionType.INTEGER);
        optionTF.persist(integerOption);
        createCreative(template, size, integerOption);
        validate("Option.update", createValid(integerOption.getId(), OptionType.STRING));
        assertViolationsCount(0);
        validate("Option.update", createValid(integerOption.getId(), OptionType.TEXT));
        assertViolationsCount(0);
        validate("Option.update", createValid(integerOption.getId(), OptionType.ENUM));
        assertHasViolation("type");
        optionTF.remove(integerOption);

        Option colorOption = createValid(null, OptionType.COLOR);
        optionTF.persist(colorOption);
        createCreative(template, size, colorOption);
        validate("Option.update", createValid(colorOption.getId(), OptionType.STRING));
        assertViolationsCount(0);
        validate("Option.update", createValid(colorOption.getId(), OptionType.TEXT));
        assertViolationsCount(0);
        validate("Option.update", createValid(colorOption.getId(), OptionType.HTML));
        assertHasViolation("type");
        optionTF.remove(colorOption);

        Option urlOption = createValid(null, OptionType.URL);
        optionTF.persist(urlOption);
        createCreative(template, size, urlOption);
        validate("Option.update", createValid(urlOption.getId(), OptionType.STRING));
        assertViolationsCount(0);
        validate("Option.update", createValid(urlOption.getId(), OptionType.TEXT));
        assertViolationsCount(0);
        validate("Option.update", createValid(urlOption.getId(), OptionType.FILE_URL));
        assertHasViolation("type");
        optionTF.remove(urlOption);

        // Change option type from "string", "html" to "text"
        Option stringOption = createValid(null, OptionType.STRING);
        optionTF.persist(stringOption);
        createCreative(template, size, stringOption);
        validate("Option.update", createValid(stringOption.getId(), OptionType.TEXT));
        assertViolationsCount(0);
        validate("Option.update", createValid(stringOption.getId(), OptionType.HTML));
        assertHasViolation("type");
        optionTF.remove(stringOption);

        Option htmlOption = createValid(null, OptionType.HTML);
        optionTF.persist(htmlOption);
        createCreative(template, size, htmlOption);
        validate("Option.update", createValid(htmlOption.getId(), OptionType.TEXT));
        assertViolationsCount(0);
        validate("Option.update", createValid(htmlOption.getId(), OptionType.STRING));
        assertHasViolation("type");
        optionTF.remove(htmlOption);

        // Any other option type changes are prohibited in the option is used by other entities
        Option fileOption = createValid(null, OptionType.FILE);
        optionTF.persist(fileOption);
        Creative creative = createCreative(template, size, fileOption);

        validate("Option.update", createValid(fileOption.getId(), OptionType.STRING));
        assertHasViolation("type");

        for (CreativeOptionValue creativeOptionValue : creative.getOptions()) {
            creativeOptionValue.setValue(null);
        }
        getEntityManager().flush();

        validate("Option.update", createValid(fileOption.getId(), OptionType.STRING));
        assertViolationsCount(0);
    }

    private Creative createCreative(CreativeTemplate template, CreativeSize size, Option option) {
        Creative creative = creativeTestFactory.createPersistent(template, size);
        //creative.getTemplate().getOptionGroups().add(option.getOptionGroup());

        getEntityManager().flush();

        getEntityManager().refresh(creative);
        assertTrue(creative.getTemplate().getAllOptions().size() > 0);

        CreativeOptionValue optionValue = new CreativeOptionValue(new CreativeOptionValuePK(creative.getId(), option.getId()));
        optionValue.setValue("4");
        creative.getOptions().add(optionValue);
        getEntityManager().flush();

        CreativeOptGroupState groupState = new CreativeOptGroupState();
        groupState.setId(new CreativeOptGroupStatePK(option.getOptionGroup().getId(), creative.getId()));
        groupState.setEnabled(true);
        groupState.setCollapsed(false);
        creative.getGroupStates().add(groupState);
        getEntityManager().flush();

        getEntityManager().refresh(creative);
        assertTrue(creative.getOptions().size() > 0);

        return creative;
    }

    @Test
    public void testFileUrlCreate() {
        testFileUrlInternal("Option.create", null);
    }

    @Test
    public void testFileUrlUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.FILE_URL);
        testFileUrlInternal("Option.update", option.getId());
    }

    private void testFileUrlInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.FILE_URL);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate File Types
        List<OptionFileType> fileTypes = new ArrayList<OptionFileType>();
        OptionFileType fileTypesOption = new OptionFileType();
        fileTypesOption.setFileType("invalid");
        fileTypes.add(fileTypesOption);
        option.setFileTypes(fileTypes);
        validate(validationName, option);
        assertHasViolation("fileTypes");

        // Validate Default Value
        option = createValid(optionId, OptionType.FILE_URL);

        option.setDefaultValue("Default Value");
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testUrlCreate() {
        testUrlInternal("Option.create", null);
    }

    @Test
    public void testUrlUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.FILE_URL);
        testUrlInternal("Option.update", option.getId());
    }

    private void testUrlInternal(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.FILE_URL);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Default Value
        option.setDefaultValue("Default Value");
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testUrlWithoutProtocolCreate() {
        testUrlWithoutProtocol("Option.create", null);
    }

    @Test
    public void testUrlWithoutProtocolUpdate() {
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.URL_WITHOUT_PROTOCOL);
        testUrlWithoutProtocol("Option.update", option.getId());
    }

    private void testUrlWithoutProtocol(String validationName, Long optionId) {
        Option option = createValid(optionId, OptionType.URL_WITHOUT_PROTOCOL);
        validate(validationName, option);
        assertTrue(violations.isEmpty());

        // Validate Default Value
        option.setDefaultValue("Default Value");
        validate(validationName, option);
        assertHasViolation("defaultValue");

        // Validate With Schema
        option.setDefaultValue("http://url.com");
        validate(validationName, option);
        assertHasViolation("defaultValue");
    }

    @Test
    public void testChangeTypeFromStringToUrlWithoutProtocol(){
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        Option option = optionTF.createPersistent(optionGroup, OptionType.STRING);
        option = createValid(option.getId(), OptionType.URL_WITHOUT_PROTOCOL);

        validate("Option.update", option);
        assertTrue(violations.isEmpty());
    }

    private Option createValid(Long id, OptionType type) {
        return createValid(id, type, null);
    }
    private Option createValid(Long id, OptionType type, OptionGroup optionGroup) {
        if (optionGroup == null) {
            optionGroup = optionGroupTF.createPersistent(template);
        }
        Option option = new Option(id);
        option.setDefaultName("Default Name");
        option.setDefaultLabel("Default Tooltip");
        option.setOptionGroup(optionGroup);
        option.setToken("TokenValue123");
        option.setType(type);
        switch (type) {
            case INTEGER:
                option.setDefaultValue("100");
                option.setMinValue(-200L);
                option.setMaxValue(200L);
                break;
            case ENUM:
                Set<OptionEnumValue> values = new LinkedHashSet<OptionEnumValue>();
                values.add(createOptionEnumValue("Name 1","Value 1", true));
                values.add(createOptionEnumValue("Name 2", "Value 2", false));
                values.add(createOptionEnumValue("Name 3", "Value 3", false));
                option.setValues(values);
                option.setRequired(true);
                break;
            case COLOR:
                option.setDefaultValue("ab1234");
                break;
            case FILE:
            case FILE_URL:
            case DYNAMIC_FILE:
                List<String> allowedFileTypes = configService.get(ALLOWED_FILE_TYPES);
                List<OptionFileType> fileTypes = new ArrayList<OptionFileType>();
                for (String fileType : allowedFileTypes) {
                    OptionFileType fileTypesOption = new OptionFileType();
                    fileTypesOption.setFileType(fileType);
                    fileTypes.add(fileTypesOption);
                }
                option.setFileTypes(fileTypes);

                if (type == OptionType.FILE_URL) {
                    option.setDefaultValue("http://url.com");
                }
                break;
            case URL:
                option.setDefaultValue("http://url.com");
                break;
            case URL_WITHOUT_PROTOCOL:
                option.setDefaultValue("url.com");
                break;
            default:
                option.setDefaultValue("Default Value");
                break;
        }
        return option;
    }

    private OptionEnumValue createOptionEnumValue(String name, String value, boolean isDefault) {
        return createOptionEnumValue(null, name, value, isDefault);
    }

    private OptionEnumValue createOptionEnumValue(Long id, String name, String value, boolean isDefault) {
        OptionEnumValue enumValue = new OptionEnumValue();
        enumValue.setId(id);
        enumValue.setName(name);
        enumValue.setValue(value);
        enumValue.setDefault(isDefault);
        return enumValue;
    }
}
