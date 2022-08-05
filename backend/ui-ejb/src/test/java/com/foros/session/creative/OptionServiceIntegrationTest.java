package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptionValue;
import com.foros.model.site.TagOptionValuePK;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.site.WDTagOptionValuePK;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.session.site.TagsService;
import com.foros.session.site.WDTagService;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.OptionService;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.WDTagTestFactory;

import group.Db;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class OptionServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private OptionGroupService optionGroupService;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private OptionService optionService;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private WDTagTestFactory wdTagTF;

    @Autowired
    private WDTagService wdTagService;

    @Autowired
    private TagsTestFactory tagTF;

    @Autowired
    private TagsService tagService;

    @Test
    public void testCreateEntity() {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);
        Option option = optionTF.createPersistent(optionGroup, OptionType.STRING);

        Option found = optionService.findById(option.getId());
        assertSame("Entity is not created properly", option, found);
    }

    @Test
    public void testUpdateEntity() {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);

        Option option = optionTF.create(optionGroup, OptionType.ENUM);
        option.getValues().clear();
        option.getValues().add(createEnumValue("111", "111", true));
        option.getValues().add(createEnumValue("222", "222", false));
        optionTF.persist(option);
        commitChanges();

        Option old = option;
        option = optionTF.create(optionGroup, OptionType.ENUM);
        option.getValues().clear();
        option.setId(old.getId());
        option.setVersion(old.getVersion());
        option.getValues().add(createEnumValue("444", "444", false));
        option.getValues().add(createEnumValue("333", "333", true));

        optionService.update(option);
        commitChanges();

        option = optionService.findById(option.getId());
        assertEquals(2, option.getValues().size());
    }

    @Test
    public void testRemove() {
        CreativeSize size = creativeSizeTF.createPersistent();
        CreativeTemplate template = creativeTemplateTF.createPersistent();
        displayCreativeTF.createPersistent(template, size);

        OptionGroup optionGroup = optionGroupTF.createPersistent(template);

        Option option = optionTF.createPersistent(optionGroup, OptionType.STRING);
        commitChanges();
        optionGroup = optionGroupService.findById(optionGroup.getId());
        assertNotNull(optionGroup);

        option = optionService.findById(option.getId());
        entityManager.clear();
        optionService.remove(option.getId());
        commitChanges();
        try {
            optionService.findById(option.getId());
            fail();
        } catch (EntityNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testFindByToken() {
        Option option = optionService.findByTokenFromTextTemplate(TextCreativeOption.HEADLINE.getToken());
        assertNotNull(option);
    }

    private OptionEnumValue createEnumValue(String name, String value, boolean def) {
        OptionEnumValue ev = new OptionEnumValue();
        ev.setName(name);
        ev.setValue(value);
        ev.setDefault(def);
        return ev;
    }

    @Test
    public void testCreativeOptionValues() {
        Creative creative = displayCreativeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(creative.getSize(), null, OptionGroupType.Advertiser, null);
        Option option = optionTF.create(optionGroup, OptionType.STRING);
        option.setDefaultValue("default_value");
        optionTF.persist(option);

        int optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 0);

        addCreativeOptionValue(creative, option, "default_value");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 0);

        addCreativeOptionValue(creative, option, "test");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 1);
    }

    private void addCreativeOptionValue(Creative creative, Option option, String value) {
        CreativeOptionValuePK optionValuePK = new CreativeOptionValuePK(creative.getId(), option.getId());
        CreativeOptionValue optionValue = new CreativeOptionValue(optionValuePK);
        optionValue.setOption(option);
        optionValue.setCreative(creative);
        optionValue.setValue(value);
        creative.getOptions().add(optionValue);
        displayCreativeTF.update(creative);
        commitChanges();
        clearContext();
    }

    @Test
    public void testColorOptionValues() {
        Creative creative = displayCreativeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(creative.getSize(), null, OptionGroupType.Advertiser, null);
        Option option = optionTF.create(optionGroup, OptionType.COLOR);
        option.setDefaultValue("11ffaa");
        optionTF.persist(option);

        int optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 0);

        addCreativeOptionValue(creative, option, "11FFAA");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 0);

        addCreativeOptionValue(creative, option, "11ff11");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where  creative_id = ?", creative.getId());
        assertEquals(optionValueCount, 1);
    }

    @Test
    public void testTagOptionValues() {
        Tag tag = tagTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(tag.getSizes().iterator().next(), null, OptionGroupType.Publisher, null);
        Option option = optionTF.create(optionGroup, OptionType.STRING);
        option.setDefaultValue("default_value");
        optionTF.persist(option);
        clearContext();

        int optionValueCount = jdbcTemplate.queryForInt("select count(*) from TAGOPTIONVALUE where tag_id = ?", tag.getId());
        assertEquals(optionValueCount, 0);

        addTagOptionValue(tag, option, "default_value");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from TAGOPTIONVALUE where tag_id = ?", tag.getId());
        assertEquals(optionValueCount, 0);

        addTagOptionValue(tag, option, "test");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from TAGOPTIONVALUE where tag_id = ?", tag.getId());
        assertEquals(optionValueCount, 1);
    }

    private void addTagOptionValue(Tag tag, Option option, String value) {
        TagOptionValuePK optionValuePK = new TagOptionValuePK(tag.getId(), option.getId());
        TagOptionValue optionValue = new TagOptionValue(optionValuePK);
        optionValue.setOption(option);
        optionValue.setValue(value);
        tag.getOptions().add(optionValue);
        tagService.updateOptions(tag);
        commitChanges();
        clearContext();
    }

    @Test
    public void testWdTagOptionValues() {
        WDTag wdTag = wdTagTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(null, wdTag.getTemplate(), OptionGroupType.Publisher, null);
        Option option = optionTF.create(optionGroup, OptionType.STRING);
        option.setDefaultValue("default_value");
        optionTF.persist(option);

        int optionValueCount = jdbcTemplate.queryForInt("select count(*) from WDTAGOPTIONVALUE where wdtag_id = ?", wdTag.getId());
        assertEquals(optionValueCount, 0);

        addWdTagOptionValue(wdTag, option, "default_value");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from WDTAGOPTIONVALUE where wdtag_id = ?", wdTag.getId());
        assertEquals(optionValueCount, 0);

        addWdTagOptionValue(wdTag, option, "test");

        optionValueCount = jdbcTemplate.queryForInt("select count(*) from WDTAGOPTIONVALUE where wdtag_id = ?", wdTag.getId());
        assertEquals(optionValueCount, 1);
    }

    private void addWdTagOptionValue(WDTag tag, Option option, String value) {
        WDTagOptionValuePK optionValuePK = new WDTagOptionValuePK(tag.getId(), option.getId());
        WDTagOptionValue optionValue = new WDTagOptionValue(optionValuePK);
        optionValue.setOption(option);
        optionValue.setTag(tag);
        optionValue.setValue(value);
        tag.getOptions().add(optionValue);
        wdTagService.update(tag);
        commitChanges();
        clearContext();
    }
}
