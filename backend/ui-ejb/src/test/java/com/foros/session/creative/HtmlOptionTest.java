package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValueUtils;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.restrictions.NullFileNameRestriction;
import com.foros.session.template.HtmlOptionFileHelper;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.tx.TransactionSupportServiceMock;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HtmlOptionTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private DisplayCreativeTestFactory creativeTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private PathProviderService pathProviderService;

    @Autowired
    private TransactionSupportServiceMock transactionSupportService;

    @Test
    public void testChangeCreativeOptionValues() {
        Creative creative = creativeTF.createPersistent();
        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");
        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        creative = creativeTF.refresh(creative);
        clearContext();

        setCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        clearContext();
        creative = creativeTF.refresh(creative);
        clearContext();

        setCreativeOptionValue(creative, htmlOption, "<html>test value2</html>");
        creativeTF.update(creative);
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        clearContext();
        creative = creativeTF.refresh(creative);
        clearContext();

        setCreativeOptionValue(creative, htmlOption, "");
        creativeTF.update(creative);
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 0);
        checkHtmlFiles(creative, 0);
    }

    @Test
    public void testDeleteHtmlOption() {
        Creative creative = creativeTF.createPersistent();
        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");
        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        clearContext();

        optionTF.remove(htmlOption);
        commitChanges();
        clearContext();
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 0);
        checkHtmlFiles(creative, 0);
    }

    @Test
    public void testDeleteHtmlOptionGroup() {
        Creative creative = creativeTF.createPersistent();
        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");
        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        optionGroupTF.remove(htmlOption.getOptionGroup());
        commitChanges();
        clearContext();
        creative = creativeTF.refresh(creative);
        checkOptionValuesCount(creative, 0);
        checkHtmlFiles(creative, 0);
    }

    @Test
    public void testCreateDynamicFileOption() {
        Creative creative = creativeTF.createPersistent();
        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        checkOptionValuesCount(creative, 1);
        checkHtmlFiles(creative, 1);

        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);
    }

    @Test
    public void testUpdateHtmlOptionToken() {
        Creative creative = creativeTF.createPersistent();

        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");

        OptionGroup hiddenGroup = optionGroupTF.createPersistent(creative.getSize(), null, OptionGroupType.Hidden);
        Option dynamicFileOption = createDynamicFileOption(hiddenGroup, "test_token_FILE");
        Option dynamicFileOption2 = createDynamicFileOption(hiddenGroup, "test_token2_FILE");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        htmlOption.setToken("test_token2");
        optionTF.update(htmlOption);
        commitChanges();
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption2);
        checkHtmlFiles(creative, 1);

        htmlOption = entityManager.find(Option.class, htmlOption.getId());
        clearContext();
        htmlOption.setToken("test_token3");
        optionTF.update(htmlOption);
        commitChanges();
        checkOptionValuesCount(creative, 1);
        checkHtmlFiles(creative, 1);
    }

    @Test
    public void testUpdateHtmlOptionType() {
        Creative creative = creativeTF.createPersistent();
        Option htmlOption = createHtmlOption(creative.getSize(), "test_token");
        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        creativeTF.update(creative);
        checkOptionValuesCount(creative, 2);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 1);

        htmlOption.setType(OptionType.TEXT);
        optionTF.update(htmlOption);
        commitChanges();
        checkOptionValuesCount(creative, 1);
        checkHtmlFiles(creative, 0);
    }

    @Test
    public void testUpdateDynamicFileOptionToken() {
        Creative creative = creativeTF.createPersistent();

        OptionGroup advertiserGroup = optionGroupTF.createPersistent(creative.getSize(), null, OptionGroupType.Advertiser);
        Option htmlOption = createHtmlOption(advertiserGroup, "test_token");
        Option htmlOption2 = createHtmlOption(advertiserGroup, "test_token2");

        Option dynamicFileOption = createDynamicFileOption(creative.getSize(), "test_token_FILE");

        clearContext();

        addCreativeOptionValue(creative, htmlOption, "<html>test value</html>");
        addCreativeOptionValue(creative, htmlOption2, "<html>test value2</html>");
        creativeTF.update(creative);
        checkOptionValuesCount(creative, 3);
        checkDynamicFileOptionValue(creative, htmlOption, dynamicFileOption);
        checkHtmlFiles(creative, 2);

        dynamicFileOption.setToken("test_token2_FILE");
        optionTF.update(dynamicFileOption);
        commitChanges();
        checkOptionValuesCount(creative, 3);
        checkDynamicFileOptionValue(creative, htmlOption2, dynamicFileOption);
        checkHtmlFiles(creative, 2);

        dynamicFileOption = entityManager.find(Option.class, dynamicFileOption.getId());
        clearContext();
        dynamicFileOption.setToken("test_token3_FILE");
        optionTF.update(dynamicFileOption);
        commitChanges();
        checkOptionValuesCount(creative, 2);
        checkHtmlFiles(creative, 2);
    }

    private Option createHtmlOption(CreativeSize size, String token) {
        OptionGroup advertiserGroup = optionGroupTF.createPersistent(size, null, OptionGroupType.Advertiser);
        return createHtmlOption(advertiserGroup, token);
    }

    private Option createHtmlOption(OptionGroup advertiserGroup, String token) {
        Option htmlOption = optionTF.create(advertiserGroup, OptionType.HTML);
        htmlOption.setToken(token);
        optionTF.persist(htmlOption);
        return htmlOption;
    }

    private Option createDynamicFileOption(CreativeSize size, String token) {
        OptionGroup hiddenGroup = optionGroupTF.createPersistent(size, null, OptionGroupType.Hidden);
        return createDynamicFileOption(hiddenGroup, token);
    }

    private Option createDynamicFileOption(OptionGroup hiddenGroup, String token) {
        Option dynamicFileOption = optionTF.create(hiddenGroup, OptionType.DYNAMIC_FILE);
        dynamicFileOption.setToken(token);
        optionTF.persist(dynamicFileOption);
        return dynamicFileOption;
    }

    private void setCreativeOptionValue(Creative creative, final Option option, String value) {
        CreativeOptionValue existing = CollectionUtils.find(creative.getOptions(), new Filter<CreativeOptionValue>() {
            @Override
            public boolean accept(CreativeOptionValue element) {
                return element.getOptionId().equals(option.getId());
            }
        });
        creative.setOptions(new LinkedHashSet<CreativeOptionValue>());
        CreativeOptionValue optionValue = addCreativeOptionValue(creative, option, value);
        if (existing != null) {
            optionValue.setVersion(existing.getVersion());
        }
    }

    private CreativeOptionValue addCreativeOptionValue(final Creative creative, Option option, String value) {
        CreativeOptionValuePK optionValuePK = new CreativeOptionValuePK(creative.getId(), option.getId());
        CreativeOptionValue optionValue = new CreativeOptionValue(optionValuePK);
        optionValue.setOption(option);
        optionValue.setCreative(creative);
        optionValue.setValue(value);
        creative.getOptions().add(optionValue);
        return optionValue;
    }

    private void checkOptionValuesCount(Creative creative, long expected) {
        int optionValueCount = jdbcTemplate.queryForInt("select count(*) from CREATIVEOPTIONVALUE where creative_id = ?", creative.getId());
        assertEquals(expected, optionValueCount);
    }

    private void checkDynamicFileOptionValue(Creative creative, Option htmlOption, Option dynamicFileOption) {
        Timestamp htmlOptionVersion = jdbcTemplate.queryForObject("select version from CREATIVEOPTIONVALUE where creative_id = ? and option_id = ?", Timestamp.class, creative.getId(), htmlOption.getId());

        assertNotNull("Creative should have html option value", htmlOptionVersion);

        DateFormat dateFormat = new SimpleDateFormat(HtmlOptionFileHelper.VERSION_HTML_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String fileName = "/" + htmlOption.getId() + "-" + dateFormat.format(htmlOptionVersion) + ".html";

        String value = jdbcTemplate.queryForObject("select value from CREATIVEOPTIONVALUE where creative_id = ? and option_id = ?", String.class, creative.getId(), dynamicFileOption.getId());
        assertTrue("Invalid dynamic file option value", value != null && value.endsWith(fileName));
    }

    private void checkHtmlFiles(Creative creative, int expectedFilesCount) {
        transactionSupportService.doBeforeCompletion();
        transactionSupportService.doCommit();

        FileSystem creativesFS = pathProviderService.getCreatives().createFileSystem();
        creativesFS.setFileNameRestriction(new NullFileNameRestriction());
        Set<String> files = new HashSet<>();
        files.addAll(Arrays.asList(creativesFS.list(OptionValueUtils.getHtmlRoot(creative))));

        assertEquals(expectedFilesCount, files.size());

        for (CreativeOptionValue optionValue : creative.getOptions()) {
            if (optionValue.getOption().getType() != OptionType.HTML) {
                continue;
            }
            String file = HtmlOptionFileHelper.createFileName(optionValue);
            if (!files.contains(file)) {
                fail(file + " is missing");
            } else {
                files.remove(file);
            }
        }

        if (!files.isEmpty()) {
            fail("Excessive files + " + files);
        }
    }
}
