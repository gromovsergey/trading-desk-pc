package com.foros.session.site;

import com.foros.model.Status;
import com.foros.model.account.PublisherAccount;
import com.foros.model.feed.Feed;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValue;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.Template;
import com.foros.session.BusinessException;
import com.foros.session.UrlValidations;
import com.foros.session.creative.FilesUrlsNotFoundException;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.template.TemplateService;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.url.URLValidator;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;

@LocalBean
@Stateless
@Validations
public class WDTagValidations {
    private static final String[] FEED_SCHEMAS = new String[]{"http", "feed", null};

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private WDTagService wdTagService;

    @EJB
    private TemplateService templateService;

    @EJB
    private UrlValidations urlValidations;

    private static final Long MAX_URL_LENGTH = 2000L;

    public static final String ONLY_COLOR_LETTERS = "^[1234567890ABCDEFabcdef]{6}+$";

    private static final Pattern COLOR_PATTERN = Pattern.compile(ONLY_COLOR_LETTERS);

    @Validation
    public void validateCreate(ValidationContext context,
                               @ValidateBean(ValidationMode.CREATE) WDTag wdTag) {
        checkTemplate(context, wdTag);
        validateUrls(context, "optedInUrls", wdTag.getOptedInFeeds(), wdTag.getOptedInOption());
        validateUrls(context, "optedOutUrls", wdTag.getOptedOutFeeds(), wdTag.getOptedOutOption());
        validatePassbackUrl(context, wdTag);
        validateOptions(wdTag, context);
        validateCheckFiles(context, wdTag);
    }

    @Validation
    public void validateUpdate(ValidationContext context,
                               @ValidateBean(ValidationMode.UPDATE) WDTag wdTag) {
        checkTemplate(context, wdTag);
        validateUrls(context, "optedInUrls", wdTag.getOptedInFeeds(), wdTag.getOptedInOption());
        validateUrls(context, "optedOutUrls", wdTag.getOptedOutFeeds(), wdTag.getOptedOutOption());
        validatePassbackUrl(context, wdTag);
        validateOptions(wdTag, context);
        validateCheckFiles(context, wdTag);
    }

    private void validateIntegerOption(Option existingOption, OptionValue option, ValidationContext context) {
        if (StringUtil.isPropertyEmpty(option.getValue())) {
            return;
        }

        long value;
        if (!NumberUtil.isLong(option.getValue())) {
            context
                    .addConstraintViolation("errors.field.integer")
                    .withPath("optionValues[" + existingOption.getId() + "].value");
            return;
        } else {
            value = NumberUtil.parseLong(option.getValue());
        }

        Long minValue = existingOption.getMinValue();
        Long maxValue = existingOption.getMaxValue();

        if (minValue != null && minValue > value) {
            context
                    .addConstraintViolation("errors.field.less")
                    .withParameters(minValue.toString())
                    .withPath("optionValues[" + existingOption.getId() + "].value");
        }

        if (maxValue != null && maxValue < value) {
            context
                    .addConstraintViolation("errors.field.notgreater")
                    .withParameters(maxValue.toString())
                    .withPath("optionValues[" + existingOption.getId() + "].value");
        }
    }

    private void checkTemplate(ValidationContext context, WDTag wdTag) {
        if (wdTag.getTemplate().getId() != null) {
            Template template = templateService.findById(wdTag.getTemplate().getId());
            if (template.getStatus() == Status.DELETED) {
                WDTag existingWdTag = null;
                if (wdTag.getId() != null) {
                    existingWdTag = wdTagService.find(wdTag.getId());
                }
                if (existingWdTag == null
                        || (!ObjectUtils.equals(wdTag.getTemplate().getId(), existingWdTag.getTemplate().getId()))) {
                    context.addConstraintViolation("errors.field.invalid").withPath("template");
                }
            }
        }
    }

    private void validateUrls(ValidationContext context, String fieldName, Set<Feed> feeds, WDTag.FeedOption feedOption) {
        if (WDTag.FeedOption.S == feedOption && feeds.size() == 0) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath(fieldName);
        } else {
            for (Feed feed : feeds) {
                String url = feed.getUrl().trim();
                if (url.length() > MAX_URL_LENGTH) {
                    context
                            .addConstraintViolation("errors.field.maxlength")
                            .withParameters(MAX_URL_LENGTH.toString())
                            .withPath(fieldName);
                    return;
                }

                urlValidations.validateUrl(context, url, fieldName, FEED_SCHEMAS, false);
            }
        }
    }

    private void validatePassbackUrl(ValidationContext context, WDTag wdTag) {
        if (WDTag.FeedOption.P == wdTag.getOptedOutOption() || StringUtil.isPropertyNotEmpty(wdTag.getPassbackUrl())) {
            if (!hasViolation("passbackUrl", context.getConstraintViolations())) {
                urlValidations.validateUrl(context, wdTag.getPassbackUrl(), "passbackUrl", false);
            }
        }
    }

    private void checkHtmlOption(String value, Option option, ValidationContext context) {
        try {
            if (value != null && value.getBytes("UTF-8").length > 2048) {
                context
                        .addConstraintViolation("errors.field.invalidMaxLengthExc")
                        .withPath("optionValues[" + option.getId() + "].value");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Validation
    public void validateCheckFiles(ValidationContext context, WDTag wdTag) {
        FilesUrlsNotFoundException exception = new FilesUrlsNotFoundException();
        PublisherAccount account = wdTag.getAccount();
        if (account == null) {
            account = em.find(Site.class, wdTag.getSite().getId()).getAccount();
        }

        DiscoverTemplate template = wdTag.getTemplate();
        if (template != null && template.getId() != null) {
            template = em.find(DiscoverTemplate.class, wdTag.getTemplate().getId());
        }
        Map<Long, OptionGroupState> groupsByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(wdTag.getGroupStates(), template, null, OptionGroupType.Publisher);

        for (OptionValue opt : wdTag.getOptions()) {
            String value = opt.getValue();
            long optionId = opt.getOptionId();

            OptionGroupState groupState = groupsByOptionId.get(opt.getOptionId());
            if (groupState != null && !groupState.getEnabled()) {
                continue;  // Skip validation for options from disabled groups.
            }

            Option existingOption = em.find(Option.class, optionId);

            // Deleted options will be checked by Service Bean, let's skip here
            if (existingOption == null) {
                continue;
            }

            if (StringUtil.isPropertyEmpty(value)) {
                if (existingOption.isRequired()) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("optionValues[" + existingOption.getId() + "].value");
                }
                continue;
            }
            switch (existingOption.getType()) {
                case STRING:
                case TEXT:
                    break;
                case HTML:
                    checkHtmlOption(value, existingOption, context);
                    break;
                case COLOR:
                    if (StringUtil.isPropertyNotEmpty(value) && !COLOR_PATTERN.matcher(value).matches()) {
                        context
                                .addConstraintViolation("Option.error.invalidColor")
                                .withPath("optionValues[" + existingOption.getId().toString() + "].value");
                    }
                    break;
                case INTEGER:
                    validateIntegerOption(existingOption, opt, context);
                    break;

                case FILE:
                case DYNAMIC_FILE:
                    try {
                        if (!getPublisherFS(account).checkExist(value)) {
                            exception.addFile(String.valueOf(optionId), value, OptionType.FILE);
                        } else if (!FileUtils.isFileAllowed(value, existingOption.getFileTypes())) {
                            exception.addNotAllowedFile(String.valueOf(optionId), value, OptionType.FILE);
                        }
                    } catch (BadNameException e) {
                        exception.addFile(String.valueOf(optionId), value, OptionType.FILE);
                    }
                    break;

                case URL:
                    if (!URLValidator.isValid(value)) {
                        exception.addUrl(String.valueOf(optionId), value, OptionType.URL);
                    }
                    break;

                case FILE_URL: {
                    if ((value.startsWith("http://") || value.startsWith("https://"))) {
                        if (!URLValidator.isValid(value)) {
                            exception.addUrl(String.valueOf(optionId), value, OptionType.FILE_URL);
                        }
                    } else {
                        try {
                            if (!getPublisherFS(account).checkExist(value)) {
                                exception.addFile(String.valueOf(optionId), value, OptionType.FILE_URL);
                            } else if (!FileUtils.isFileAllowed(value, existingOption.getFileTypes())) {
                                exception.addNotAllowedFile(String.valueOf(optionId), value, OptionType.FILE_URL);
                            }
                        } catch (BadNameException e) {
                            exception.addUrl(String.valueOf(optionId), value, OptionType.FILE_URL);
                        }
                    }
                    break;
                }

                case URL_WITHOUT_PROTOCOL:
                    urlValidations.validateUrl(context, value, "optionValues[" + optionId + "].value", UrlValidations.NO_SCHEMA, false);
                    break;

                case ENUM:
                    Set<String> optionValues = new HashSet<String>(existingOption.getValues().size());
                    for (OptionEnumValue optionValue : existingOption.getValues()) {
                        optionValues.add(optionValue.getValue());
                    }
                    if (!optionValues.contains(value)) {
                        throw new BusinessException("Invalid option value for option with id=" + opt.getOptionId());
                    }
                    break;

                default: {
                    throw new BusinessException("Not permitted option type.");
                }
            }
        }

        if (!exception.getFiles().isEmpty() || !exception.getUrls().isEmpty() || !exception.getNotAllowedFiles().isEmpty()) {
            processFilesUrlsNotFoundException(exception, context);
        }
    }

    private void processFilesUrlsNotFoundException(FilesUrlsNotFoundException e, ValidationContext context) {
        Map<String, String> files = e.getFiles();
        Map<String, OptionType> fileOptionTypes = e.getFileOptionTypes();
        for (String key : files.keySet()) {
            String fileName = StringEscapeUtils.escapeHtml(files.get(key));

            if (fileOptionTypes.get(key) != OptionType.FILE_URL) {
                context
                        .addConstraintViolation("errors.fileNotFound")
                        .withParameters(fileName)
                        .withPath("files[" + key + "]");
            } else {
                context
                        .addConstraintViolation("errors.invalidfileorurl")
                        .withParameters(fileName)
                        .withPath("fileUrls[" + key + "]");
            }
        }

        Map<String, String> urls = e.getUrls();
        Map<String, OptionType> urlOptionTypes = e.getUrlOptionTypes();
        for (String key : urls.keySet()) {
            String url = urls.get(key);

            if (urlOptionTypes.get(key) != OptionType.FILE_URL) {
                context
                        .addConstraintViolation("errors.invalidurl")
                        .withParameters(url)
                        .withPath("urls[" + key + "]");
            } else {
                context
                        .addConstraintViolation("errors.invalidurl")
                        .withParameters(url)
                        .withPath("fileUrls[" + key + "]");
            }
        }

        Map<String, String> naFiles = e.getNotAllowedFiles();
        for (String key : naFiles.keySet()) {
            String fileExtension = StringEscapeUtils.escapeHtml(FileUtils.getExtension(naFiles.get(key)));

            if (fileOptionTypes.get(key) != OptionType.FILE_URL) {
                context
                        .addConstraintViolation("errors.fileTypeIsNotAllowed")
                        .withParameters(fileExtension)
                        .withPath("files[" + key + "]");
            } else {
                context
                        .addConstraintViolation("errors.fileTypeIsNotAllowed")
                        .withParameters(fileExtension)
                        .withPath("fileUrls[" + key + "]");
            }
        }
    }

    private FileSystem getPublisherFS(PublisherAccount account) {
        String accountFolderName = OptionValueUtils.getPublisherRoot(account);
        PathProvider pathProvider = pathProviderService.getPublisherAccounts().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return pathProviderService.createFileSystem(pathProvider);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void validateOptions(WDTag wdTag, ValidationContext context) {
        if (wdTag.getTemplate() != null && wdTag.getTemplate().getId() != null) {
            DiscoverTemplate template = em.find(DiscoverTemplate.class, wdTag.getTemplate().getId());

            Collection allOptions = CollectionUtils.collect(template.getPublisherOptions(), new Transformer() {
                @Override
                public Object transform(Object input) {
                    return ((Option) input).getId();
                }
            });

            Collection options = CollectionUtils.collect(wdTag.getOptions(), new Transformer() {
                @Override
                public Object transform(Object input) {
                    WDTagOptionValue wdTagOptionValue = (WDTagOptionValue) input;
                    return wdTagOptionValue.getOptionId();

                }
            });
            if (!allOptions.containsAll(options)) {
                context
                        .addConstraintViolation("wdTag.invalid.options")
                        .withPath("options");
            }
        }
    }

    private boolean hasViolation(String path, Set<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            if (ObjectUtils.equals(path, violation.getPropertyPath().toString())) {
                return true;
            }
        }
        return false;
    }
}
