package com.foros.session.creative;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.fileman.FileInfo;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.CreativeInfo;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.ImpressionInfo;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValue;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.session.BusinessServiceBean;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.ContentSourceSupport;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.PathProviderUtil;
import com.foros.session.fileman.restrictions.NullFileNameRestriction;
import com.foros.session.template.ApplicationFormatService;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.TemplateUtil;
import com.foros.util.UrlUtil;
import com.foros.util.customization.CustomizationHelper;
import com.foros.util.preview.CreativeOptionValueSource;
import com.foros.util.preview.OptionValueSource;
import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.PreviewContextBuilder;
import com.foros.util.preview.PreviewHelper;
import com.foros.util.preview.PreviewModel;
import com.foros.util.preview.TokenDefinition;
import com.foros.util.preview.token.ContextValueTokenDefinition;
import com.foros.util.preview.token.OptionTokenDefinition;
import com.foros.util.preview.token.OptionTokenDefinitionSupport;
import com.foros.util.preview.token.RandomTokenDefinition;
import com.foros.util.preview.token.StaticValueTokenDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import static com.foros.model.template.CreativeToken.*;

@Stateless(name = "CreativePreviewService")
public class CreativePreviewServiceBean extends BusinessServiceBean<Creative> implements CreativePreviewService {

    private static final Logger logger = Logger.getLogger(CreativePreviewServiceBean.class.getName());
    private static final Pattern PREVIEW_PATH_SPLITTER = Pattern.compile("/(\\d{1,18})/(\\d{1,18})/(\\d{1,18})(-([a-zA-Z0-9]+))?\\.html");

    private static final long TEXT_PREVIEW_WIDTH = 234L;
    private static final long TEXT_PREVIEW_HEIGHT = 90L;

    private static final String PREVIEW_TMP_FILE_SUFFIX = ".previewtmp";
    private static final String PREVIEW_TMP_FILE_SUFFIX_FULL = PREVIEW_TMP_FILE_SUFFIX + ".html";
    private static final String timestampFile = "lastchecktime";

    private static final HashSet<String> CLICK_SUBSTITUTIONS = new HashSet<>(Arrays.asList(
            COLOID.getName(),
            PUBID.getName(),
            SITEID.getName(),
            TAGID.getName(),
            ADVID.getName(),
            CID.getName(),
            CGID.getName(),
            CCID.getName(),
            RANDOM.getName(),
            EXTERNALID.getName()
    ));


    private static final Random rnd = new Random();

    @EJB
    private ApplicationFormatService appFmtSvc;

    @EJB
    private ConfigService configService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private TemplateService templateService;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    private Map<String, TokenDefinition> predefinedImpressionDefinitions = new HashMap<>();
    private Map<String, TokenDefinition> predefinedCreativeDefinitions = new HashMap<>();

    private Map<String, TokenDefinition> clickTokenDefinitions = new HashMap<>();
    private Map<String, TokenDefinition> dcreativeTokenDefinitions = new HashMap<>();
    private String interactiveClickService;
    private String creativesURL;
    private String dataUrl;

    public CreativePreviewServiceBean() {
        super(Creative.class);
    }

    @PostConstruct
    public void init() {
        interactiveClickService = TemplateUtil.getInteractiveClickServiceUrl(configService);
        creativesURL = TemplateUtil.getCreativesURL(configService);
        dataUrl = TemplateUtil.getDataUrl(configService);


        TokenDefinition widthToken = new ContextValueTokenDefinition("WIDTH");
        TokenDefinition heightToken = new ContextValueTokenDefinition("HEIGHT");
        TokenDefinition sizeToken = new ContextValueTokenDefinition("SIZE");

        // Impression scope
        predefinedImpressionDefinitions.put(TAGSIZE.getName(), sizeToken);
        predefinedImpressionDefinitions.put(TAGWIDTH.getName(), widthToken);
        predefinedImpressionDefinitions.put(TAGHEIGHT.getName(), heightToken);
        predefinedImpressionDefinitions.put(ADIMAGE_SERVER.getName(), new StaticValueTokenDefinition(dataUrl));
        predefinedImpressionDefinitions.put(APP_FORMAT.getName(), new StaticValueTokenDefinition(ApplicationFormat.PREVIEW_FORMAT));
        predefinedImpressionDefinitions.put(TEMPLATE.getName(), new ContextValueTokenDefinition("TEMPLATE"));
        predefinedImpressionDefinitions.put(AD_FOOTER_URL.getName(), new ContextValueTokenDefinition("AD_FOOTER_URL"));

        // Creative scope
        predefinedCreativeDefinitions.put(SIZE.getName(), sizeToken);
        predefinedCreativeDefinitions.put(WIDTH.getName(), widthToken);
        predefinedCreativeDefinitions.put(HEIGHT.getName(), heightToken);
        predefinedCreativeDefinitions.put(RANDOM.getName(), RandomTokenDefinition.INSTANCE);
        predefinedCreativeDefinitions.put(KEYWORD.getName(), new StaticValueTokenDefinition("[KEYWORD]"));
        predefinedCreativeDefinitions.put(PRECLICK.getName(), new StaticValueTokenDefinition(interactiveClickService));
        predefinedCreativeDefinitions.put(PRECLICKF.getName(), new StaticValueTokenDefinition(interactiveClickService));
        predefinedCreativeDefinitions.put(FOROSPRECLICK.getName(), new StaticValueTokenDefinition(interactiveClickService));
        predefinedCreativeDefinitions.put(ADIMAGE_PATH.getName(), new ContextValueTokenDefinition("IMAGE-PATH"));

        // CLICK token scope
        copy(predefinedCreativeDefinitions, clickTokenDefinitions, KEYWORD, RANDOM, SIZE);

        // dcreative scope
        copy(predefinedImpressionDefinitions, dcreativeTokenDefinitions, ADIMAGE_SERVER);
        copy(predefinedCreativeDefinitions, dcreativeTokenDefinitions, PRECLICK, PRECLICKF, FOROSPRECLICK, RANDOM, ADIMAGE_PATH);
    }

    private void copy(Map<String, TokenDefinition> from, Map<String, TokenDefinition> to, CreativeToken... tokens) {
        for (CreativeToken token : tokens) {
            String name = token.getName();
            TokenDefinition value = from.get(name);
            if (value == null) {
                throw new IllegalArgumentException(token.toString());
            }
            to.put(name, value);
        }
    }


    private void checkFile(FileSystem templatesFS, long lastCheck, TemplateFile file) {
        try {
            FileInfo fileInfo = templatesFS.getFileInfo(file.getTemplateFile());

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                    "Check: file " + file.getTemplateFile() + " template " + file.getTemplate().
                        getName() + "(id = " + file.getTemplate().getId() + ") last modified " + fileInfo.getTime());
            }

            if (fileInfo.getTime() > lastCheck) {
                Template template = file.getTemplate();
                if (template instanceof CreativeTemplate) {
                    deletePreview((CreativeTemplate) template);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Preview deletion failed for creative template file: " + file, e);
        }
    }

    @Override
    public void deleteObsoletePreviews() {
        PathProviderService pps = pathProviderService;

        FileSystem templatesFS = pathProviderService.createFileSystem(pps.getTemplates());
        FileSystem previewFS = pathProviderService.createFileSystem(pps.getPreview());

        List<TemplateFile> files = findPreviewTemplateFiles();

        long lastCheck = previewFS.getFileInfo(timestampFile).getTime();
        logger.log(Level.INFO, "Last check time " + lastCheck);

        for (TemplateFile file : files) {
            checkFile(templatesFS, lastCheck, file);
        }

        try {
            previewFS.touch(timestampFile);
        } catch (IOException e) {
            logger.log(Level.FINE, "Unable to touch timestamp file: " + e.getMessage());
        }
    }

    @Override
    public void deletePreview(Creative creative) {
        deletePreview(creative.getTemplate().getId(), creative.getSize().getId(), creative.getId());

        if (creative.isTextCreative()) {
            Set<CreativeSize> tagSizes = campaignCreativeService.getEffectiveTagSizes(creative);
            for (CreativeSize tagSize : tagSizes) {
                deletePreview(creative.getTemplate().getId(), tagSize.getId(), creative.getId());
            }
        }
    }

    @Override
    public void deletePreview(CreativeSize size) {
        List<CreativeTemplate> templates = findPreviewTemplatesBySize(size);

        for (CreativeTemplate tmpl : templates) {
            deletePreview(tmpl.getId(), size.getId(), null);
        }
    }

    @Override
    public void deletePreview(CreativeTemplate template) {
        deletePreview(template.getId(), null, null);
    }

    private void deletePreview(Long templateId, Long sizeId, Long creativeId) {
        try {
            String path = PreviewHelper.calculatePreviewPathBase(templateId, sizeId, creativeId);

            if (creativeId == null) {
                getPreviewFS().delete(path);
            } else {
                getPreviewFS().delete(path + PreviewHelper.calculatePreviewPathDefaultSuffix());
                for (String name : CustomizationHelper.getCustomizationNames()) {
                    getPreviewFS().delete(path + PreviewHelper.calculatePreviewPathSuffix(name));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't delete preview for:" + " template=" + templateId + " size=" + sizeId + " creative=" + creativeId, e);
        }
    }

    @Override
    public void deleteAllTemporaryCreativePreviews() {
        final Long maxAge = configService.get(ConfigParameters.TMP_CREATIVE_PREVIEW_FILES_MAX_AGE);
        try {
            Files.walkFileTree(new File(pathProviderService.getPreview().getRootDir()).toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    String fileName = file.toString();
                    if (fileName.endsWith(PREVIEW_TMP_FILE_SUFFIX_FULL) &&
                            System.currentTimeMillis() - attr.creationTime().toMillis() >= maxAge) {
                        try {
                            Files.deleteIfExists(file);
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Can't delete path " + file, e);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.log(Level.WARNING, "Can't process path " + file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exc) {
            logger.log(Level.WARNING, "Can't clear temporary creative preview files", exc);
        }
    }

    private List<TemplateFile> findPreviewTemplateFiles() {
        String query = "select f from TemplateFile f where f.applicationFormat.name = :name";

        //noinspection unchecked
        return em.createQuery(query)
                .setParameter("name", ApplicationFormat.PREVIEW_FORMAT)
                .getResultList();
    }

    private List<CreativeTemplate> findPreviewTemplatesBySize(CreativeSize size) {
        ApplicationFormat appFmt = appFmtSvc.findByName(ApplicationFormat.PREVIEW_FORMAT);

        String templateFileQuery = "SELECT DISTINCT t FROM CreativeTemplate t JOIN t.templateFiles f " +
                "WHERE f.creativeSize = :size AND f.applicationFormat = :appFmt";
        Query q = em.createQuery(templateFileQuery);

        q.setParameter("size", size);
        q.setParameter("appFmt", appFmt);
        @SuppressWarnings("unchecked")
        List<CreativeTemplate> result = q.getResultList();
        return result;
    }

    @Override
    public PreviewInfoTO generateCreativePreviewInfo(final Long creativeId, Long sizeId, Long templateId) {
        PreviewInfoTO to = new PreviewInfoTO();
        CreativeTemplate template = em.find(CreativeTemplate.class, templateId);
        CreativeSize size = em.find(CreativeSize.class, sizeId);

        to.setPath(PreviewHelper.calculatePreviewUrl(configService.detach(), templateId, sizeId, creativeId));

        if (template.isText() && size.isText()) {
            to.setWidth(TEXT_PREVIEW_WIDTH);
            to.setHeight(TEXT_PREVIEW_HEIGHT);
        } else {
            PreviewModel previewModel = buildPreviewModel(template, size);
            PreviewContext context = PreviewContextBuilder
                    .of(template, size, new LazyOptionValueSource(creativeId))
                    .withCreativeId(creativeId)
                    .build(previewModel.getAllDefinitions());
            String width = context.evaluateToken(WIDTH);
            String height = context.evaluateToken(HEIGHT);

            if (!StringUtils.isEmpty(width) && validateLongParameter(width, to, "creative.previewWidthIsIncorrect")) {
                to.setWidth(Long.parseLong(width));
            }

            if (!StringUtils.isEmpty(height) && validateLongParameter(height, to, "creative.previewHeightIsIncorrect")) {
                to.setHeight(Long.parseLong(height));
            }
        }
        return to;
    }

    @Override
    public PreviewInfoTO generateCreativePreviewInfo(Long creativeId) {
        Creative creative = findById(creativeId);
        return generateCreativePreviewInfo(creativeId, creative.getSize().getId(), creative.getTemplate().getId());
    }

    private boolean validateLongParameter(String value, PreviewInfoTO to, String errorLabel) {
        if (!NumberUtil.isLong(value)) {
            to.addError(errorLabel);
            return false;
        }
        return true;
    }

    @Override
    public ContentSource dcreative(String fileName, Long creativeId) {
        FileSystem fs = pathProviderService.getCreatives().createFileSystem();
        fs.setFileNameRestriction(new NullFileNameRestriction());
        try (InputStream input = fs.readFile(fileName)) {
            Map<String, TokenDefinition> tokenDefinitions = new HashMap<>(dcreativeTokenDefinitions);
            Creative creative = findById(creativeId);
            CreativeTemplate template = creative.getTemplate();
            CreativeSize size = creative.getSize();
            PreviewModel model = buildPreviewModel(template, size);
            OptionTokenDefinitionSupport crClickDefinition =
                    (OptionTokenDefinitionSupport) model.getCreativeDefinitions().get(CreativeToken.CRCLICK.getName());
            if (crClickDefinition != null) {
                tokenDefinitions.put(CLICK.getName(), clickDefinition(crClickDefinition));
                tokenDefinitions.put(CLICKF.getName(), clickDefinition(crClickDefinition));
            }

            PreviewContext previewContext = PreviewContextBuilder.of(template, size, new CreativeOptionValueSource(creative))
                    .withCreativeId(creativeId)
                    .build(tokenDefinitions);

            Map<String, String> tokenValues = previewContext.evaluateAll();
            String res = PreviewHelper.generateTextPreview(tokenValues, input);
            return ContentSourceSupport.create(res.getBytes(StandardCharsets.UTF_8), "tempContent");
        } catch (IOException e) {
            throw new PreviewException(e);
        }
    }

    @Override
    public ContentSource generatePreview(String previewPath) {
        FileSystem fs = pathProviderService.getPreview().createFileSystem();
        if (!fs.checkExist(previewPath)) {
            Matcher pathSplitter = PREVIEW_PATH_SPLITTER.matcher(previewPath);
            if (!pathSplitter.matches()) {
                return null;
            }

            final Long templateId = Long.valueOf(pathSplitter.group(1));
            final Long sizeId = Long.valueOf(pathSplitter.group(2));
            final Long creativeId = Long.valueOf(pathSplitter.group(3));
            String customization;
            if (pathSplitter.start(5) != -1) {
                customization = pathSplitter.group(5);
                // validate customization!!!
            } else {
                customization = null;
            }

            final Creative creative;
            try {
                creative = findById(creativeId);
            } catch (EntityNotFoundException e) {
                return null;
            }

            if (!creative.getTemplate().getId().equals(templateId)) {
                return null;
            }

            // text creatives can be viewed in different sizes
            if (!creative.getSize().getId().equals(sizeId) && !creative.isTextCreative()) {
                return null;
            }

            CustomizationHelper.overrideCustomization(customization, new Runnable() {
                @Override
                public void run() {
                    updatePreviewAndReturn(creativeId, sizeId, templateId, null, null);
                }
            });
        }
        return ContentSourceSupport.create(fs, previewPath);
    }

    @Override
    public String generateTemporaryPreview(Creative creative) {
        Long sizeId = creative != null && creative.getSize() != null ? creative.getSize().getId() : null;
        Long templateId = creative != null && creative.getTemplate() != null ? creative.getTemplate().getId() : null;
        if (sizeId == null || templateId == null) {
            return "";
        }

        AdvertiserAccount account = creative.getAccount();
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            if (creative.isTextCreative() && TextCreativeOption.IMAGE_FILE.getToken().equals(optionValue.getOption().getToken())) {
                if (StringUtil.isPropertyNotEmpty(optionValue.getValue())) {
                    String fileName = TextAdImageUtil.getResizedFilePath(configService, account, optionValue.getValue());
                    optionValue.setValue(fileName);
                    campaignCreativeService.updateImagePreview(account, fileName);
                }
            } else {
                OptionValueUtils.prepareOptionValue(optionValue, account);
            }
        }

        String filePrefix = System.currentTimeMillis() + "_" + rnd.nextLong() + PREVIEW_TMP_FILE_SUFFIX;
        String previewPath = updatePreviewAndReturn(null, sizeId, templateId, filePrefix, creative);
        String previewServiceUrl = UrlUtil.concat(configService.get(ConfigParameters.DATA_URL), configService.get(ConfigParameters.TMP_PREVIEW_PATH));
        return UrlUtil.concat(previewServiceUrl, previewPath);
    }

    @Override
    public ContentSource getTemporaryPreview(String path) {
        FileSystem fs = pathProviderService.getPreview().createFileSystem();
        if (fs.checkExist(path)) {
            try (InputStream is = fs.readFile(path)) {
                ContentSource result = ContentSourceSupport.create(IOUtils.toByteArray(is), "");
                fs.delete(path);
                return result;
            } catch (IOException e) {
                logger.log(Level.WARNING, "Can't read preview file " + path, e);
            }
        }

        return null;
    }
    private FileSystem getPreviewFS() {
        return pathProviderService.createFileSystem(pathProviderService.getPreview());
    }

    private FileSystem getPreviewFS(Creative creative, Long sizeId) {
        String templateDir = creative.getTemplate().getId().toString();
        String sizeDir = sizeId.toString();
        PathProvider templatePP = PathProviderUtil.getNested(pathProviderService.getPreview(), templateDir, OnNoProviderRoot.AutoCreate);
        PathProvider sizePP = PathProviderUtil.getNested(templatePP, sizeDir, OnNoProviderRoot.AutoCreate);
        return pathProviderService.createFileSystem(sizePP);
    }

    private void generatePreview(Creative creative, Long sizeId, OutputStream output) {
        List<CreativeOptionValueSource> creativeSources = Collections.singletonList(new CreativeOptionValueSource(creative));

        CreativePreviewOptions previewOptions = new CreativePreviewOptions();
        previewOptions.setCreativeSources(creativeSources);
        previewOptions.setCreativeId(creative.getId());
        previewOptions.setTemplateId(creative.getTemplate().getId());
        previewOptions.setSizeId(sizeId);

        generatePreview(previewOptions, output);
    }

    @Override
    public void generatePreview(CreativePreviewOptions previewOptions, OutputStream output) {
        CreativeSize size = em.find(CreativeSize.class, previewOptions.getSizeId());
        CreativeTemplate template = em.find(CreativeTemplate.class, previewOptions.getTemplateId());
        PreviewModel model = buildPreviewModel(template, size);

        PreviewContext impressionContext = PreviewContextBuilder
                .of(template, size, previewOptions.getTagSource())
                .withAdFooter(previewOptions.getAdFooterUrl())
                .build(model.getImpressionDefinitions());

        Map<String, String> impressionTokens = impressionContext.evaluateAll();
        List<CreativeInfo> creativeInfos = new ArrayList<>(previewOptions.getCreativeSources().size());

        for (OptionValueSource creativeSource : previewOptions.getCreativeSources()) {
            PreviewContext creativeContext = PreviewContextBuilder
                    .empty()
                    .withTemplate(template)
                    .withSize(size)
                    .withCreativeId(previewOptions.getCreativeId())
                    .withParent(impressionContext)
                    .withOptionValueSource(creativeSource)
                    .build(model.getCreativeDefinitions());

            creativeInfos.add(new CreativeInfo(creativeContext.evaluateAll()));
        }

        ImpressionInfo impressionInfo = new ImpressionInfo(impressionTokens, creativeInfos);
        templateService.generatePreview(
                previewOptions.getTemplateId(),
                previewOptions.getSizeId(),
                ApplicationFormat.PREVIEW_FORMAT,
                impressionInfo,
                output
        );
    }

    private String updatePreviewAndReturn(Long creativeId, Long sizeId, Long templateId, String previewFilePrefix, Creative creative) {
        previewFilePrefix = (previewFilePrefix == null) ? creativeId.toString() : previewFilePrefix;
        String path = PreviewHelper.calculatePreviewPath(templateId, sizeId, previewFilePrefix);
        if (getPreviewFS().checkExist(path)) {
            return path;
        }

        creative = (creative == null) ? findById(creativeId) : creative;
        FileSystem fs = getPreviewFS(creative, sizeId);
        String fileName = PreviewHelper.calculatePreviewFile(previewFilePrefix);
        try (OutputStream output = fs.openFile(fileName)) {
            generatePreview(creative, sizeId, output);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Failed to create output file " + fileName, ex);
        }
        return path;
    }

    @Override
    public PreviewModel buildPreviewModel(CreativeTemplate template, CreativeSize size) {
        Set<OptionGroup> templateGroups = (Set<OptionGroup>) (template != null ? template.getOptionGroups() : Collections.emptySet()) ;
        Set<OptionGroup> sizeGroups = size.getOptionGroups();
        ArrayList<OptionGroup> groups = new ArrayList<>(templateGroups.size() + sizeGroups.size());
        // size goes after template to override options
        groups.addAll(templateGroups);
        groups.addAll(sizeGroups);

        HashMap<String, OptionTokenDefinitionSupport> creativeOptionDefinitions = new HashMap<>();
        HashMap<String, OptionTokenDefinitionSupport> impressionOptionDefinitions = new HashMap<>();

        for (OptionGroup group : groups) {
            for (Option option : group.getOptions()) {
                OptionTokenDefinitionSupport tokenDefinition = newTokenDefinition(option);

                switch (group.getType()) {
                    case Advertiser:
                        creativeOptionDefinitions.put(option.getToken(), tokenDefinition);
                        break;
                    case Publisher:
                        impressionOptionDefinitions.put(option.getToken(), tokenDefinition);
                        break;
                    case Hidden:
                        if (option.getDefaultValue() != null) {
                            impressionOptionDefinitions.put(option.getToken(), tokenDefinition);
                        } else {
                            creativeOptionDefinitions.put(option.getToken(), tokenDefinition);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported: " + group.getType());
                }
            }
        }

        Map<String, TokenDefinition> impressionDefinitions = new HashMap<>();
        impressionDefinitions.putAll(predefinedImpressionDefinitions);
        impressionDefinitions.putAll(impressionOptionDefinitions);

        Map<String, TokenDefinition> creativeDefinitions = new HashMap<>();
        creativeDefinitions.putAll(predefinedCreativeDefinitions);
        creativeDefinitions.putAll(creativeOptionDefinitions);

        OptionTokenDefinitionSupport crClickDefinition = impressionOptionDefinitions.get(CRCLICK.getName());
        if (crClickDefinition == null) {
            crClickDefinition = creativeOptionDefinitions.get(CRCLICK.getName());
        }

        if (crClickDefinition != null) {
            creativeDefinitions.put(CLICK.getName(), clickDefinition(crClickDefinition));
            creativeDefinitions.put(CLICKF.getName(), clickDefinition(crClickDefinition));
        }

        return new PreviewModel(impressionDefinitions, creativeDefinitions);
    }

    private CRCLICKTokenDefinition clickDefinition(OptionTokenDefinitionSupport crClickDefinition) {
        return new CRCLICKTokenDefinition(
                interactiveClickService,
                crClickDefinition.getOption(),
                CLICK_SUBSTITUTIONS
        );
    }

    private OptionTokenDefinitionSupport newTokenDefinition(Option option) {
        OptionTokenDefinitionSupport tokenDefinition;

        tokenDefinition = specialTokenDefinition(option);

        if (tokenDefinition != null) {
            return tokenDefinition;
        }

        switch (option.getType()) {
            case FILE:
            case FILE_URL:
                tokenDefinition = new FileOptionTokenDefinition(creativesURL, option);
                break;
            case DYNAMIC_FILE:
                tokenDefinition = new DynamicFileOptionTokenDefinition(dataUrl, option);
                break;
            default:
                tokenDefinition = new OptionTokenDefinition(option);
                break;
        }
        return tokenDefinition;
    }

    private OptionTokenDefinitionSupport specialTokenDefinition(Option option) {
        if (CRHTML.getName().equals(option.getToken()) && option.getType() == OptionType.FILE_URL) {
            return new DynamicFileOptionTokenDefinition(dataUrl, option);
        } else if (CRCLICK.getName().equals(option.getToken())) {
            return new CRCLICKTokenDefinition(interactiveClickService, option);
        }
        return null;
    }

    private static String fileUrl(String prefix, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }

        StringBuilder result = new StringBuilder(prefix.length() + fileName.length());
        result.append(prefix);
        if (fileName.charAt(0) != '/') {
            result.append('/');
        }
        for (String str : fileName.split("/")) {
            result.append(StringUtil.encodeUrl(str).replace("+", "%20")).append("/");
        }
        return result.deleteCharAt(result.lastIndexOf("/")).toString();
    }

    private static class FileOptionTokenDefinition extends OptionTokenDefinitionSupport {
        private final String creativesUrl;

        public FileOptionTokenDefinition(String creativesUrl, Option option) {
            super(option);
            this.creativesUrl = creativesUrl;
        }

        @Override
        public String evaluate(PreviewContext context) {
            String value = super.evaluate(context);
            if (UrlUtil.isSchemaUrl(value, true)) {
                return value;
            }
            return fileUrl(creativesUrl, value);
        }
    }

    private static class CRCLICKTokenDefinition extends OptionTokenDefinitionSupport {
        private String interactiveClickService;

        public CRCLICKTokenDefinition(String interactiveClickService, Option option) {
            super(option);
            this.interactiveClickService = interactiveClickService;
        }

        public CRCLICKTokenDefinition(String interactiveClickService, Option option, Set<String> substitutions) {
            super(option, substitutions);
            this.interactiveClickService = interactiveClickService;
        }

        @Override
        public String evaluate(PreviewContext context) {
            return interactiveClickService + super.evaluate(context);
        }
    }

    private static class DynamicFileOptionTokenDefinition extends OptionTokenDefinitionSupport {
        private final String dataUrl;

        public DynamicFileOptionTokenDefinition(String dataUrl, Option option) {
            super(option);
            this.dataUrl = dataUrl;
        }

        @Override
        public String evaluate(PreviewContext context) {
            return fileUrl(dataUrl + "/dcreative.action?creativeId=" + context.getContextValue("CREATIVE_ID") + "&path=", super.evaluate(context));
        }
    }

    private class LazyOptionValueSource implements OptionValueSource {
        private final Long creativeId;
        private OptionValueSource delegate;

        public LazyOptionValueSource(Long creativeId) {
            this.creativeId = creativeId;
        }

        private OptionValueSource getDelegate() {
            if (delegate == null) {
                delegate = new CreativeOptionValueSource(findById(creativeId));
            }
            return delegate;
        }

        @Override
        public OptionValue get(Long optionId) {
            return getDelegate().get(optionId);
        }

        @Override
        public String getImagesPath() {
            return getDelegate().getImagesPath();
        }
    }
}
