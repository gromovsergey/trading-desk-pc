package com.foros.session.site;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.fileman.FileInfo;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.CreativeInfo;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.ImpressionInfo;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.creative.PreviewException;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.ContentSourceSupport;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.PathProviderUtil;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;
import com.foros.util.customization.CustomizationHelper;
import com.foros.util.preview.PreviewHelper;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.foros.config.ConfigParameters.ADSERVER_DATA_URL;
import static com.foros.config.ConfigParameters.DATA_URL;
import static com.foros.config.ConfigParameters.REDIRECT_PATH;
import static com.foros.config.ConfigParameters.WDTAG_CUSTOMIZATIONS_PATH;
import static com.foros.config.ConfigParameters.WDTAG_PREVIEW_PATH;

@Stateless(name = "WDTagPreviewService")
public class WDTagPreviewServiceBean implements WDTagPreviewService {
    private final static Logger logger = Logger.getLogger(WDTagPreviewServiceBean.class.getName());

    private final static String timestampFile = "lastchecktime";

    private final static String tagsToUpdateFile = "tagsToUpate";

    private final static String EMPTY_HTML = "<html><head></head><body></body></html>";

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ConfigService configService;

    @EJB
    private TemplateService templateService;

    @EJB
    LoggingJdbcTemplate jdbcTemplate;

    @Override
    public ContentSource getTagContentHtml(Long tagId) {
        if (tagId == null) {
            return getEmptyHtml();
        }

        WDTag tag = em.find(WDTag.class, tagId);
        return getHtml(tag, false);
    }

    @Override
    public ContentSource getLiveTagContentHtml(WDTag tag) {
        if (tag.getTemplate() == null || tag.getTemplate().getId() == null) {
            return getEmptyHtml();
        }

        tag.setTemplate(em.find(DiscoverTemplate.class, tag.getTemplate().getId()));
        tag.setSite(em.getReference(Site.class, tag.getSite().getId()));

        for (WDTagOptionValue option : tag.getOptions()) {
            option.setOption(em.find(Option.class, option.getOption().getId()));

            OptionType optionType = option.getOption().getType();
            String optionValue = option.getValue();

            if ((optionType == OptionType.FILE || optionType == OptionType.DYNAMIC_FILE) && StringUtil.isPropertyNotEmpty(optionValue)) {
                String fileName = OptionValueUtils.getPublisherRoot(tag.getAccount()) + optionValue;
                option.setValue(fileName);
            } else if (optionType == OptionType.FILE_URL && StringUtil.isPropertyNotEmpty(optionValue) &&
                    !optionValue.startsWith("http://") && !optionValue.startsWith("https://")) {
                String fileName = OptionValueUtils.getPublisherRoot(tag.getAccount()) + optionValue;
                option.setValue(fileName);
            }
        }

        return getHtml(tag, true);
    }

    @Override
    public String getHTMLCode(WDTag tag) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            generatePreviewInternal(tag, output, ApplicationFormat.DISCOVER_TAG_FORMAT);
        } finally {
            IOUtils.closeQuietly(output);
        }

        try {
            return output.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private ContentSource getEmptyHtml() {
        try {
            return ContentSourceSupport.create(EMPTY_HTML.getBytes("UTF-8"), "empty.html");
        } catch (UnsupportedEncodingException e) {
            return ContentSourceSupport.create(new byte[0], "empty.html");
        }
    }

    private ContentSource getHtml(WDTag tag, boolean isLivePreview) {
        if (isLivePreview) {
            return getLiveHtmlContentSource(tag);
        }

        return getHtmlContentSource(tag);
    }

    private ContentSource getHtmlContentSource(WDTag tag) {
        PathProvider previewPP = getWDTagsPreviewPP(tag);
        FileSystem previewFS = previewPP.createFileSystem();
        String htmlFileName = makeFileName(tag, CustomizationHelper.getCustomizationName());

        try {
            OutputStream output = null;
            try {
                output = previewFS.openFile(htmlFileName);
                generatePreviewInternal(tag, output, ApplicationFormat.PREVIEW_FORMAT);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } catch (IOException e) {
            throw new PreviewException("Cannot open output file: " + htmlFileName);
        }

        return ContentSourceSupport.create(previewPP.getPath(htmlFileName));
    }

    private ContentSource getLiveHtmlContentSource(WDTag tag) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            generatePreviewInternal(tag, output, ApplicationFormat.PREVIEW_FORMAT);
        } finally {
            IOUtils.closeQuietly(output);
        }

        return ContentSourceSupport.create(output.toByteArray(), "livePreview");
    }

    private String makeFileName(WDTag tag, String suffix) {
        String base = tag.getId().toString();
        if (StringUtil.isPropertyNotEmpty(suffix)) {
            base += "-" + suffix;
        }
        return base + ".html";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generatePreview(WDTag tag) {
        getHtml(tag, false);
    }

    private Set<Long> readTagsToUpdate() {
        ObjectInputStream inputStream = null;
        Set<Long> tagsToUpdate = new LinkedHashSet<Long>();
        FileSystem discoverFS = pathProviderService.getDiscover().createFileSystem();
        try {

            //Construct the ObjectInputStream object
            inputStream = new ObjectInputStream(new FileInputStream(discoverFS.getPathProvider().getPath(tagsToUpdateFile)));

            tagsToUpdate = (Set<Long>)inputStream.readObject();

        } catch (FileNotFoundException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
            }
        }
        return tagsToUpdate;
    }

    private void writeTagsToUpdate(Set<Long> tagsToUpdate) {
        ObjectOutputStream outputStream = null;
        FileSystem discoverFS = pathProviderService.getDiscover().createFileSystem();
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(discoverFS.getPathProvider().getPath(tagsToUpdateFile)));
            outputStream.writeObject(tagsToUpdate);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void updateObsoletePreviews() {
        Set<Long> tagsToUpdate = readTagsToUpdate();
        if (tagsToUpdate.isEmpty()) {
            FileSystem discoverFS = pathProviderService.getDiscover().createFileSystem();
            FileSystem templatesFS = pathProviderService.getTemplates().createFileSystem();

            long lastCheckTime = discoverFS.getFileInfo(timestampFile).getTime();

            List<TemplateFile> files = findCustomizationTemplateFiles();

            for (TemplateFile file : files) {
                checkFile(templatesFS, lastCheckTime, file);
            }

            try {
                discoverFS.touch(timestampFile);
            } catch (IOException e) {
                logger.log(Level.FINE, "Unable to touch timestamp file: " + e.getMessage());
            }

            LocalDate currentDate = new LocalDate(lastCheckTime);
            String query = "select distinct tag.wdtag_id as wdtag_id from wdtag tag " +
                           " inner join template t on tag.template_id = t.template_id " +
                           " left join wdtagoptionvalue v on tag.wdtag_id = v.wdtag_id " +
                           " inner join options o2 on o2.template_id = t.template_id  " +
                           "where tag.version > ?::timestamp or t.version > ?::timestamp or v.version > ?::timestamp or o2.version > ?::timestamp ";

            tagsToUpdate.addAll(jdbcTemplate.queryForList(
                    query,
                    new Object[]{currentDate, currentDate, currentDate, currentDate},
                    Long.class
            ));
        }

        Iterator<Long> tagsIterator = tagsToUpdate.iterator();
        if (tagsIterator.hasNext()) {
            StringBuilder tagsSelectQuery = new StringBuilder("SELECT t FROM WDTag t WHERE t.id in (");
            tagsSelectQuery.append(tagsIterator.next());
            tagsIterator.remove();

            for (int i = 0; i < 9 && tagsIterator.hasNext(); i++) {
                tagsSelectQuery.append(',').append(tagsIterator.next());
                tagsIterator.remove();
            }
            tagsSelectQuery.append(')');

            if (tagsToUpdate.size() > 0) {
                logger.log(Level.INFO, tagsToUpdate.size() + " tags pending preview generation.");
            }
            Query tagsQuery = em.createQuery(tagsSelectQuery.toString());

            @SuppressWarnings("unchecked")
            List<WDTag> tags = tagsQuery.getResultList();

            for (WDTag tag : tags) {
                try {
                    generateCustomizationInternal(tag);
                } catch (Exception e) {
                    // There can be an exception when file is concurrently modified by another thread,
                    // just ignore it, let another thread to generate the customization file.
                    // Any I/O exceptions also should be ignored
                    logger.log(Level.FINE, "Unable to generate customization file for tag " + tag.getId());
                }

                FileSystem previewFS = getWDTagsPreviewPP(tag).createFileSystem();
                deletePreview(previewFS, tag, null);
                for (String name : CustomizationHelper.getCustomizationNames()) {
                    deletePreview(previewFS, tag, name);
                }
            }
        }

        writeTagsToUpdate(tagsToUpdate);
    }

    private void deletePreview(FileSystem previewFS, WDTag tag, String suffix) {
        String fileName = makeFileName(tag, suffix);
        if (previewFS.checkExist(fileName)) {
            previewFS.delete(fileName);
        }
    }

    private List<TemplateFile> findCustomizationTemplateFiles() {
        String query = "select f from TemplateFile f where f.applicationFormat.name = :appFormat";
        Query q = em.createQuery(query);
        q.setParameter("appFormat", ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT);
        @SuppressWarnings("unchecked")
        List<TemplateFile> result = q.getResultList();
        return result;
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
                if (template instanceof DiscoverTemplate) {
                    deletePreview((DiscoverTemplate) template);

                    for (WDTag tag : ((DiscoverTemplate) template).getTags()) {
                        generateCustomizationInternal(tag);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Preview deletion failed for discover template file: " + file, e);
        }
    }

    private OutputStream generatePreviewInternal(WDTag tag, OutputStream output, String appFormat) {
        ImpressionInfo impressionInfo = new ImpressionInfo(getTokens(tag, appFormat), Collections.<CreativeInfo>emptyList());
        templateService.generatePreview(tag.getTemplate().getId(), null, appFormat, impressionInfo, output);

        return output;
    }

    private PathProvider getWDTagsPreviewPP(WDTag tag) {
        String templateDir = tag.getTemplate().getId().toString();
        String folderName = configService.get(WDTAG_PREVIEW_PATH);
        PathProvider discoverPreviewPP = PathProviderUtil.getNested(pathProviderService.getDiscover(), folderName, OnNoProviderRoot.AutoCreate);
        return PathProviderUtil.getNested(discoverPreviewPP, templateDir, OnNoProviderRoot.AutoCreate);
    }

    private PathProvider getWDTagsCustomizationPP(WDTag tag) {
        String wdtagPath = FileUtils.generateDirectoryPartById(tag.getId());
        String folderName = configService.get(WDTAG_CUSTOMIZATIONS_PATH);
        PathProvider discoverCustomizationPP = PathProviderUtil.getNested(pathProviderService.getDiscover(), folderName, OnNoProviderRoot.AutoCreate);
        return PathProviderUtil.getNested(discoverCustomizationPP, wdtagPath, OnNoProviderRoot.AutoCreate);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void generateDiscoverCustomization(WDTag tag) {
        generateCustomizationInternal(tag);
    }

    @Override
    public void deletePreview(DiscoverTemplate template) {
        try {
            String templateDir = template.getId().toString();

            PathProvider discoverPreviewPP = PathProviderUtil.getNested(pathProviderService.getDiscover(),
                    configService.get(WDTAG_PREVIEW_PATH), OnNoProviderRoot.AutoCreate);

            discoverPreviewPP.createFileSystem().delete(templateDir);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't delete preview for template=" + template, e);
        }
    }

    private void generateCustomizationInternal(WDTag tag) {
        PathProvider cstmPP = getWDTagsCustomizationPP(tag);
        FileSystem previewFS = cstmPP.createFileSystem();
        String customizationFileName = FileUtils.generateFilenamePartById(tag.getId()) + ".props";

        try {
            OutputStream output = null;
            try {
                output = previewFS.openFile(customizationFileName);
                generatePreviewInternal(tag, output, ApplicationFormat.DISCOVER_CUSTOMIZATION_FORMAT);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } catch (IOException e) {
            throw new PreviewException("Cannot open output file: " + customizationFileName);
        }
    }

    private Map<String, String> getTokens(WDTag tag, String appFormat) {
        Map<String, String> tokenMap = new HashMap<String, String>();

        // system tokens
        String discoverDomain =
                tag.getAccount().getCountry().getDiscoverDomainOrDefault(configService.get(ConfigParameters.DEFAULT_DISCOVER_DOMAIN));
        tokenMap.put(CreativeToken.WDTAGID.getName(), tag.getId() == null ? null : tag.getId().toString());
        tokenMap.put(CreativeToken.TAGWIDTH.getName(), tag.getWidth() == null ? null : tag.getWidth().toString());
        tokenMap.put(CreativeToken.TAGHEIGHT.getName(), tag.getHeight() == null ? null : tag.getHeight().toString());
        tokenMap.put(CreativeToken.ADIMAGE_SERVER.getName(), getDataUrl(appFormat));
        tokenMap.put(CreativeToken.APP_FORMAT.getName(), appFormat);
        tokenMap.put(CreativeToken.TEMPLATE.getName(), tag.getTemplate().getName().getDefaultName());
        tokenMap.put(CreativeToken.DISCOVER_DOMAIN.getName(), discoverDomain);
        tokenMap.put(CreativeToken.WIDTH.getName(), tag.getWidth() == null ? null : tag.getWidth().toString());
        tokenMap.put(CreativeToken.HEIGHT.getName(), tag.getHeight() == null ? null : tag.getHeight().toString());

        // tag and hidden tokens
        Map<String, Option> optionsMap = PreviewHelper.getOptionsMap(tag.getTemplate(), OptionGroupType.Hidden, OptionGroupType.Publisher);
        Map<Long, String> optionValuesMap = getWDTagOptionValuesMap(tag, appFormat);
        tokenMap.putAll(PreviewHelper.getTokenValueMap(optionsMap, optionValuesMap));

        PreviewHelper.calculateClick(tokenMap, getRedirectUrl(appFormat), optionsMap, new HashMap<String, String>());

        // perform recursive substitution
        for (Map.Entry<String, String> entry : tokenMap.entrySet()) {
            PreviewHelper.performRecursiveSubstitution(entry.getKey(), tokenMap, optionsMap, 0);
        }

        return tokenMap;
    }

    private Map<Long, String> getWDTagOptionValuesMap(WDTag tag, String appFormat) {
        NumberFormat nf = NumberFormat.getNumberInstance(CurrentUserSettingsHolder.getLocaleOrDefault());

        String filesPath = configService.get(ConfigParameters.PUBL_PATH);
        filesPath = filesPath != null ? filesPath : "";
        String filesUrl = getDataUrl(appFormat) + "/" + filesPath;

        Map<Long, String> optionValuesMap = new HashMap<Long, String>();
        for (WDTagOptionValue opt : tag.getOptions()) {
            String value = opt.getValue();
            OptionType optionType = opt.getOption().getType();
            if (optionType == OptionType.FILE_URL || optionType == OptionType.FILE || optionType == OptionType.DYNAMIC_FILE) {
                if (value != null && !(value.startsWith("http://") || value.startsWith("https://"))) {
                    value = filesUrl + (value.startsWith("/") ? "" : "/") + value;
                }
            } else if (optionType == OptionType.COLOR) {
                value = StringUtil.isPropertyEmpty(value) ? "" : "#" + value;
            } else if (optionType == OptionType.INTEGER && !StringUtil.isPropertyEmpty(value)) {
                try {
                    Number numValue = nf.parse(value);
                    value = String.valueOf(numValue.longValue());
                } catch (Exception e) {
                    // no need to log about this
                }
            }
            optionValuesMap.put(opt.getOptionId(), value);
        }
        return optionValuesMap;
    }

    private String getDataUrl(String appFormat) {
        String dataUrl = configService.get(ApplicationFormat.PREVIEW_FORMAT.equals(appFormat) ? DATA_URL : ADSERVER_DATA_URL);
        if (dataUrl.endsWith("/")) {
            dataUrl = dataUrl.substring(0, dataUrl.length() - 1);
        }

        return dataUrl;
    }

    private String getRedirectUrl(String appFormat) {
        return getDataUrl(appFormat) + "/" + configService.get(REDIRECT_PATH);
    }
}
