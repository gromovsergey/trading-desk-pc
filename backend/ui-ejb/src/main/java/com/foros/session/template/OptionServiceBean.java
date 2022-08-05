package com.foros.session.template;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigService;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.ActionType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionFileType;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.Template;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.security.AuditService;
import com.foros.session.site.WDTagPreviewService;
import com.foros.tx.TransactionSupportService;
import com.foros.util.EntityUtils;
import com.foros.util.JpaCollectionMerger;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

@Stateless(name = "OptionService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class OptionServiceBean extends BusinessServiceBean<Option> implements OptionService {
    @EJB
    private CreativePreviewService previewService;

    @EJB
    private WDTagPreviewService wdTagPreviewService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    ConfigService configService;

    @EJB
    private AuditService auditService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private TransactionSupportService transactionSupportService;

    private HtmlOptionHelper htmlOptionHelper = new HtmlOptionHelper() {
        @Override
        protected EntityManager getEM() {
            return em;
        }

        @Override
        protected LoggingJdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }
    };

    private HtmlOptionFileHelper htmlFileHelper = new HtmlOptionFileHelper() {
        @Override
        protected TransactionSupportService getTransactionSupportService() {
            return transactionSupportService;
        }

        @Override
        protected PathProviderService getPathProviderService() {
            return pathProviderService;
        }
    };

    private Map<String, Option> optionByToken = new HashMap<String, Option>();

    public OptionServiceBean() {
        super(Option.class);
    }

    private void prePersist(Option option) {
        option.setOptionGroup(em.find(OptionGroup.class, option.getOptionGroup().getId()));
        OptionType type = option.getType();

        if (type == OptionType.ENUM) {
            for (OptionEnumValue optValue : option.getValues()) {
                if (optValue.isDefault())
                    option.setDefaultValue(optValue.getValue());
                optValue.setOption(option);
            }
        }

        if (option.getType() == OptionType.FILE || option.getType() == OptionType.FILE_URL || option.getType() == OptionType.DYNAMIC_FILE) {
            for (OptionFileType fileType : option.getFileTypes()) {
                fileType.setOption(option);
            }
        }
    }

    @Override
    @Restrict(restriction = "Option.update", parameters = "find('Option', #option.id)")
    @Validate(validation = "Option.update", parameters = "#option")
    @Interceptors({CaptureChangesInterceptor.class})
    public Option update(final Option option) {
        prePersist(option);
        Option existingOption = findById(option.getId());
        Set<HtmlOptionHelper.OptionLink> existingLinks = htmlOptionHelper.findLinks(existingOption.getOptionGroup());
        OptionType existingType = existingOption.getType();

        if (option.getType() == OptionType.ENUM) {
            final List<OptionValueType> changes = new ArrayList<>();

            (new JpaCollectionMerger<OptionEnumValue>(existingOption.getValues(), option.getValues()) {
                @Override
                protected EntityManager getEM() {
                    return em;
                }

                @Override
                protected void update(OptionEnumValue persistent, OptionEnumValue updated) {
                    if (!StringUtils.equals(persistent.getValue(), updated.getValue())) {
                        changes.add(new OptionValueType(option.getId(), persistent.getValue(), updated.getValue()));
                    }
                    updated.setVersion(persistent.getVersion());
                    super.update(persistent, updated);
                }
            }).merge();

            option.unregisterChange("values");

            if (changes.size() > 0) {
                jdbcTemplate.execute(
                        "select optionvalue_util.update_option_values(?)",
                        jdbcTemplate.createArray("optionvalue", changes)
                );
            }
            jdbcTemplate.scheduleEviction();
        }

        if (option.getType() == OptionType.FILE || option.getType() == OptionType.FILE_URL || option.getType() == OptionType.DYNAMIC_FILE) {
            List<OptionFileType> existingMimeTypes = existingOption.getFileTypes();

            for (OptionFileType mimeType : existingOption.getFileTypes()) {
                int idx = option.getFileTypes().indexOf(mimeType);
                if (idx == -1) {
                    em.remove(mimeType);
                }
            }

            for (OptionFileType mimeType : option.getFileTypes()) {
                int idx = existingMimeTypes.indexOf(mimeType);

                if (idx != -1) {
                    mimeType.setId(existingMimeTypes.get(idx).getId());
                }

                mimeType.setOption(option);
            }
        }

        if (isGroupChanged(option, existingOption)) {
            OptionGroup existingOptionGroup = existingOption.getOptionGroup();
            //modify collection to invalidate it from second level cache
            existingOptionGroup.getOptions().remove(existingOption);
            deletePreview(existingOptionGroup);
        }

        //need to check this before merge as after merge params become same object
        boolean orderChanged = isOrderChanged(option, existingOption);

        Option updated = super.update(option);
        OptionGroup optionGroup = updated.getOptionGroup();
        if (orderChanged) {
            Set<Option> options = optionGroup.getOptions();
            optionGroup.setOptions(normalizeSortOrderOnUpdate(options, updated));
        }
        auditService.audit(optionGroup.getTemplate() != null ? optionGroup.getTemplate() : optionGroup.getCreativeSize(), ActionType.UPDATE);

        htmlOptionHelper.processGroup(existingOption.getOptionGroup(), existingLinks);
        updateParentVersion(optionGroup);
        if (existingType == OptionType.HTML && existingOption.getType() != OptionType.HTML) {
            htmlFileHelper.removeOptionFilesOnCommit(option);
        }

        deletePreview(optionGroup);

        return updated;
    }

    private void deletePreview(OptionGroup optionGroup) {
        try {
            Template template = optionGroup.getTemplate();
            if (template instanceof CreativeTemplate) {
                previewService.deletePreview((CreativeTemplate) template);
            } else if (template instanceof DiscoverTemplate) {
                wdTagPreviewService.deletePreview((DiscoverTemplate) template);
            }

            CreativeSize size = optionGroup.getCreativeSize();
            if (size != null) {
                previewService.deletePreview(size);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Restrict(restriction = "Option.create", parameters = "#option")
    @Validate(validation = "Option.create", parameters = "#option")
    @Interceptors({CaptureChangesInterceptor.class})
    public void create(Option option) {
        prePersist(option);
        OptionGroup optionGroup = option.getOptionGroup();
        Set<HtmlOptionHelper.OptionLink> existingLinks = htmlOptionHelper.findLinks(optionGroup);
        Set<Option> options = optionGroup.getOptions();
        optionGroup.setOptions(normalizeSortOrderOnCreate(options, option));
        auditService.audit(optionGroup.getTemplate() != null ? optionGroup.getTemplate() : optionGroup.getCreativeSize(), ActionType.UPDATE);

        super.create(option);

        htmlOptionHelper.processGroup(option.getOptionGroup(), existingLinks);
        updateParentVersion(optionGroup);
        deletePreview(optionGroup);
    }

    @Override
    public Option findByTokenFromTextTemplate(String token) {
        Option option = optionByToken.get(token);
        if (option == null) {
            try {
                Query q = em.createNamedQuery("Option.findByToken");
                q.setParameter("token", token);
                q.setParameter("templateName", "Text");
                option = (Option) q.getSingleResult();
            } catch (NoResultException e) {
                option = null;
            }
            optionByToken.put(token, option);
        }
        return option;
    }

    @Override
    public Option findById(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction = "Option.view", parameters = "find('Option', #id)")
    public Option view(Long id) {
        return findById(id);
    }

    @Override
    public OptionEnumValue findEnumValueById(Long id) {
        return em.find(OptionEnumValue.class, id);
    }

    @Override
    public OptionEnumValue findEnumValueByStringValue(Long optionId, String value) {
        Query q = em.createQuery("select oev from OptionEnumValue oev where oev.option.id = :optionId and oev.value = :value");
        q.setParameter("optionId", optionId);
        q.setParameter("value", value);
        return (OptionEnumValue) q.getSingleResult();
    }

    @Override
    public OptionEnumValue findDefaultEnumValue(Long optionId) {
        Query q = em.createQuery("select oev from OptionEnumValue oev where oev.option.id = :optionId and oev.isDefault = true");
        q.setParameter("optionId", optionId);
        return (OptionEnumValue) q.getSingleResult();
    }

    @Override
    @Restrict(restriction = "Option.update", parameters = "find('Option', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public Option createCopy(Long id) {
        Option existing = em.find(Option.class, id);
        if (existing == null) {
            throw new EntityNotFoundException("Option with id=" + id + " not found");
        }

        Option newOption = EntityUtils.clone(existing);
        newOption.setId(null);
        newOption.setDefaultName("");
        newOption.setDefaultLabel("");
        return newOption;
    }

    @Override
    @Restrict(restriction = "Option.delete", parameters = "find('Option', #id)")
    @Interceptors({CaptureChangesInterceptor.class})
    public void remove(Long id) {
        Option option = findById(id);
        OptionGroup optionGroup = option.getOptionGroup();
        Set<HtmlOptionHelper.OptionLink> existingLinks = htmlOptionHelper.findLinks(optionGroup);
        //ToDo: waiting for https://hibernate.atlassian.net/browse/HHH-3799
        Set<Option> options = new LinkedHashSet<>(optionGroup.getOptions());
        options.remove(option);
        optionGroup.setOptions(options);
        // optionGroup.getOptions().remove(option);
        em.remove(option);
        auditService.audit(optionGroup.getTemplate() != null ? optionGroup.getTemplate() : optionGroup.getCreativeSize(), ActionType.UPDATE);

        htmlOptionHelper.processGroup(optionGroup, existingLinks);
        updateParentVersion(optionGroup);
        if (option.getType() == OptionType.HTML) {
            htmlFileHelper.removeOptionFilesOnCommit(option);
        }

        OptionHelper.evictCache(optionGroup);

        deletePreview(optionGroup);
    }

    private Set<Option> normalizeSortOrderOnCreate(Set<Option> options, Option option) {
        return normalizeSortOrderInternal(options, option, false);
    }

    private Set<Option> normalizeSortOrderOnUpdate(Set<Option> options, Option option) {
        return normalizeSortOrderInternal(options, option, true);
    }

    private Set<Option> normalizeSortOrderInternal(Set<Option> options, Option option, boolean updated) {
        int position = (option.getSortOrder() == null) ? 0: option.getSortOrder().intValue();
        LinkedList<Option> optionList = new LinkedList<Option>(options);
        if (updated) {
            optionList.remove(option);
            if (position > optionList.size()) {
                position = optionList.size();
            }
            optionList.add(position, option);
        } else {
            optionList.add(position, option);
        }
        normalizeSortOrders(optionList);
        return new HashSet<Option>(optionList);
    }

    private void normalizeSortOrders(List<Option> options) {
        int sortOrder = 0;
        for (Option option : options) {
            option.setSortOrder(sortOrder++);
        }
    }

    private boolean isOrderChanged(Option option, Option existingOption) {
        if (isGroupChanged(option, existingOption)) {
            return true;
        }

        if (!option.getSortOrder().equals(existingOption.getSortOrder())) {
            return true;
        }

        if (option.getDefaultName() != null ? !option.getDefaultName().equals(existingOption.getDefaultName()) : existingOption.getDefaultName() != null) {
            return true;
        }
        if (option.getType() != null ? !option.getType().equals(existingOption.getType()) : existingOption.getType() != null) {
            return true;
        }

        return false;
    }

    private boolean isGroupChanged(Option option, Option existingOption) {
        return !option.getOptionGroup().getId().equals(existingOption.getOptionGroup().getId());
    }

    private void updateParentVersion(OptionGroup optionGroup) {
        PersistenceUtils.performHibernateLock(em, optionGroup.getTemplate() != null ? optionGroup.getTemplate() : optionGroup.getCreativeSize());
    }

    // optionvalue_util.optionvalue type
    private static class OptionValueType  {
        private Long optionId;
        private String newValue;
        private String oldValue;

        public OptionValueType(Long optionId, String newValue, String oldValue) {
            this.optionId = optionId;
            this.newValue = newValue;
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public String getOldValue() {
            return oldValue;
        }

        public Long getOptionId() {
            return optionId;
        }

        @Override
        public String toString() {
            return "(" + optionId + "," + SQLUtil.escapeStructValue(newValue) + "," + SQLUtil.escapeStructValue(oldValue) + ")";
        }
    }
}
