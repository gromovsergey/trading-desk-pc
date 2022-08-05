package com.foros.session.template;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigService;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.site.Tag;
import com.foros.model.template.ApplicationFormat;
import com.foros.model.template.CreativeInfo;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.ImpressionInfo;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.model.template.TemplateTO;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.QuickReferenceHolderService;
import com.foros.session.UtilityService;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.PreviewException;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.fileman.PathProviderUtil;
import com.foros.session.security.AuditService;
import com.foros.session.site.WDTagPreviewService;
import com.foros.session.status.StatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.bean.Filter;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.jpa.NativeQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.util.mapper.Converter;
import com.foros.util.preview.PreviewHelper;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "TemplateService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class TemplateServiceBean extends BusinessServiceBean<Template> implements TemplateService {

    @EJB
    private AuditService auditService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private CreativePreviewService creativePreviewService;

    @EJB
    private WDTagPreviewService wdTagPreviewService;

    @EJB
    private StatusService statusService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private ApplicationFormatService applicationFormatService;

    @EJB
    private ConfigService configService;

    @EJB
    CreativeCategoryService creativeCategoryService;

    @EJB
    private HibernateWorkExecutorService executorService;

    @EJB
    private QuickReferenceHolderService quickReferenceHolderService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private OptionGroupService optionGroupService;

    private Map<Long, Option> options;

    public TemplateServiceBean() {
        super(Template.class);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.create")
    @Validate(validation = "Template.create", parameters = "#template")
    public void create(CreativeTemplate template) {
        auditService.audit(template, ActionType.CREATE);
        resolveCategories(template);
        createImpl(template);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.create")
    @Validate(validation = "Template.create", parameters = "#template")
    public void create(DiscoverTemplate template) {
        auditService.audit(template, ActionType.CREATE);
        createImpl(template);
    }

    private void createImpl(Template template) {
        super.create(template);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.update", parameters = "find('DiscoverTemplate',#template.id)")
    @Validate(validation = "Template.update", parameters = "#template")
    public DiscoverTemplate update(DiscoverTemplate template) {
        template.unregisterChange("optionGroups", "templateFiles");
        Template existingTemplate = findById(template.getId());
        auditService.audit(existingTemplate, ActionType.UPDATE);
        //template.setOptionGroups(existingTemplate.getOptionGroups());
        for (TemplateFile file : template.getTemplateFiles()) {
            if (template.getId() != null) {
                file.setTemplate(em.find(DiscoverTemplate.class, template.getId()));
            } else if (file.getId() != null) {
                file.setId(null);
            }
        }
        template = (DiscoverTemplate) super.update(template);
        wdTagPreviewService.deletePreview(template);
        return template;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.update", parameters = "find('Template',#template.id)")
    @Validate(validation = "Template.update", parameters = "#template")
    public CreativeTemplate update(CreativeTemplate template) {
        template.unregisterChange("optionGroups", "templateFiles");
        CreativeTemplate existingTemplate = (CreativeTemplate) findById(template.getId());
        auditService.audit(existingTemplate, ActionType.UPDATE);
        //template.setOptionGroups(existingTemplate.getOptionGroups());
        boolean isExistingCategoriesEmpty = existingTemplate.getCategories().isEmpty();

        resolveCategories(template);

        template = (CreativeTemplate) super.update(template);
        deletePreview(template);
        if (isExistingCategoriesEmpty && !template.getCategories().isEmpty() && !template.getCreatives().isEmpty()) {
            creativeCategoryService.removeUnlinkedVisualCategories(template.getId());
        }

        return template;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.createCopy", parameters = "find('Template',#id)")
    public Template createCopy(Long id) {
        Template template = findById(id);
        Template newTemplate = EntityUtils.clone(template);
        String originalName = template.getDefaultName();
        String defaultName = utilityService.calculateNameForCopy(template, 100, originalName, "defaultName");
        newTemplate.setDefaultName(defaultName);
        newTemplate.setOptionGroups(optionGroupService.copyGroups(template.getOptionGroups()));
        for (OptionGroup group : newTemplate.getOptionGroups()) {
            group.setTemplate(newTemplate);
        }
        for (TemplateFile file : newTemplate.getTemplateFiles()) {
            file.setTemplate(newTemplate);
        }
        auditService.audit(newTemplate, ActionType.CREATE);
        createImpl(newTemplate);
        return newTemplate;
    }

    private void resolveCategories(CreativeTemplate template) {
        template.setCategories(utilityService.resolveReferences(
                template.getCategories(),
                new HashSet<CreativeCategory>(),
                CreativeCategory.class
        ));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.delete", parameters = "find('Template', #id)")
    public Template delete(Long id) {
        Template t = findById(id);
        restrictTextTemplateUpdate(t);
        statusService.delete(findById(id));

        return t;
    }

    private void restrictTextTemplateUpdate(Template template) {
        if (template instanceof CreativeTemplate) {
            CreativeTemplate creativeTemplate = (CreativeTemplate) template;
            if (creativeTemplate.getDefaultName().equals(Template.TEXT_TEMPLATE)) {
                throw new BusinessException("Text template update is not allowed!");
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.undelete", parameters = "find('Template', #id)")
    public Template undelete(Long id) {
        Template t = findById(id);
        statusService.undelete(findById(id));
        return t;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Template.view")
    public List<TemplateTO> findAllCreativeTemplates() {
        return em.createNamedQuery("CreativeTemplate.findAll").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Template.view")
    public List<TemplateTO> findAllDiscoverTemplates() {
        return em.createNamedQuery("DiscoverTemplate.findAll").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Template.view")
    public List<TemplateTO> findAllNonDeletedCreativeTemplates() {
        return em.createNamedQuery("CreativeTemplate.findAllNonDeleted").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Template.view")
    public List<TemplateTO> findAllNonDeletedDiscoverTemplates() {
        return em.createNamedQuery("DiscoverTemplate.findAllNonDeleted").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Template> findByAccountType(AccountType accType) {
        Query q = em.createNamedQuery("Template.findByAccType");
        q.setParameter("accType", accType);

        return q.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Template> findJsHTMLTemplatesBySize(Long accountTypeId, Long sizeId) {
        List<String> applicationFormats = Arrays.asList(ApplicationFormat.JS, ApplicationFormat.HTML);

        Query query = em.createQuery("select distinct ct from AccountType at, in (at.templates) ct, in (ct.templateFiles) ctf " +
                " where at.id = :accountTypeId and ctf.creativeSize.id = :sizeId " +
                " and ct.defaultName <> :textTemplate and ctf.applicationFormat.name in (:applicationFormats)")
                .setParameter("accountTypeId", accountTypeId)
                .setParameter("sizeId", sizeId)
                .setParameter("textTemplate", CreativeTemplate.TEXT_TEMPLATE)
                .setParameter("applicationFormats", applicationFormats);

        return query.getResultList();

    }

    @Override
    public boolean isCreativeSizeLinkedToCreatives(TemplateFile templateFile) {
        List<String> applicationFormats = Arrays.asList(ApplicationFormat.JS, ApplicationFormat.HTML);

        Query query = em.createQuery("select count(distinct ct.id) from CreativeTemplate ct, TemplateFile ctf, Creative c " +
                " where c.size.id = :sizeId and ct.id = ctf.template.id and ct.id =  c.template.id " +
                " and ctf.id = :templateFileId and c.template.id = :templateId and c.status <> 'D' " +
                " and ctf.applicationFormat.name in (:applicationFormats)")
                .setParameter("sizeId", templateFile.getCreativeSize().getId())
                .setParameter("templateFileId", templateFile.getId())
                .setParameter("templateId", templateFile.getTemplate().getId())
                .setParameter("applicationFormats", applicationFormats);

        @SuppressWarnings("unchecked")
        Number linkedSizesCount = (Number) query.getSingleResult();
        return (linkedSizesCount.longValue() > 0);
    }

    @Override
    public boolean isCreativeTemplateLinkedToCreatives(Long creativeTemplateId) {
        Query query = em.createQuery("select count(ct.id) from CreativeTemplate ct, in (ct.creatives) c " +
                " where ct.id = :creativeTemplateId ");
        query.setParameter("creativeTemplateId", creativeTemplateId);

        @SuppressWarnings("unchecked")
        Number linkedTemplatesCount = (Number) query.getSingleResult();

        return linkedTemplatesCount.longValue() > 0;
    }

    @Override
    public boolean isTemplateLinkedToCreativeSize(Long accountId, Long templateId, Long sizeId) {
        List<String> applicationFormats = Arrays.asList(ApplicationFormat.JS, ApplicationFormat.HTML);

        Query query = em.createQuery(" select count(ct.id) from Account a, " +
                " in (a.accountType.templates) ct, in (ct.templateFiles) ctf " +
                " where a.id = :accountId and ctf.creativeSize.id = :sizeId  and ct.id = :templateId " +
                " and ct.defaultName <> :textTemplate and ctf.applicationFormat.name in (:applicationFormats)")
                .setParameter("accountId", accountId)
                .setParameter("sizeId", sizeId)
                .setParameter("templateId", templateId)
                .setParameter("textTemplate", CreativeTemplate.TEXT_TEMPLATE)
                .setParameter("applicationFormats", applicationFormats);

        @SuppressWarnings("unchecked")
        Number linkedSizesCount = (Number) query.getSingleResult();
        return (linkedSizesCount.longValue() > 0);
    }

    private Template findTemplateForFile(TemplateFile tf) {
        if (tf.getTemplate() == null) {
            throw new BusinessException("Creative template is not set for template file");
        }

        try {
            return findById(tf.getTemplate().getId());
        } catch (EntityNotFoundException ex) {
            throw new BusinessException("Template[id=" + tf.getTemplate().getId() + "] not found");
        }
    }

    private void deletePreview(Template template) {
        if (template instanceof CreativeTemplate) {
            creativePreviewService.deletePreview((CreativeTemplate) template);
        } else if (template instanceof DiscoverTemplate) {
            wdTagPreviewService.deletePreview((DiscoverTemplate) template);
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.updateFilesMatch", parameters = "find('Template',#tf.template.id)")
    @Validate(validation = "TemplateFile.create", parameters = "#tf")
    public void createTemplateFile(TemplateFile tf) {
        Template template = findTemplateForFile(tf);
        tf.setApplicationFormat(em.getReference(ApplicationFormat.class, tf.getApplicationFormat().getId()));
        if (tf.getCreativeSize() != null) {
            tf.setCreativeSize(em.getReference(CreativeSize.class, tf.getCreativeSize().getId()));
        }
        tf.setTemplate(template);

        if (template.getTemplateFiles() == null) {
            template.setTemplateFiles(new HashSet<TemplateFile>());
        }

        template.getTemplateFiles().add(tf);

        em.persist(tf);
        em.merge(template);
        em.flush();

        deletePreview(tf.getTemplate());
        auditService.audit(template, ActionType.UPDATE);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.updateFilesMatch", parameters = "find('TemplateFile',#tf.id).template")
    @Validate(validation = "TemplateFile.update", parameters = "#tf")
    public TemplateFile updateTemplateFile(TemplateFile tf) {
        Template template = findTemplateForFile(tf);
        Set<TemplateFile> templateFiles = new HashSet<TemplateFile>(template.getTemplateFiles());
        if (!templateFiles.contains(tf)) {
            throw new BusinessException("Template[id=" + template.getId() + "] has no template file[id=" + tf.getId() + "]");
        }

        // update creative template version
        PersistenceUtils.performHibernateLock(em, template);

        tf.setApplicationFormat(em.getReference(ApplicationFormat.class, tf.getApplicationFormat().getId()));
        tf.setCreativeSize(em.getReference(CreativeSize.class, tf.getCreativeSize().getId()));
        TemplateFile mergedCTFile = em.merge(tf);
        em.flush();

        deletePreview(tf.getTemplate());
        auditService.audit(template, ActionType.UPDATE);
        return mergedCTFile;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Template.updateFilesMatch", parameters = "find('TemplateFile',#tempalateFileId).template")
    @Validate(validation = "TemplateFile.delete", parameters = "find('TemplateFile',#tempalateFileId)")
    public void deleteTemplateFile(Long tempalateFileId) {
        TemplateFile tf = findTemplateFileById(tempalateFileId);

        deletePreview(tf.getTemplate());

        for (Iterator<TemplateFile> it = tf.getTemplate().getTemplateFiles().iterator(); it.hasNext(); ) {
            if (it.next().getId().equals(tf.getId())) {
                it.remove();
            }
        }

        em.remove(tf);
        PersistenceUtils.performHibernateLock(em, tf.getTemplate());

        em.flush();
        auditService.audit(tf.getTemplate(), ActionType.UPDATE);
    }

    @Override
    @Restrict(restriction = "Template.view")
    public TemplateFile findTemplateFileById(Long id) {
        TemplateFile tf = em.find(TemplateFile.class, id);
        if (tf == null) {
            throw new EntityNotFoundException("Template file [id=" + id + "] not found");
        }

        return tf;
    }

    @Override
    public CreativeTemplate findTextTemplate() {
        return em.find(CreativeTemplate.class, findTextTemplateId());
    }

    @Override
    public Long findTextTemplateId() {
        return quickReferenceHolderService.getTextTemplateId();
    }

    @Override
    public Template findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction = "Template.view")
    public Template view(Long id) {
        return findById(id);
    }

    @Override
    public List<Template> findAll() {
        return super.findAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Template.view")
    public List<TemplateTO> findAvailableCreativeTemplates(Long accountTypeId) {
        return findAvailableTemplates(accountTypeId, "CREATIVE");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TemplateTO> findAvailableDiscoverTemplates(Long accountTypeId) {
        return findAvailableTemplates(accountTypeId, "DISCOVER");
    }

    private List<TemplateTO> findAvailableTemplates(Long accountTypeId, String templateType) {
        ConditionStringBuilder sql = new ConditionStringBuilder()
                .append(" SELECT t.template_id, t.name, t.status FROM Template t")
                .append(" WHERE t.template_type  = '").append(templateType).append("'")
                .append(" and t.name <> '").append(Template.TEXT_TEMPLATE).append("'")
                .append(" and")
                .append("(")
                .append(" t.status <> 'D'")
                .append(accountTypeId != null, " or t.template_id IN (SELECT template_id FROM AccountTypeCreativeTemplate WHERE account_type_id = :accountTypeId)")
                .append(")");

        QueryWrapper<Object[]> q = new NativeQueryWrapper<Object[]>(em, sql.toString());
        q.oneIf(accountTypeId != null).setParameter("accountTypeId", accountTypeId);

        return CollectionUtils.convert(new Converter<Object[], TemplateTO>() {
            @Override
            public TemplateTO item(Object[] row) {
                Long id = ((Number) row[0]).longValue();
                String name = row[1].toString();
                char status = row[2].toString().charAt(0);
                return new TemplateTO(id, name, status);
            }
        }, q.getResultList());
    }

    @Override
    public void generatePreview(Long templateId, Long sizeId, String applicationFormat, ImpressionInfo impressionInfo, OutputStream output) {
        String inputFileName = null;
        InputStream input = null;
        try {
            TemplateFile templateFile = findTemplateFile(templateId, sizeId, applicationFormat);
            if (templateFile == null) {
                String message = "Template file is not found (templateId=" + templateId  +
                ", sizeId=" + sizeId + ", appFormat=" + applicationFormat + "). ";
                output.write(message.getBytes("UTF-8"));
                return;
            }

            inputFileName = templateFile.getTemplateFile();

            PathProvider templatesPP = pathProviderService.getTemplates();
            FileSystem templatesFS = templatesPP.createFileSystem();

            input = templatesFS.readFile(inputFileName);

            String res = doGeneratePreview(templateFile, impressionInfo, input, templatesPP, templatesFS);
            output.write(res.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new PreviewException("Failed to open template file " + inputFileName, e);
        } catch (Exception e) {
            throw new PreviewException("Failed to process (templateId=" + templateId  +
                    ", sizeId=" + sizeId + ", appFormat=" + applicationFormat + ", file=" +
                    inputFileName + "). ", e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public long countExpandableCreatives(Long templateId) {
        Query query = em.createQuery("select count(c) from Creative c " +
                "where c.template.id = :templateId " +
                "and c.status <> 'D' " +
                "and c.expandable = true")
                .setParameter("templateId", templateId);

        return ((Number) query.getSingleResult()).longValue();
    }

    private String doGeneratePreview(TemplateFile templateFile, ImpressionInfo impressionInfo, InputStream input,
                                 PathProvider templatesPP, FileSystem templatesFS) throws Exception {
        switch (templateFile.getType()) {
            case TEXT:
                Map<String, String> tokens = new TreeMap<>();
                tokens.putAll(commonTokens(impressionInfo.getCreatives()));
                tokens.putAll(impressionInfo.getOptionValues());
                PreviewHelper.addCreativeJsonOptionValue(tokens, impressionInfo.getCreatives());
                return PreviewHelper.generateTextPreview(tokens, input);

            case XSLT:
                String uriResolverDir = templatesFS.getParent(templateFile.getTemplateFile());
                PathProvider pp = PathProviderUtil.getNested(templatesPP, uriResolverDir);
                FileSystem uriResolverFS = pathProviderService.createFileSystem(pp);

                String xml = getXml(impressionInfo);
                return PreviewHelper.generateXSLTPreview(xml, input, uriResolverFS);
            default:
                throw new IllegalArgumentException(templateFile.getType().toString());
        }
    }

    private Map<String, String> commonTokens(List<CreativeInfo> creatives) {
        Map<String, String> res;
        switch (creatives.size()) {
            case 0:
                res = Collections.emptyMap();
                break;
            case 1:
                res = creatives.get(0).getOptionValues();
                break;
            default:
                res = new HashMap<>();
                res.putAll(creatives.get(0).getOptionValues());
                for (int i = 1; i < creatives.size(); i++) {
                    Map<String, String> optionValues = creatives.get(0).getOptionValues();
                    Iterator<Map.Entry<String, String>> it = res.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> entry = it.next();
                        if (!ObjectUtils.equals(optionValues.get(entry.getKey()), entry.getValue())) {
                            it.remove();
                        }
                    }
                }
        }
        return res;
    }

    private TemplateFile findTemplateFile(Long templateId, Long sizeId, String applicationFormat) {
        ApplicationFormat appFmt = applicationFormatService.findByName(applicationFormat);

        String templateFileQuery =
                "SELECT f FROM TemplateFile f " +
                        "WHERE f.template.id = :templateId " +
                        " AND f.applicationFormat = :appFmt" +
                        (sizeId == null ? " AND f.creativeSize is null" : " AND f.creativeSize.id = :sizeId ");

        Query q = em.createQuery(templateFileQuery);

        q.setParameter("templateId", templateId);

        if (sizeId != null) {
            q.setParameter("sizeId", sizeId);
        }

        q.setParameter("appFmt", appFmt);

        try {
            return (TemplateFile) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String getXml(ImpressionInfo impressionInfo) {
        StringBuilder buf = new StringBuilder("<?xml version=\"1.0\"?>\n");

        buf.append("<impression>\n");

        if (impressionInfo.getCreatives() != null) {
            for (CreativeInfo creativeInfo : impressionInfo.getCreatives()) {
                buf.append("<creative>\n");
                PreviewHelper.addPreviewTokens(buf, creativeInfo.getOptionValues());
                buf.append("</creative>\n");
            }
        }

        PreviewHelper.addPreviewTokens(buf, impressionInfo.getOptionValues());

        buf.append("</impression>");

        return buf.toString();
    }

    @Override
    public List<CreativeTemplate> findTemplatesWithPublisherOptions(Tag tag) {
        return jdbcTemplate.query("select * from siteutil.find_tmpl_w_pub_opts(?::int)", new Object[] {tag.getId()},
                new RowMapper<CreativeTemplate>() {
                    @Override
                    public CreativeTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return em.find(CreativeTemplate.class, rs.getLong(1));
                    }
                });
    }

    @Override
    public List<Long> findIdsByDefaultNames(Collection<String> names) {
        return em.createQuery("SELECT t.id FROM CreativeTemplate t WHERE t.defaultName in (:names)", Long.class)
                .setParameter("names", names)
                .getResultList();
    }


    @Override
    public Option findTextOptionFromTextTemplate(Long id) {
        if (options == null) {
            Collection<Option> textOptions = CollectionUtils.filter(findTextTemplate().getAdvertiserOptions(), new Filter<Option>() {
                @Override
                public boolean accept(Option element) {
                    return TextCreativeOption.byTokenOptional(element.getToken()) != null;
                }
            });

            options = new HashMap<>();
            for (Option textOption : textOptions) {
                Option option = new Option();
                option.setId(textOption.getId());
                option.setToken(textOption.getToken());
                options.put(option.getId(), option);
            }

        }
        return options.get(id);
    }
}
