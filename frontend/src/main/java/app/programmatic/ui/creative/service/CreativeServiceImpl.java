package app.programmatic.ui.creative.service;

import app.programmatic.ui.creative.tool.CreativeHelper;
import app.programmatic.ui.fileNew.service.FileServiceCreative;
import com.foros.rs.client.model.advertising.AdvertiserLink;
import com.foros.rs.client.model.advertising.campaign.AdOption;
import com.foros.rs.client.model.advertising.campaign.CreativeSelector;
import com.foros.rs.client.model.advertising.template.*;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.foros.service.ForosCreativeService;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.creative.dao.model.*;
import app.programmatic.ui.creative.dao.model.CreativeTemplate;
import app.programmatic.ui.creative.tool.CreativeBuilder;
import app.programmatic.ui.creative.tool.CreativeTemplateBuilder;
import app.programmatic.ui.localization.service.LocalizationService;
import app.programmatic.ui.file.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

@Service
public class CreativeServiceImpl implements CreativeService {
    private static final Logger logger = LoggerFactory.getLogger(CreativeServiceImpl.class);

    public static final String TEXT_SIZE_TEMPLATE = "Text";
    public static final String JS_APP_FORMAT = "js";
    public static final String HTML_APP_FORMAT = "html";
    public static final String VISUAL_CATEGORY_TYPE_NAME = "Visual";
    public static final String CONTENT_CATEGORY_TYPE_NAME = "Content";
    private static final String[] CREATIVE_IMAGE_FILE_TYPES = {"image/bmp", "image/gif", "image/jpeg", "image/png"};
    private static final String HTML_FILE_TYPES = "text/html";
    private static final String IMAGE_FILE_TOKEN = "ADIMAGE";
    private static final String CR_HTML_TOKEN = "CRHTML";
    private static final String ALT_TEXT_TOKEN = "ALTTEXT";
    private static final String CLICK_URL_TOKEN = "CRCLICK";
    private static final String LANDING_PAGE_URL_TOKEN = "DESTURL";
    private static final String CRADVTRACKPIXEL_TOKEN = "CRADVTRACKPIXEL";
    private static final String CRADVTRACKPIXEL2_TOKEN = "CRADVTRACKPIXEL2";

    @Value("${staticresource.url}")
    private String staticResourceUrl;
    @Value("${staticresource.creativesPath}")
    private String creativesPath;

    @Autowired
    private ForosCreativeService forosService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileServiceCreative fileServiceCreative;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    private JdbcOperations jdbcOperations;

    private Long textSizeId;
    private Long textTemplateId;
    private ContentType visualCategoryType;
    private ContentType contentCategoryType;


    @Override
    public Creative find(Long id) {
        CreativeSelector selector = new CreativeSelector();
        selector.setCreativeIds(Collections.singletonList(id));

        Result<com.foros.rs.client.model.advertising.campaign.Creative> rsResult =
                forosService.getCreativeService().get(selector);
        if (rsResult.getEntities().isEmpty()) {
            return null;
        }
        com.foros.rs.client.model.advertising.campaign.Creative forosCreative = rsResult.getEntities().get(0);

        CreativeSizeStat size = findDisplaySizes(Collections.singleton(forosCreative.getSize().getId())).get(0);
        CreativeTemplateStat template = findDisplayTemplates(Collections.singleton(forosCreative.getTemplate().getId())).get(0);

        Creative result = new Creative();
        CreativeBuilder.fillCreativeStatFields(result, forosCreative, size.getName(), template.getName());

        result.setVersion(XmlDateTimeConverter.convertToEpochTime(forosCreative.getUpdated()));
        result.setOptions(forosCreative.getOptions());

        if (forosCreative.getCategories().isEmpty()) {
            result.setContentCategories(Collections.emptyList());
            result.setVisualCategories(Collections.emptyList());
        } else {
            CreativeCategories creativeCategories = getCreativeCategories(forosCreative.getCategories().stream()
                            .map(c -> c.getId())
                            .collect(Collectors.toList()),
                    null);
            result.setContentCategories(creativeCategories.getContentCategories());
            result.setVisualCategories(creativeCategories.getVisualCategories());
        }

        return localizationService.processCreative(result);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.creative.validation.ForosCreativeViolationsServiceImpl")
    public Long create(Creative creative) {
        return createOrUpdate(creative);
    }

    @Override
    @Transactional
    @ForosApiViolationsAware("app.programmatic.ui.creative.validation.ForosCreativeViolationsServiceImpl")
    public Long update(Creative creative) {
        logger.info("Start update Creative with creativeId = {}", creative.getId());

        Creative oldCreative = find(creative.getId());
        if (ObjectUtils.notEqual(creative.getId(), oldCreative.getId())) {
            logger.warn("Entity Creative with creativeId = {}, not found", creative.getId());
            return null;
        }

        if (ObjectUtils.notEqual(creative.getName(), oldCreative.getName())) {
            Long result = CreativeHelper.updateOnlyName(creative, oldCreative, jdbcOperations);
            if (result == null) {
                logger.warn("Update Creative with creativeId (Only name) = {} failed", creative.getId());
                return null;
            } else if (result.equals(creative.getId())) {
                return creative.getId();
            }
        } else if (ObjectUtils.notEqual(creative.getDisplayStatus(), oldCreative.getDisplayStatus())) {
            Long result = CreativeHelper.updateOnlyStatus(creative, oldCreative, jdbcOperations);
            if (result == null) {
                logger.warn("Update Creative with creativeId (Only status) = {} failed", creative.getId());
                return null;
            } else if (result.equals(creative.getId())) {
                return creative.getId();
            }
        }

        logger.info("Update Creative with creativeId (All parameters) = {}", creative.getId());

        return createOrUpdate(creative);
    }

    private Long createOrUpdate(Creative creative) {
        return createOrUpdate(CreativeBuilder.buildForosCreative(creative));
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.creative.validation.ForosCreativesUploadViolationsServiceImpl")
    public List<Long> uploadCreatives(CreativeUpload creativeUpload) {
        List<Operation<com.foros.rs.client.model.advertising.campaign.Creative>> operationList = new ArrayList<>();

        Long accountId = creativeUpload.getAccountId();
        AdvertiserLink account = ForosHelper.createAdvertiserLink(accountId);

        CreativeTemplate imageTemplate = findTemplate(accountId, CreativeTemplateNames.IMAGE.getName());
        EntityLink templateLink = ForosHelper.createEntityLink(imageTemplate.getId());

        Option imageFileOption = null;
        Option altTextOption = null;
        Option clickUrlOption = null;
        Option landingPageUrlOption = null;
        Option crAdvTrackPixel = null;
        Option crAdvTrackPixel2 = null;
        for (OptionGroup optionGroup : imageTemplate.getOptionGroups()) {
            for (Option option : optionGroup.getOptions()) {
                if (IMAGE_FILE_TOKEN.equals(option.getToken())) {
                    imageFileOption = option;
                }
                if (ALT_TEXT_TOKEN.equals(option.getToken())) {
                    altTextOption = option;
                }
                if (CLICK_URL_TOKEN.equals(option.getToken())) {
                    clickUrlOption = option;
                }
                if (LANDING_PAGE_URL_TOKEN.equals(option.getToken())) {
                    landingPageUrlOption = option;
                }
                if (CRADVTRACKPIXEL_TOKEN.equals(option.getToken())) {
                    crAdvTrackPixel = option;
                }
                if (CRADVTRACKPIXEL2_TOKEN.equals(option.getToken())) {
                    crAdvTrackPixel2 = option;
                }
            }
        }

        List<CreativeSizeStat> sizes = getDisplaySizes(accountId, imageTemplate.getId());

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.addAll(creativeUpload.getCategories());
        categoryIds.addAll(imageTemplate.getVisualCategories().stream()
                .map(CreativeCategory::getId)
                .collect(Collectors.toList()));
        List<EntityLink> categories = categoryIds.stream()
                .map(categoryId -> ForosHelper.createEntityLink(categoryId))
                .collect(Collectors.toList());

        for (CreativeImage creativeImage : creativeUpload.getImagesList()) {
            Operation<com.foros.rs.client.model.advertising.campaign.Creative> operation = new Operation<>();

            com.foros.rs.client.model.advertising.campaign.Creative creative =
                    new com.foros.rs.client.model.advertising.campaign.Creative();

            creative.setAccount(account);
            creative.setName(creativeImage.getName());
            creative.setTemplate(templateLink);

            Optional<Long> sizeId = sizes.stream()
                    .filter(size -> size.getWidth().equals(creativeImage.getDimensions().getWidth()) &&
                            size.getHeight().equals(creativeImage.getDimensions().getHeight()))
                    .map(size -> size.getId())
                    .findFirst();

            if (!sizeId.isPresent()) {
                continue;
            }

            creative.setSize(ForosHelper.createEntityLink(sizeId.get()));

            List<AdOption> options = new ArrayList<>(4);
            options.add(createAdOption(imageFileOption.getId(), imageFileOption.getToken(), creativeImage.getPath()));
            options.add(createAdOption(altTextOption.getId(), altTextOption.getToken(), creativeUpload.getAltText()));
            options.add(createAdOption(clickUrlOption.getId(), altTextOption.getToken(), creativeUpload.getClickUrl()));
            options.add(createAdOption(landingPageUrlOption.getId(), altTextOption.getToken(), creativeUpload.getLandingPageUrl()));
            if (crAdvTrackPixel != null) {
                options.add(createAdOption(crAdvTrackPixel.getId(), altTextOption.getToken(), creativeUpload.getCrAdvTrackPixel()));
            }
            if (crAdvTrackPixel2 != null) {
                options.add(createAdOption(crAdvTrackPixel2.getId(), altTextOption.getToken(), creativeUpload.getCrAdvTrackPixel2()));
            }
            creative.setOptions(options);

            creative.setWidth(creativeImage.getDimensions().getWidth());
            creative.setHeight(creativeImage.getDimensions().getHeight());
            creative.setStatus(Status.ACTIVE);
            creative.setCategories(categories);

            operation.setEntity(creative);
            operation.setType(OperationType.CREATE);
            operationList.add(operation);
        }

        Operations<com.foros.rs.client.model.advertising.campaign.Creative> operations = new Operations<>();
        operations.setOperations(operationList);

        return forosService.getCreativeService().perform(operations).getIds();
    }


    @Override
    public List<Long> uploadCreatives(CreativeUploadHtml creativeUploadHtml) {
        List<Long> creativeIds = new ArrayList<>();
        Long accountId = creativeUploadHtml.getAccountId();
        CreativeTemplate htmlTemplate = findTemplate(accountId, CreativeTemplateNames.HTML.getName());

        Option crHtmlOption = null;
        Option clickUrlOption = null;
        Option landingPageUrlOption = null;
        Option crAdvTrackPixel = null;

        for (OptionGroup optionGroup : htmlTemplate.getOptionGroups()) {
            for (Option option : optionGroup.getOptions()) {
                if (CR_HTML_TOKEN.equals(option.getToken())) {
                    crHtmlOption = option;
                }
                if (CLICK_URL_TOKEN.equals(option.getToken())) {
                    clickUrlOption = option;
                }
                if (LANDING_PAGE_URL_TOKEN.equals(option.getToken())) {
                    landingPageUrlOption = option;
                }
                if (CRADVTRACKPIXEL_TOKEN.equals(option.getToken())) {
                    crAdvTrackPixel = option;
                }
            }
        }

        List<CreativeSizeStat> sizes = getDisplaySizes(accountId, htmlTemplate.getId());
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.addAll(creativeUploadHtml.getCategories());
        categoryIds.addAll(htmlTemplate.getVisualCategories().stream()
                .map(CreativeCategory::getId)
                .collect(Collectors.toList()));
        CreativeCategories creativeCategories = getCreativeCategories(categoryIds, null);

        for (CreativeImage creativeImage : creativeUploadHtml.getImagesList()) {
            try {
                Optional<Long> sizeId = sizes.stream()
                        .filter(size -> size.getWidth().equals(creativeImage.getDimensions().getWidth()) &&
                                size.getHeight().equals(creativeImage.getDimensions().getHeight()))
                        .map(size -> size.getId())
                        .findFirst();
                if (!sizeId.isPresent()) {
                    continue;
                }
                List<AdOption> options = new ArrayList<>(3);

                options.add(createAdOption(clickUrlOption.getId(), clickUrlOption.getToken(), creativeUploadHtml.getClickUrl()));
                options.add(createAdOption(landingPageUrlOption.getId(), landingPageUrlOption.getToken(), creativeUploadHtml.getLandingPageUrl()));
                if (crAdvTrackPixel != null) {
                    options.add(createAdOption(crAdvTrackPixel.getId(), crAdvTrackPixel.getToken(), creativeUploadHtml.getCrAdvTrackPixel()));
                }
                Creative creative = new Creative();
                creative.setAccountId(accountId);
                creative.setName(creativeImage.getName());
                creative.setTemplateId(htmlTemplate.getId());
                creative.setWidth(creativeImage.getDimensions().getWidth());
                creative.setHeight(creativeImage.getDimensions().getHeight());
                creative.setDisplayStatus(MajorDisplayStatus.INACTIVE);
                creative.setContentCategories(creativeCategories.getContentCategories());
                creative.setVisualCategories(creativeCategories.getVisualCategories());
                creative.setSizeId(sizeId.get());
                creative.setOptions(options);
                Long creativeId = create(creative);

                Creative createdCreative = find(creativeId);

                File file = new File(creativeImage.getPath());
                String pathDir = file.getParent();
                String newParentDir = fileServiceCreative.createParentFilePath(createdCreative.getAgencyId(), accountId, creativeId);
                fileServiceCreative.moveFiles(pathDir, newParentDir);
                String newFilePath = newParentDir + "/" + file.getName();

                options.add(createAdOption(crHtmlOption.getId(), crHtmlOption.getToken(), newFilePath));
                createdCreative.setOptions(options);

                //TODO раскоментировать и удалить creativeIds.add(creativeId); когда будут принимать ссылки (и сохраняться на сервер)
                Long updatedId = update(createdCreative);
                creativeIds.add(updatedId);

                //creativeIds.add(creativeId);
            } catch (IOException e) {
                //TODO пока не заполняем, зависит от дальнейшего развития креативов, возможно сюда будет складывать
                //TODO неудачные креативы (которые по какой-то ошибке не смогли загрузиться)
                System.out.println(e.getMessage());
            }
        }

        return creativeIds;
    }


    private static AdOption createAdOption(Long id, String token, String value) {
        AdOption result = new AdOption();
        result.setId(id);
        result.setToken(token);
        result.setValue(value);
        return result;
    }

    @Override
    public CreativeTemplate findTemplate(Long accountId, String templateName) {
        try {
            Long templateId = jdbcOperations.queryForObject(
                    "select t.template_id " +
                            "from " +
                            "accounttypecreativetemplate atct " +
                            "inner join template t using(template_id) " +
                            "inner join account a on a.account_id = ? " +
                            "left join account ag on ag.account_id = a.agency_account_id " +
                            "where " +
                            "atct.account_type_id = coalesce(a.account_type_id, ag.account_type_id) " +
                            "and t.template_type = 'CREATIVE' " +
                            "and t.name = ?",
                    new Object[]{accountId, templateName},
                    Long.class);
            return findDisplayTemplate(templateId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public List<CreativeSizeStat> getDisplaySizes(Long accountId, Long templateId) {

        List<CreativeSizeStat> result = new ArrayList<>();
        Array appNames = jdbcOperations.execute((Connection con) -> con.createArrayOf("varchar", new String[]{JS_APP_FORMAT, HTML_APP_FORMAT}));
        jdbcOperations.query(
                "select distinct cs.size_id, cs.name, cs.width, cs.height " +
                        "from accounttypecreativesize atcs " +
                        "  inner join creativesize cs using(size_id) " +
                        "  inner join account a on a.account_id = ?" +
                        "  left join account ag on ag.account_id = a.agency_account_id " +
                        "  inner join templatefile tf on tf.size_id = cs.size_id " +
                        "  inner join appformat af using(app_format_id) " +
                        "where atcs.account_type_id = coalesce(a.account_type_id, ag.account_type_id)" +
                        "  and tf.template_id = ? and af.name = any(?) " +
                        "order by cs.name",
                new Object[]{accountId, templateId, appNames},
                (ResultSet rs) -> {
                    result.add(new CreativeSizeStat(
                            rs.getLong("size_id"),
                            rs.getString("name"),
                            rs.getLong("width"),
                            rs.getLong("height")));
                }
        );

        return localizationService.processSizesStats(result);
    }

    private List<CreativeSizeStat> findDisplaySizes(Set<Long> sizeIds) {
        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", sizeIds.toArray()));
        return jdbcOperations.query("select size_id, name, width, height from creativesize where size_id = any(?)",
                new Object[]{idsArray},
                (ResultSet rs, int index) -> new CreativeSizeStat(
                        rs.getLong("size_id"),
                        rs.getString("name"),
                        rs.getLong("width"),
                        rs.getLong("height")));
    }

    private List<CreativeTemplateStat> findDisplayTemplates(Set<Long> templateIds) {
        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", templateIds.toArray()));
        return jdbcOperations.query("select template_id, name from template where template_id = any(?)",
                new Object[]{idsArray},
                (ResultSet rs, int index) -> new CreativeTemplateStat(rs.getLong("template_id"), rs.getString("name")));
    }

    private static boolean isJsOrHtmlFormatSupported(TemplateFile templateFile) {
        return JS_APP_FORMAT.equals(templateFile.getApplicationFormat().getName()) ||
                HTML_APP_FORMAT.equals(templateFile.getApplicationFormat().getName());
    }

    @Override
    public CreativeSize findDisplaySize(Long id) {
        CreativeSizeSelector selector = new CreativeSizeSelector();
        selector.setId(id);

        CreativeSize result = forosService.getAdminSizeService().get(selector);
        result.setOptionGroups(CreativeTemplateBuilder.filterOptionGroups(result.getOptionGroups()));

        return localizationService.processSize(result);
    }

    @Override
    public List<CreativeTemplateStat> getAccountDisplayTemplates(Long accountId) {
        List<CreativeTemplateStat> result = jdbcOperations.query(
                "select * from statqueries.get_account_templates_with_sizes(?::int)",
                new Object[]{accountId},
                (ResultSet rs, int ind) -> new CreativeTemplateStat(rs.getLong("template_id"), rs.getString("template_name")));

        return localizationService.processTemplatesStats(result);
    }

    @Override
    public CreativeTemplate findDisplayTemplate(Long id) {
        com.foros.rs.client.model.advertising.template.CreativeTemplate forosTemplate = getForosDisplayTemplate(id);

        CreativeTemplate result = CreativeTemplateBuilder.buildFromForosTemplate(forosTemplate);

        if (forosTemplate.getCategories().isEmpty()) {
            result.setVisualCategories(Collections.emptyList());
        } else {
            CreativeCategories creativeCategories = getCreativeCategories(forosTemplate.getCategories().stream()
                            .map(t -> t.getId())
                            .collect(Collectors.toList()),
                    visualCategoryType.getId());
            result.setVisualCategories(creativeCategories.getVisualCategories());
        }

        return localizationService.processTemplate(result);
    }

    private com.foros.rs.client.model.advertising.template.CreativeTemplate getForosDisplayTemplate(Long id) {
        CreativeTemplateSelector selector = new CreativeTemplateSelector();
        selector.setId(id);
        return forosService.getAdminTemplateService().get(selector);
    }

    @Override
    public CreativeCategories getCreativeCategories() {
        return getCreativeCategories(Collections.emptyList(), null);
    }

    private CreativeCategories getCreativeCategories(List<Long> ids, Integer typeId) {
        List<CreativeCategory> contentCategories = new ArrayList<>(ids.isEmpty() ? 500 : ids.size());
        List<CreativeCategory> visualCategories = new ArrayList<>(ids.isEmpty() ? 500 : ids.size());

        ArrayList<Array> params = new ArrayList<>(2);
        List<Integer> typeIds = typeId != null ? Collections.singletonList(typeId) :
                Arrays.asList(visualCategoryType.getId(), contentCategoryType.getId());
        params.add(jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", typeIds.toArray())));
        if (!ids.isEmpty()) {
            params.add(jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray())));
        }

        String sql = String.format("select creative_category_id, name, cct_id as type_id from creativecategory cc " +
                        "  where cct_id = any(?) %s" +
                        "  order by type_id, name",
                ids.isEmpty() ? "" : " and creative_category_id = any(?) ");

        jdbcOperations.query(sql,
                params.toArray(),
                (ResultSet rs, int ind) -> {
                    CreativeCategory category = new CreativeCategory(rs.getLong("creative_category_id"), rs.getString("name"));
                    Integer type = rs.getInt("type_id");
                    if (type.equals(contentCategoryType.getId())) {
                        contentCategories.add(category);
                    } else if (type.equals(visualCategoryType.getId())) {
                        visualCategories.add(category);
                    } else {
                        throw new RuntimeException("Unreachable code");
                    }

                    return null;
                });

        return new CreativeCategories(contentCategories, visualCategories);
    }

    @Override
    public List<CreativeCategory> getLinkedContentCategories(Long accountId) {
        List<CreativeCategory> result = jdbcOperations.query(
                "select cc.creative_category_id as id, cc.name from creativecategory_account cca " +
                        "  inner join creativecategory cc using(creative_category_id) " +
                        "  where account_id = ?",
                new Object[]{accountId},
                (ResultSet rs, int ind) ->
                        new CreativeCategory(rs.getLong("id"), rs.getString("name"))
        );

        return localizationService.processCategories(result);
    }

    @Override
    public List<CreativeStat> getDisplayCreatives(Long accountId, int limit) {
        CreativeSelector selector = new CreativeSelector();
        selector.setAdvertiserIds(Collections.singletonList(accountId));
        selector.setExcludedSizeIds(Arrays.asList(textSizeId));
        selector.setExcludedTemplateIds(Arrays.asList(textTemplateId));
        selector.setPaging(createPaging(limit));
        selector.setCreativeStatuses(Arrays.asList(Status.ACTIVE, Status.INACTIVE));

        List<com.foros.rs.client.model.advertising.campaign.Creative> entities = forosService.getCreativeService().get(selector).getEntities();
        Collections.sort(entities, (c1, c2) -> c2.getUpdated().compare(c1.getUpdated()));
        List<CreativeStat> result = transformForosCreatives(entities);
        return localizationService.processCreativesStats(result);
    }

    @Override
    public List<CreativeStat> getDisplayCreativesByIds(List<Long> ids, int limit) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        CreativeSelector selector = new CreativeSelector();
        selector.setCreativeIds(ids);
        selector.setPaging(createPaging(limit));
        selector.setCreativeStatuses(Arrays.asList(Status.ACTIVE, Status.INACTIVE));

        return transformForosCreatives(forosService.getCreativeService().get(selector).getEntities());
    }

    private List<CreativeStat> transformForosCreatives(List<com.foros.rs.client.model.advertising.campaign.Creative> forosCreatives) {
        Map<Long, String> sizeNameById = findDisplaySizes(
                forosCreatives.stream().map(c -> c.getSize().getId()).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(size -> size.getId(), size -> size.getName()));
        Map<Long, String> templateNameById = findDisplayTemplates(
                forosCreatives.stream().map(c -> c.getTemplate().getId()).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(template -> template.getId(), template -> template.getName()));

        return forosCreatives.stream()
                .map(forosCreative -> CreativeBuilder.fillCreativeStatFields(
                        new CreativeStat(),
                        forosCreative,
                        sizeNameById.get(forosCreative.getSize().getId()),
                        templateNameById.get(forosCreative.getTemplate().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public LivePreviewResult preview(Long creativeId) {
        return forosService.getCreativeService().preview(creativeId);
    }

    @Override
    public LivePreviewResult livePreview(Creative creative) {
        return forosService.getCreativeService().livePreview(CreativeBuilder.buildForosCreative(creative));
    }

    @Override
    public MajorDisplayStatus changeStatus(Long creativeId, StatusOperation operation) {
        com.foros.rs.client.model.advertising.campaign.Creative creative = new com.foros.rs.client.model.advertising.campaign.Creative();
        creative.setId(creativeId);
        ForosHelper.changeEntityStatus(creative, ForosHelper.isChangeStatusOperation(operation));
        createOrUpdate(creative);
        return findCreativeStatus(creativeId);
    }

    private Long createOrUpdate(com.foros.rs.client.model.advertising.campaign.Creative creative) {
        Operation<com.foros.rs.client.model.advertising.campaign.Creative> operation = new Operation<>();
        operation.setEntity(creative);
        operation.setType(creative.getId() == null ? OperationType.CREATE : OperationType.UPDATE);

        Operations<com.foros.rs.client.model.advertising.campaign.Creative> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(operation));

        return forosService.getCreativeService().perform(operations).getIds().get(0);
    }

    private MajorDisplayStatus findCreativeStatus(Long creativeId) {
        Integer displayStatusId = jdbcOperations.queryForObject(
                "select display_status_id from creative where creative_id = ?",
                new Object[]{creativeId},
                Integer.class);
        return CreativeDisplayStatus.valueOf(displayStatusId).getMajorStatus();
    }

    @Override
    public FileUrl upload(MultipartFile file, Long accountId) {
        try {
            AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
            String fileName = fileService.uploadToCreativesRoot(file, accountId, "");
            return getFileUrl(account, fileName);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.creative.validation.ForosCreativesUploadViolationsServiceImpl")
    public List<CreativeImage> uploadZip(MultipartFile file, Long accountId) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
            String parentDir = fileService.uploadZipToCreativesRoot(file, accountId);

            ZipEntry zipEntry = zipStream.getNextEntry();
            if (zipEntry == null) {
                // check that it is zip
                return null;
            }

            List<CreativeImage> result = new ArrayList<>();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && !zipEntry.getName().contains("__MACOSX")) {
                    String mimeType = URLConnection.guessContentTypeFromName(zipEntry.getName());
                    if (Arrays.asList(CREATIVE_IMAGE_FILE_TYPES).contains(mimeType)) {
                        String fileName = !zipEntry.getName().contains(".") ? zipEntry.getName() : zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf('.'));
                        CreativeImage.Dimensions dimensions = getImageDimension(zipStream, mimeType);
                        result.add(new CreativeImage(fileName, parentDir + "/" + zipEntry.getName(), dimensions));
                    }
                }
                zipEntry = zipStream.getNextEntry();
            }

            Collections.sort(result, (c1, c2) -> c1.getName().compareTo(c2.getName()));
            return result;

        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CreativeImage> uploadZipHtml(MultipartFile file, Long accountId) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {

            AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
            String parentDir = fileServiceCreative.uploadZip(file, account.getAgencyId(), accountId, 0L);
            ZipEntry zipEntry = zipStream.getNextEntry();
            if (zipEntry == null) {
                return null;
            }

            List<CreativeImage> result = new ArrayList<>();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && !zipEntry.getName().contains("__MACOSX")) {
                    String mimeType = URLConnection.guessContentTypeFromName(zipEntry.getName());
                    if (HTML_FILE_TYPES.equals(mimeType)) {
                        String[] dirs = zipEntry.getName().split("/");
                        if (dirs[0] != null && dirs[1] != null) {
                            String name = dirs[1];
                            String[] dimensionsArr = dirs[0].split("x");
                            try {
                                Long width = Long.parseLong(dimensionsArr[0]);
                                Long height = Long.parseLong(dimensionsArr[1]);
                                CreativeImage.Dimensions dimensions = new CreativeImage.Dimensions(width, height);
                                String fileName = !name.contains(".") ? name : name.substring(0, name.lastIndexOf('.'));
                                result.add(new CreativeImage(fileName, parentDir + "/" + zipEntry.getName(), dimensions));
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
                zipEntry = zipStream.getNextEntry();
            }

            Collections.sort(result, (c1, c2) -> c1.getName().compareTo(c2.getName()));
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CreativeImage.Dimensions getImageDimension(ZipInputStream zis, String mimeType) throws IOException {
        CreativeImage.Dimensions result = null;
        Iterator<ImageReader> iter = ImageIO.getImageReadersByMIMEType(mimeType);

        if (iter.hasNext()) {
            ImageInputStream stream = null;
            ImageReader reader = iter.next();
            try {
                stream = ImageIO.createImageInputStream(zis);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new CreativeImage.Dimensions(new Long(width), new Long(height));
            } finally {
                if (stream != null) {
                    stream.close();
                }
                reader.dispose();
            }
        }

        return result;
    }

    @Override
    public Boolean checkFileExist(MultipartFile file, Long accountId) {
        try {
            return fileService.checkExist(file, accountId);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileUrl getFileBaseUrl(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return getFileUrl(account, null);
    }

    private FileUrl getFileUrl(AdvertisingAccount account, String fileName) {
        String url = staticResourceUrl
                + "/" + creativesPath
                + "/" + (account.getAgencyId() != null ? account.getAgencyId().toString() + "/" : "") + account.getId().toString()
                + (fileName != null && !fileName.isEmpty() ? "/" + fileName : "");
        return new FileUrl(url, fileName);
    }

    private PagingSelector createPaging(int limit) {
        PagingSelector pagingSelector = new PagingSelector();
        pagingSelector.setFirst(0l);
        pagingSelector.setCount((long) limit);

        return pagingSelector;
    }

    @PostConstruct
    public void init() {
        textSizeId = jdbcOperations.queryForObject("select size_id from creativesize where name = ?",
                new Object[]{TEXT_SIZE_TEMPLATE}, Long.class);
        textTemplateId = jdbcOperations.queryForObject("select template_id from template where name = ? and template_type = 'CREATIVE'",
                new Object[]{TEXT_SIZE_TEMPLATE}, Long.class);
        contentCategoryType = jdbcOperations.queryForObject("select cct_id from creativecategorytype where name = ?",
                new Object[]{CONTENT_CATEGORY_TYPE_NAME},
                (ResultSet rs, int ind) -> new ContentType(rs.getInt("cct_id"), CONTENT_CATEGORY_TYPE_NAME));
        visualCategoryType = jdbcOperations.queryForObject("select cct_id from creativecategorytype where name = ?",
                new Object[]{VISUAL_CATEGORY_TYPE_NAME},
                (ResultSet rs, int ind) -> new ContentType(rs.getInt("cct_id"), VISUAL_CATEGORY_TYPE_NAME));
    }
}
