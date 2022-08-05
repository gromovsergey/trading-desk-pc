package com.foros.rs.sandbox;

import com.foros.cache.CacheManager;
import com.foros.cache.CacheManagerMock;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.isp.Colocation;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.restriction.permission.PermissionService;
import com.foros.rs.sandbox.factory.AdvertiserAccountGeneratorFactory;
import com.foros.rs.sandbox.factory.AgencyAccountGeneratorFactory;
import com.foros.rs.sandbox.factory.AgencyAccountTypeGeneratorFactory;
import com.foros.rs.sandbox.factory.AudienceChannelGeneratorFactory;
import com.foros.rs.sandbox.factory.BehavioralChannelGeneratorFactory;
import com.foros.rs.sandbox.factory.CategoryChannelGeneratorFactory;
import com.foros.rs.sandbox.factory.ColocationGeneratorFactory;
import com.foros.rs.sandbox.factory.ConversionGeneratorFactory;
import com.foros.rs.sandbox.factory.CreativeCategoryGeneratorFactory;
import com.foros.rs.sandbox.factory.CreativeSizeGeneratorFactory;
import com.foros.rs.sandbox.factory.CreativeTemplateGeneratorFactory;
import com.foros.rs.sandbox.factory.DisplayCampaignGeneratorFactory;
import com.foros.rs.sandbox.factory.DisplayCreativeGeneratorFactory;
import com.foros.rs.sandbox.factory.DisplayCreativeLinkGeneratorFactory;
import com.foros.rs.sandbox.factory.DisplayGroupGeneratorFactory;
import com.foros.rs.sandbox.factory.IspAccountGeneratorFactory;
import com.foros.rs.sandbox.factory.IspAccountTypeGeneratorFactory;
import com.foros.rs.sandbox.factory.OptionGeneratorFactory;
import com.foros.rs.sandbox.factory.OptionGroupGeneratorFactory;
import com.foros.rs.sandbox.factory.PublisherAccountGeneratorFactory;
import com.foros.rs.sandbox.factory.PublisherAccountTypeGeneratorFactory;
import com.foros.rs.sandbox.factory.SiteGeneratorFactory;
import com.foros.rs.sandbox.factory.TagGeneratorFactory;
import com.foros.rs.sandbox.factory.TextCampaignGeneratorFactory;
import com.foros.rs.sandbox.factory.TextGroupGeneratorFactory;
import com.foros.rs.sandbox.factory.UserGeneratorFactory;
import com.foros.security.SecurityContextMock;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ServiceLocatorMock;
import com.foros.session.admin.CustomizationResourcesService;
import com.foros.session.admin.CustomizationResourcesServiceMock;
import com.foros.session.admin.DynamicResourcesService;
import com.foros.session.admin.DynamicResourcesServiceMock;
import com.foros.session.campaign.CCGKeywordService;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.session.security.UserCredentialService;
import com.foros.session.security.UserService;
import com.foros.session.template.OptionService;
import com.foros.test.factory.ActionTestFactory;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.test.factory.ColocationTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DeviceChannelTestFactory;
import com.foros.test.factory.DisplayCCGTestFactory;
import com.foros.test.factory.DisplayCampaignTestFactory;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.IspAccountTestFactory;
import com.foros.test.factory.IspAccountTypeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.PublisherAccountTypeTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.TextCCGTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.test.factory.UserTestFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.io.output.TeeOutputStream;
import org.easymock.EasyMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class ForosApiSandboxGenerator {
    private static final Logger logger = Logger.getLogger(ForosApiSandboxGenerator.class.getName());

    private static final String NAME_PREFIX = "RsApiGen.17.";

    // pwtest
    public static final String TEST_PASSWORD_HASH = "vtTO42M9MpzR2SiQ73zK7PkYv6c=";
    // test@ocslab.com
    public static final long ADMINISTRATOR_USER_ID = 1L;
    // OUI Agency Account Administrator
    public static final long AGENCY_ADMIN_ROLE_ID = 23L;
    // OUI Publisher Account Administrator
    public static final long PUBLISHER_ADMIN_ROLE_ID = 6L;


    private static final String[] CONTEXT_LOCATIONS = {"root-test-context.xml", "test-context.xml", "sandbox-properties.xml"};

    @Autowired
    private JpaTransactionManager transactionManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTestFactory userTestFactory;

    @Autowired
    private AgencyAccountTypeTestFactory agencyAccountTypeTestFactory;

    @Autowired
    private PublisherAccountTypeTestFactory publisherAccountTypeTestFactory;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTestFactory;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTestFactory;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTestFactory;

    @Autowired
    private IspAccountTestFactory ispAccountTestFactory;

    @Autowired
    private IspAccountTypeTestFactory ispAccountTypeTestFactory;

    @Autowired
    private SiteTestFactory siteTestFactory;

    @Autowired
    private TextCampaignTestFactory textCampaignTestFactory;

    @Autowired
    private DisplayCampaignTestFactory displayCampaignTestFactory;

    @Autowired
    private TextCCGTestFactory textCCGTestFactory;

    @Autowired
    private DisplayCCGTestFactory displayCCGTestFactory;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTestFactory;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTestFactory;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTestFactory;

    @Autowired
    private BehavioralChannelService behavioralChannelService;

    @Autowired
    private DeviceChannelTestFactory deviceChannelTestFactory;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTestFactory;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTestFactory;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTestFactory;

    @Autowired
    private TagsTestFactory tagsTestFactory;

    @Autowired
    private ActionTestFactory actionTestFactory;

    @Autowired
    private DisplayCreativeLinkTestFactory displayCreativeLinkTestFactory;

    @Autowired
    private OptionGroupTestFactory optionGroupTestFactory;

    @Autowired
    private OptionTestFactory optionTestFactory;

    @Autowired
    private CreativeCategoryService creativeCategoryService;

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private CCGKeywordService ccgKeywordService;

    @Autowired
    private ColocationTestFactory colocationTestFactory;

    @Resource
    private String baseUrl;

    private static Path outputPath;

    public static void main(String[] args) {
        try {
            processParams(args);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "USAGE: Utility parameters: <utility properties file> <output properties file>", e);
            System.exit(1);
        }

        if (Files.exists(outputPath)) {
            logger.log(Level.INFO, "File {0} exists", outputPath);
            System.exit(0);
        }

        ForosApiSandboxGenerator generator = instantiateWithContext();
        try {
            generator.generate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void processParams(String[] args) throws IOException {
        if (args.length != 2) {
            throw new RuntimeException("Two params are required");
        }

        Path propertiesPath = Paths.get(args[0]);
        try (InputStream fis = Files.newInputStream(propertiesPath)) {
            Properties properties = new Properties();
            properties.load(fis);
            for (String name : properties.stringPropertyNames()) {
                System.setProperty(name, properties.getProperty(name));
            }
            System.setProperty("impala.jdbc.driverClassName", "");
            System.setProperty("impala.jdbc.url", "");
        }

        outputPath = Paths.get(args[1]).toAbsolutePath();
        Files.createDirectories(outputPath.getParent());
    }

    private static ForosApiSandboxGenerator instantiateWithContext() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT_LOCATIONS);

        PermissionService permissionService = (PermissionService) applicationContext.getBean("permissionService");
        EasyMock.expect(permissionService.isGranted(EasyMock.anyString(), EasyMock.anyString())).andReturn(true).anyTimes();
        EasyMock.replay(permissionService);

        ServiceLocatorMock.getInstance().injectService(PermissionService.class, permissionService);
        ServiceLocatorMock.getInstance().injectService(CacheManager.class, new CacheManagerMock());
        ServiceLocatorMock.getInstance().injectService(DynamicResourcesService.class, new DynamicResourcesServiceMock());
        ServiceLocatorMock.getInstance().injectService(CustomizationResourcesService.class, new CustomizationResourcesServiceMock());
        ServiceLocatorMock.getInstance().setBeanFactory(applicationContext);

        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        ForosApiSandboxGenerator generator = new ForosApiSandboxGenerator();
        autowireCapableBeanFactory.autowireBean(generator);

        return generator;
    }

    private void generate() throws Exception {
        TransactionStatus transaction = transactionManager.getTransaction(null);
        try {
            User admin = userService.find(ADMINISTRATOR_USER_ID);
            SecurityContextMock.getInstance().setPrincipal(admin);
            CurrentUserSettingsHolder.set("127.0.0.1", TimeZone.getDefault(), Locale.UK);

            Properties properties = new OrderedProperties();
            properties.setProperty("foros.base", baseUrl);

            generate(admin, properties);

            transactionManager.commit(transaction);

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                properties.store(new TeeOutputStream(os, System.out), "For FOROS-API tests");
            }

        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    private void generate(User admin, Properties properties) throws Exception {
        InternalAccount internalAccount = (InternalAccount) admin.getAccount();

        CreativeCategoryGeneratorFactory creativeCategoryGeneratorFactory = new CreativeCategoryGeneratorFactory(creativeCategoryService);
        CreativeCategory contentCategory = creativeCategoryGeneratorFactory
                .findOrCreate(NAME_PREFIX + "ContentCategory", CreativeCategoryType.CONTENT);
        CreativeCategory visualCategory = creativeCategoryGeneratorFactory
                .findOrCreate(NAME_PREFIX + "VisualCategory", CreativeCategoryType.VISUAL);

        CreativeSize size = new CreativeSizeGeneratorFactory(creativeSizeTestFactory)
                .findOrCreate(NAME_PREFIX + "CreativeSize");

        CreativeTemplate template = new CreativeTemplateGeneratorFactory(creativeTemplateTestFactory, size)
                .findOrCreate(NAME_PREFIX + "CreativeTemplate");

        AccountType agencyAccountType = new AgencyAccountTypeGeneratorFactory(agencyAccountTypeTestFactory, size, template)
                .findOrCreate(NAME_PREFIX + "AgencyType");

        AgencyAccount agency = new AgencyAccountGeneratorFactory(agencyAccountTestFactory, agencyAccountType, internalAccount)
                .findOrCreate(NAME_PREFIX + "AgencyAccount");

        AdvertiserAccountGeneratorFactory advertiserAccountGeneratorFactory = new AdvertiserAccountGeneratorFactory(advertiserAccountTestFactory, agency);
        AdvertiserAccount advertiser = advertiserAccountGeneratorFactory.findOrCreate(NAME_PREFIX + "AdvertiserAccount");
        int advertiserCrowdSize = 2;
        for (int i = 0; i < advertiserCrowdSize; i++) {
            advertiserAccountGeneratorFactory.findOrCreate(NAME_PREFIX + "AdvertiserAccount-" + i);
        }

        UserGeneratorFactory userGeneratorFactory = new UserGeneratorFactory(userTestFactory, userCredentialService);
        User agencyUser = userGeneratorFactory.findOrCreate(NAME_PREFIX + "AgencyUser", agency);

        AccountType publisherAccountType = new PublisherAccountTypeGeneratorFactory(publisherAccountTypeTestFactory, size)
                .findOrCreate(NAME_PREFIX + "PublisherType");

        PublisherAccount publisher = new PublisherAccountGeneratorFactory(publisherAccountTestFactory, internalAccount, publisherAccountType)
                .findOrCreate(NAME_PREFIX + "PublisherAccount");

        User publisherUser = userGeneratorFactory.findOrCreate(NAME_PREFIX + "PublisherUser", publisher);

        Site site = new SiteGeneratorFactory(siteTestFactory, publisher)
                .findOrCreate(NAME_PREFIX + "Site");

        Tag tag = new TagGeneratorFactory(tagsTestFactory, site, size)
                .findOrCreate(NAME_PREFIX + "Tag");

        ConversionGeneratorFactory conversionTestFactory = new ConversionGeneratorFactory(actionTestFactory, advertiser);
        Action conversion = conversionTestFactory.findOrCreate(NAME_PREFIX + "Conversion");
        Action deletedConversion = conversionTestFactory.findOrCreate(NAME_PREFIX + "DeletedConversion");
        if (!deletedConversion.getStatus().equals(Status.DELETED)) {
            actionTestFactory.delete(deletedConversion.getId());
        }

        Campaign textCampaign = new TextCampaignGeneratorFactory(textCampaignTestFactory, advertiser)
                .findOrCreate(NAME_PREFIX + "TextCampaign");

        CampaignCreativeGroup textGroup = new TextGroupGeneratorFactory(textCCGTestFactory, textCampaign, conversion)
                .findOrCreate(NAME_PREFIX + "TextCampaignGroup");
        if (textGroup.getCcgKeywords().isEmpty()) {
            ccgKeywordService.update(generateKeywords(), textGroup.getId(), textGroup.getVersion());
        }

        Campaign displayCampaign = new DisplayCampaignGeneratorFactory(displayCampaignTestFactory, advertiser)
                .findOrCreate(NAME_PREFIX + "DisplayCampaign");

        CampaignCreativeGroup displayGroup = new DisplayGroupGeneratorFactory(displayCCGTestFactory, displayCampaign, conversion)
                .findOrCreate(NAME_PREFIX + "DisplayCampaignGroup");

        DisplayCreativeGeneratorFactory displayCreativeGeneratorFactory = new DisplayCreativeGeneratorFactory(
                displayCreativeTestFactory, advertiser, size, template, visualCategory, contentCategory);
        Creative displayCreative = displayCreativeGeneratorFactory.findOrCreate(NAME_PREFIX + "DisplayCreative");
        Creative displayCreativeForLink = displayCreativeGeneratorFactory.findOrCreate(NAME_PREFIX + "DisplayCreativeForLink");
        Creative siteCreative = displayCreativeGeneratorFactory.findOrCreate(NAME_PREFIX + "SiteCreative");
        int creativeCrowdSize = 10;
        List<Creative> crowdSceneCreatives = new ArrayList<>(creativeCrowdSize);
        for (int i = 0; i < creativeCrowdSize; i++) {
            crowdSceneCreatives.add(displayCreativeGeneratorFactory.findOrCreate(NAME_PREFIX + "SiteCreative-" + i));
        }

        DisplayCreativeLinkGeneratorFactory displayCreativeLinkGeneratorFactory = new DisplayCreativeLinkGeneratorFactory(displayCreativeLinkTestFactory, displayGroup);
        CampaignCreative creativeLink = displayCreativeLinkGeneratorFactory.findOrCreate(NAME_PREFIX + "CreativeLink", displayCreativeForLink);
        CampaignCreative siteCreativeLink = displayCreativeLinkGeneratorFactory.findOrCreate(NAME_PREFIX + "SiteCreativeLink", siteCreative);
        for (Creative creative : crowdSceneCreatives) {
            displayCreativeLinkGeneratorFactory.findOrCreate("", creative);
        }

        CategoryChannelGeneratorFactory categoryChannelGeneratorFactory = new CategoryChannelGeneratorFactory(categoryChannelTestFactory, internalAccount);
        CategoryChannel categoryChannel1 = categoryChannelGeneratorFactory.findOrCreate(NAME_PREFIX + "CategoryChannel1");
        CategoryChannel categoryChannel2 = categoryChannelGeneratorFactory.findOrCreate(NAME_PREFIX + "CategoryChannel2");

        BehavioralChannel behavioralChannel = new BehavioralChannelGeneratorFactory(behavioralChannelTestFactory, behavioralChannelService, agency)
                .findOrCreate(NAME_PREFIX + "BehavioralChannel");

        AudienceChannel audienceChannel = new AudienceChannelGeneratorFactory(audienceChannelTestFactory, internalAccount)
                .findOrCreate(NAME_PREFIX + "AudienceChannel");

        OptionGroup templOptionGroup = new OptionGroupGeneratorFactory(optionGroupTestFactory, null, template)
                .findOrCreate(NAME_PREFIX + "TemplOptionGroup");
        Option stringOption = new OptionGeneratorFactory(optionTestFactory, templOptionGroup)
                .findOrCreate(NAME_PREFIX + "TemplStringOption", OptionType.STRING);
        Option integerOption = new OptionGeneratorFactory(optionTestFactory, templOptionGroup)
                .findOrCreate(NAME_PREFIX + "TemplIntegerOption", OptionType.INTEGER);
        Option enumOption = new OptionGeneratorFactory(optionTestFactory, templOptionGroup)
                .findOrCreate(NAME_PREFIX + "TemplEnumOption", OptionType.ENUM);

        OptionGroup sizeOptionGroup = new OptionGroupGeneratorFactory(optionGroupTestFactory, size, null)
                .findOrCreate(NAME_PREFIX + "SizeOptionGroup");
        Option sizeStringOption = new OptionGeneratorFactory(optionTestFactory, sizeOptionGroup)
                .findOrCreate(NAME_PREFIX + "sizeStringOption", OptionType.STRING);

        AccountType ispAccountType = new IspAccountTypeGeneratorFactory(ispAccountTypeTestFactory)
                .findOrCreate("IspAccountType");

        IspAccount ispAccount = new IspAccountGeneratorFactory(ispAccountTestFactory, ispAccountType, internalAccount)
                .findOrCreate("IspAccount");

        Colocation colocation = new ColocationGeneratorFactory(colocationTestFactory, ispAccount)
                .findOrCreate("Colocation");


        UserCredential adminCredential = userGeneratorFactory.findOrCreateRsUserCredential(admin);
        properties.setProperty("foros.userToken", adminCredential.getRsToken());
        properties.setProperty("foros.key", adminCredential.getRsKeyBase64());

        properties.setProperty("foros.advertiser.userToken", agencyUser.getUserCredential().getRsToken());
        properties.setProperty("foros.advertiser.key", agencyUser.getUserCredential().getRsKeyBase64());

        properties.setProperty("foros.publisher.userToken", publisherUser.getUserCredential().getRsToken());
        properties.setProperty("foros.publisher.key", publisherUser.getUserCredential().getRsKeyBase64());

        properties.setProperty("foros.test.internal.id", admin.getAccount().getId().toString());
        properties.setProperty("foros.test.advertiser.id", advertiser.getId().toString());
        properties.setProperty("foros.test.agency.id", agency.getId().toString());
        properties.setProperty("foros.test.user.id", agencyUser.getId().toString());
        properties.setProperty("foros.test.campaign.id", textCampaign.getId().toString());
        properties.setProperty("foros.test.creativeGroup.id", textGroup.getId().toString());
        properties.setProperty("foros.test.displayGroup.id", displayGroup.getId().toString());
        properties.setProperty("foros.test.creative.id", displayCreative.getId().toString());
        properties.setProperty("foros.test.site.id", site.getId().toString());
        properties.setProperty("foros.test.tag.id", tag.getId().toString());
        properties.setProperty("foros.test.site.creative.id", siteCreative.getId().toString());
        properties.setProperty("foros.test.conversion.id", conversion.getId().toString());

        properties.setProperty("foros.text.creativeLinkId", creativeLink.getId().toString());

        properties.setProperty("foros.test.category.create", categoryChannel1.getId().toString());
        properties.setProperty("foros.test.category.update", categoryChannel2.getId().toString());
        properties.setProperty("foros.test.channel.advertising.id", behavioralChannel.getId().toString());
        properties.setProperty("foros.test.channel.ids", behavioralChannel.getId().toString());
        properties.setProperty("foros.test.device.mobileChannel.id", deviceChannelTestFactory.getMobileDeviceChannel().getId().toString());
        properties.setProperty("foros.test.device.nonMobileChannel.id", deviceChannelTestFactory.getNonMobileDeviceChannel().getId().toString());
        properties.setProperty("foros.test.channel.audience.id", audienceChannel.getId().toString());

        properties.setProperty("foros.test.template.id", template.getId().toString());
        properties.setProperty("foros.test.template.option.string.id", stringOption.getId().toString());
        properties.setProperty("foros.test.template.option.integer.id", integerOption.getId().toString());
        properties.setProperty("foros.test.template.option.enum.id", enumOption.getId().toString());
        properties.setProperty("foros.test.template.option.enum.value", enumOption.getValues().iterator().next().getValue());

        properties.setProperty("foros.test.template.text.id", creativeTemplateTestFactory.findText().getId().toString());
        properties.setProperty("foros.test.template.text.option.headline.id", optionService.findByTokenFromTextTemplate("HEADLINE").getId().toString());
        properties.setProperty("foros.test.template.text.option.descriptionLine1.id", optionService.findByTokenFromTextTemplate("DESCRIPTION1").getId().toString());
        properties.setProperty("foros.test.template.text.option.descriptionLine2.id", optionService.findByTokenFromTextTemplate("DESCRIPTION2").getId().toString());
        properties.setProperty("foros.test.template.text.option.displayUrl.id", optionService.findByTokenFromTextTemplate("DISPLAY_URL").getId().toString());
        properties.setProperty("foros.test.template.text.option.clickUrl.id", optionService.findByTokenFromTextTemplate("CRCLICK").getId().toString());

        properties.setProperty("foros.test.size.id", size.getId().toString());
        properties.setProperty("foros.test.size.text.id", creativeSizeTestFactory.findText().getId().toString());

        properties.setProperty("foros.test.category.content.id", contentCategory.getId().toString());
        properties.setProperty("foros.test.category.visual.id", visualCategory.getId().toString());

        properties.setProperty("foros.test.colocation.id", colocation.getId().toString());
    }

    private Collection<CCGKeyword> generateKeywords() {
        List<CCGKeyword> result = new ArrayList<>();

        CCGKeyword keyword = new CCGKeyword();
        keyword.setOriginalKeyword(NAME_PREFIX + "OrigKwd");
        keyword.setMaxCpcBid(new BigDecimal(0.02));
        keyword.setTriggerType(KeywordTriggerType.PAGE_KEYWORD);
        keyword.setClickURL("http://google.ru");

        result.add(keyword);

        return result;
    }

    private static class OrderedProperties extends Properties {
        private List<String> keys = new ArrayList<>();

        @Override
        public synchronized Object put(Object key, Object value) {
            Object result = super.put(key, value);
            if (result == null) {
                keys.add(key.toString());
            }
            return result;
        }

        @Override
        public synchronized Object remove(Object key) {
            throw new IllegalArgumentException();
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return new IteratorEnumeration(keys.iterator());
        }

    }
}
