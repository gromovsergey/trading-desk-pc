package com.foros.session.site;


import static com.foros.config.ConfigParameters.ADSERVER_DATA_URL;
import static com.foros.config.ConfigParameters.DATA_URL;
import static com.foros.config.ConfigParameters.PUBL_PATH;
import static com.foros.config.ConfigParameters.REDIRECT_PATH;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.MockConfigService;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateFileType;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProviderService;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DiscoverTemplateTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.WDTagTestFactory;
import com.foros.util.NumberUtil;

import group.Db;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class WDTagServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private WDTagPreviewService wdTagPreviewService;

    @Autowired
    private WDTagTestFactory wdTagTF;

    @Autowired
    private PathProviderService pathProviderService;

    @Autowired
    private MockConfigService configService;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTestFactory;

    @Autowired
    private DiscoverTemplateTestFactory templateTF;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private OptionTestFactory optionTF;

    @Autowired
    private DiscoverTemplateTestFactory discoverTemplateTF;

    private static final String TEST_ADSERVER_DATA_URL = "https://adserver-data.ur/test";
    private static final String TEST_DATA_URL = "https://data.ur/test";
    private static final String TEST_REDIRECT_PATH = "/redirect/path";
    private static final String TEST_PUBL_PATH = "/publ/path";

    private List<String> fixedTokens;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        fixedTokens = new ArrayList<>();
        fixedTokens.add("TAGWIDTH");
        fixedTokens.add("WDTAGID");
        fixedTokens.add("CLICK");
        fixedTokens.add("HEIGHT");
        fixedTokens.add("TEMPLATE");
        fixedTokens.add("TAGHEIGHT");
        fixedTokens.add("ADIMAGE-SERVER");
        fixedTokens.add("APP_FORMAT");
        fixedTokens.add("WIDTH");

        configService.set(ADSERVER_DATA_URL, TEST_ADSERVER_DATA_URL);
        configService.set(DATA_URL, TEST_DATA_URL);
        configService.set(REDIRECT_PATH, TEST_REDIRECT_PATH);
        configService.set(PUBL_PATH, TEST_PUBL_PATH);
    }

    @Test
    public void testPreviewService() throws Exception {
        WDTag wdTag = generateWdTag();

        FileSystem fs = pathProviderService.getTemplates().createFileSystem();
        try (OutputStream os = fs.openFile("testPreviewService.html")) {
            os.write(generateTemplateFileContent(wdTag.getOptions()));
        }

        creativeTemplateTestFactory.createPersistentTemplateFile(wdTag.getTemplate(), TemplateFileType.TEXT,
                ApplicationFormat.DISCOVER_TAG_FORMAT, null, "testPreviewService.html");

        String htmlCode = wdTagPreviewService.getHTMLCode(wdTag);
        checkAllExist(wdTag, htmlCode);
    }

    @Test
    /**
     * tests whether integer option values are save in plain format understandable for Long.valueOf()
     */
    public void testIntegerOptionValueFormat() {
        String valueWithComma = "1,000";
        Long expected = NumberUtil.parseLong(valueWithComma);

        DiscoverTemplate template = templateTF.createPersistent();
        OptionGroup group = optionGroupTF.createPersistent(template);
        Option o = optionTF.createPersistent(group, OptionType.INTEGER);

        WDTag tag = wdTagTF.create();
        tag.setTemplate(template);

        tag.getOptions().add(wdTagTF.createWDTagOption(o, valueWithComma));
        wdTagTF.persist(tag);

        getEntityManager().flush();
        getEntityManager().clear();

        Set<WDTagOptionValue> options = wdTagTF.findById(tag.getId()).getOptions();
        WDTagOptionValue value = options.toArray(new WDTagOptionValue[options.size()])[0];

        assertEquals(expected, Long.valueOf(value.getValue()));
    }

    private WDTag generateWdTag() {
        DiscoverTemplate templ = discoverTemplateTF.create(new TemplateFile[0]);
        discoverTemplateTF.persist(templ);

        OptionGroup optionGroup = optionGroupTF.createPersistent(null, templ, OptionGroupType.Publisher);
        generateOptionsOfEveryType(optionGroup);

        WDTag wdTag = wdTagTF.create();
        wdTag.setId(1l);
        wdTag.setTemplate(templ);
        wdTag.setOptions(generateOptValuesOfEveryType(templ));

        entityManager.flush();

        return wdTag;
    }

    private void generateOptionsOfEveryType(OptionGroup optionGroup) {
        optionTF.createPersistent(optionGroup, OptionType.STRING);
        optionTF.createPersistent(optionGroup, OptionType.TEXT);
        optionTF.createPersistent(optionGroup, OptionType.FILE);
        optionTF.createPersistent(optionGroup, OptionType.URL);
        optionTF.createPersistent(optionGroup, OptionType.FILE_URL);
        optionTF.createPersistent(optionGroup, OptionType.INTEGER);
        optionTF.createPersistent(optionGroup, OptionType.COLOR);
        optionTF.createPersistent(optionGroup, OptionType.ENUM);
    }

    private Set<WDTagOptionValue> generateOptValuesOfEveryType(DiscoverTemplate templ) {
        Set<WDTagOptionValue> options = new HashSet<WDTagOptionValue>();
        for (Option option : templ.getPublisherOptions()) {
            switch (option.getType()) {
                case STRING:
                    options.add(wdTagTF.createWDTagOption(option, "StringOptVal"));
                    break;
                case TEXT:
                    options.add(wdTagTF.createWDTagOption(option, "StringOptVal\nStringOptVal"));
                    break;
                case FILE:
                    options.add(wdTagTF.createWDTagOption(option, "file.js"));
                    break;
                case URL:
                    options.add(wdTagTF.createWDTagOption(option, "test-url.com"));
                    break;
                case FILE_URL:
                    options.add(wdTagTF.createWDTagOption(option, "file-url.js"));
                    break;
                case INTEGER:
                    options.add(wdTagTF.createWDTagOption(option, "33333"));
                    break;
                case COLOR:
                    options.add(wdTagTF.createWDTagOption(option, "FFFFFF"));
                    break;
                case ENUM:
                    options.add(wdTagTF.createWDTagOption(option, "test-enum"));
                    break;
            }
        }
        return options;
    }

    private byte[] generateTemplateFileContent(Set<WDTagOptionValue> options) {
        StringBuilder builder = new StringBuilder();
        builder.append(",");

        for (String token : fixedTokens) {
            builder.append("##");
            builder.append(token);
            builder.append("##,");
        }

        for (WDTagOptionValue optVal : options) {
            builder.append("##");
            builder.append(optVal.getOption().getToken());
            builder.append("##,");
        }

        try {
            return builder.toString().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAllExist(WDTag wdTag, String htmlCode) {
        for (WDTagOptionValue optVal : wdTag.getOptions()) {
            String val = optVal.getValue();
            if (optVal.getOption().getType().equals(OptionType.FILE_URL) || optVal.getOption().getType().equals(OptionType.FILE)) {
                val = TEST_ADSERVER_DATA_URL + "/" + TEST_PUBL_PATH + "/" + val;
            } else if (optVal.getOption().getType().equals(OptionType.COLOR)) {
                val = "#" + val;
            }

            assertContains(htmlCode, "," + val + ",");
        }

        assertContains(htmlCode, "," + wdTag.getId() + ",");

        assertContains(htmlCode, "," + wdTag.getWidth() + ",");
        assertContains(htmlCode, "," + wdTag.getHeight() + ",");
        assertContains(htmlCode, "," + TEST_ADSERVER_DATA_URL + ",");
        assertContains(htmlCode, "," + ApplicationFormat.DISCOVER_TAG_FORMAT + ",");
        assertContains(htmlCode, "," + wdTag.getTemplate().getName().getDefaultName() + ",");
        assertContains(htmlCode, TEST_ADSERVER_DATA_URL + "/" + TEST_REDIRECT_PATH);
    }

    private void assertContains(String htmlCode, String val) {
        if (!htmlCode.contains(val)) {
            fail(htmlCode + " doesn't contain " + val);
        }
    }
}
