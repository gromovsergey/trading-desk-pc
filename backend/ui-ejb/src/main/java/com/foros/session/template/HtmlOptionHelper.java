package com.foros.session.template;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.*;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.util.StringUtil;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class HtmlOptionHelper {
    public static final String HTTPS_SAFE_TOKEN = "HTTPS_SAFE";
    public static final String HTTPS_SAFE = "HTTPS-Safe";
    public static final String HTTP_ONLY = "HTTP-Only";
    private static final String TOKEN_SUFFIX = "_FILE";

    abstract protected EntityManager getEM();

    abstract protected LoggingJdbcTemplate getJdbcTemplate();

    private void createOptionValues(final Option htmlOption, final Option dynamicFileOption) {
        getJdbcTemplate().execute(
            "select optionvalue_util.create_dynamic_file_option_values(?::int, ?::int)",
            htmlOption.getId(), dynamicFileOption.getId()
            );
        getJdbcTemplate().scheduleEviction();
    }

    private void removeOptionValues(final Option option) {
        getJdbcTemplate().execute("select optionvalue_util.remove_option_values(?::int)", option.getId());
        getJdbcTemplate().scheduleEviction();
    }

    public Set<OptionLink> findLinks(Creative creative) {
        Set<OptionLink> res = new HashSet<>();
        res.addAll(findLinks(creative.getSize()));
        res.addAll(findLinks(creative.getTemplate()));
        return res;
    }

    public Set<OptionLink> findLinks(Template template) {
        return findLinks(template.getAdvertiserOptions(), template.getHiddenOptions());
    }

    public Set<OptionLink> findLinks(CreativeSize size) {
        return findLinks(size.getAdvertiserOptions(), size.getHiddenOptions());
    }

    private Set<OptionLink> findLinks(Collection<Option> advertiserOptions, Collection<Option> hiddenOptions) {
        Set<OptionLink> links = new HashSet<>(2);
        for (Option hiddenOption : hiddenOptions) {
            if (hiddenOption.getType() == OptionType.DYNAMIC_FILE && hiddenOption.getToken().endsWith(TOKEN_SUFFIX)) {
                for (Option advertiserOption : advertiserOptions) {
                    if (advertiserOption.getType() == OptionType.HTML
                            && (advertiserOption.getToken() + TOKEN_SUFFIX).equals(hiddenOption.getToken())) {
                        links.add(new OptionLink(advertiserOption, hiddenOption));
                    }
                }
            }
        }
        return links;
    }

    public void processHttpSafeOption(Creative creative) {
        Option httpSafeOption = findHttpSafeOption(creative);
        if (httpSafeOption == null) {
            return;
        }

        String httpSafeValue = isHttpOnlyCreative(creative) ? HTTP_ONLY : HTTPS_SAFE;
        CreativeOptionValue httpOptionValue = creative.findOptionValue(httpSafeOption);
        if (httpOptionValue == null) {
            httpOptionValue = createHttpOptionValue(creative, httpSafeOption);
        }
        String oldHttpSafeValue = httpOptionValue.getValue();
        httpOptionValue.setValue(httpSafeValue);

        boolean optionsChange = creative.isChanged("options");
        boolean valueChanged = false;
        if (httpSafeValue.equals(httpSafeOption.getDefaultValue())) {
            if (creative.getOptions().remove(httpOptionValue)) {
                getEM().remove(httpOptionValue);
                valueChanged = true;
            }
        } else {
            valueChanged = creative.getOptions().add(httpOptionValue) ||
                    !httpSafeValue.equals(oldHttpSafeValue);
        }

        if (!valueChanged && !optionsChange) {
            creative.unregisterChange("options");
        }
    }

    public static Option findHttpSafeOption(Creative creative) {
        for (Option option : creative.getTemplate().getHiddenOptions()) {
            if (HTTPS_SAFE_TOKEN.equals(option.getToken())) {
                return option;
            }
        }
        return null;
    }

    private CreativeOptionValue createHttpOptionValue(Creative creative, Option httpSafeOption) {
        CreativeOptionValue httpOptionValue = new CreativeOptionValue();
        httpOptionValue.setCreative(creative);
        httpOptionValue.setId(new CreativeOptionValuePK(creative.getId(), httpSafeOption.getId()));
        httpOptionValue.setOption(httpSafeOption);
        return httpOptionValue;
    }

    protected boolean isHttpOnlyCreative(Creative creative) {
        Set<CreativeOptionValue> options = getOptionValuesToCheck(creative);
        for (CreativeOptionValue optionValue : options) {
            if (isHttpOnlyOption(optionValue)) {
                return true;
            }
        }
        return false;
    }

    private Set<CreativeOptionValue> getOptionValuesToCheck(Creative creative) {
        Set<CreativeOptionValue> options = new HashSet<>(creative.getOptions());
        for (Option sizeOption : creative.getSize().getAdvertiserOptions()) {
            CreativeOptionValue optionValue = new CreativeOptionValue(creative.getId(), sizeOption.getId());
            optionValue.setCreative(creative);
            optionValue.setOption(sizeOption);
            optionValue.setValue(sizeOption.getDefaultValue());
            options.add(optionValue);
        }
        for (Option templateOption : creative.getTemplate().getAdvertiserOptions()) {
            CreativeOptionValue optionValue = new CreativeOptionValue(creative.getId(), templateOption.getId());
            optionValue.setCreative(creative);
            optionValue.setOption(templateOption);
            optionValue.setValue(templateOption.getDefaultValue());
            options.add(optionValue);
        }
        return options;
    }

    private boolean isHttpOnlyOption(CreativeOptionValue optionValue) {
        Option option = optionValue.getOption();
        boolean httpOnly = false;
        if ("CRCLICK".equals(option.getToken()) || "DESTURL".equals(option.getToken()) || "YANDEX_ADM".equals(option.getToken()) ) {
            return false;
        }
        switch (option.getType()) {
        case URL:
        case FILE_URL:
            if (StringUtil.isPropertyNotEmpty(optionValue.getValue()) && optionValue.getValue().startsWith("http://")) {
                httpOnly = true;
            }
            break;
        case HTML:
            if (StringUtil.isPropertyNotEmpty(optionValue.getValue()) && optionValue.getValue().contains("http://")) {
                httpOnly = true;
            }
            break;
        default:
            break;
        }
        return httpOnly;
    }

    public void processDynamicFiles(Creative creative) {
        Set<OptionLink> updatedLinks = findLinks(creative);

        for (CreativeOptionValue optionValue : creative.getOptions()) {
            Option option = optionValue.getOption();
            if (option.getType() == OptionType.DYNAMIC_FILE && option.getOptionGroup().getType() == OptionGroupType.Hidden) {
                OptionLink link = findByOption(updatedLinks, option);
                if (link == null) {
                    removeOptionLink(creative, optionValue);
                }
            }
        }

        HashSet<OptionValueLink> toBeUpdated = new HashSet<>();
        for (OptionLink updatedLink : updatedLinks) {
            CreativeOptionValue html = creative.findOptionValue(updatedLink.getHtml());
            CreativeOptionValue dynamicFile = creative.findOptionValue(updatedLink.getDynamicFile());
            if (html == null || StringUtil.isPropertyEmpty(html.getValue())) {
                if (dynamicFile != null) {
                    removeOptionLink(creative, dynamicFile);
                }
            } else {
                toBeUpdated.add(new OptionValueLink(updatedLink, html, dynamicFile));
            }
        }

        if (toBeUpdated.isEmpty()) {
            return;
        }

        getEM().flush();

        for (OptionValueLink optionValueLink : toBeUpdated) {
            CreativeOptionValue html = optionValueLink.getHtml();
            CreativeOptionValue dynamicFile = optionValueLink.getDynamicFile();
            String path = OptionValueUtils.getHtmlRoot(html.getCreative()) + HtmlOptionFileHelper.createFileName(html);

            if (dynamicFile == null) {
                Option dynamicFileOption = optionValueLink.getOptionLink().getDynamicFile();
                dynamicFile = new CreativeOptionValue(creative.getId(), dynamicFileOption.getId());
                dynamicFile.setOption(dynamicFileOption);
                dynamicFile.setValue(path);
                dynamicFile.setCreative(creative);
                getEM().persist(dynamicFile);
                creative.getOptions().add(dynamicFile);
            } else {
                dynamicFile.setValue(path);
            }
        }
    }

    public void processSize(CreativeSize creativeSize, Set<OptionLink> existingLinks) {
        Set<OptionLink> updatedLinks = findLinks(creativeSize);
        processLinks(existingLinks, updatedLinks);
    }

    public void processTemplate(Template template, Set<OptionLink> existingLinks) {
        Set<OptionLink> updatedLinks = findLinks(template);
        processLinks(existingLinks, updatedLinks);
    }

    public void processGroup(OptionGroup optionGroup, Set<OptionLink> existingLinks) {
        if (optionGroup.getTemplate() != null) {
            processTemplate(optionGroup.getTemplate(), existingLinks);
        } else if (optionGroup.getCreativeSize() != null) {
            processSize(optionGroup.getCreativeSize(), existingLinks);
        }

    }

    private OptionLink findByOption(Set<OptionLink> links, Option option) {
        for (OptionLink link : links) {
            if (link.getDynamicFile().getId().equals(option.getId()) || link.getHtml().getId().equals(option.getId())) {
                return link;
            }
        }
        return null;
    }

    private void removeOptionLink(Creative creative, CreativeOptionValue optionValue) {
        getEM().remove(optionValue);
        creative.getOptions().remove(optionValue);
    }

    private void processLinks(Set<OptionLink> existingLinks, Set<OptionLink> updatedLinks) {
        for (OptionLink existingLink : existingLinks) {
            if (!updatedLinks.contains(existingLink)) {
                removeOptionValues(existingLink.getDynamicFile());
            }
        }

        for (OptionLink updatedLink : updatedLinks) {
            if (!existingLinks.contains(updatedLink)) {
                createOptionValues(updatedLink.getHtml(), updatedLink.getDynamicFile());
            }
        }
    }

    public Set<OptionLink> findLinks(OptionGroup optionGroup) {
        if (optionGroup.getTemplate() != null) {
            return findLinks(optionGroup.getTemplate());
        } else if (optionGroup.getCreativeSize() != null) {
            return findLinks(optionGroup.getCreativeSize());
        } else {
            return new HashSet<>(0);
        }
    }

    public static class LinkSupport<T> {
        protected final T html;
        protected final T dynamicFile;

        public LinkSupport(T html, T dynamicFile) {
            this.html = html;
            this.dynamicFile = dynamicFile;
        }

        public T getHtml() {
            return html;
        }

        public T getDynamicFile() {
            return dynamicFile;
        }
    }

    public static class OptionLink extends LinkSupport<Option> {

        public OptionLink(Option html, Option dynamicFile) {
            super(html, dynamicFile);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            OptionLink link = (OptionLink) o;

            if (!dynamicFile.getId().equals(link.dynamicFile.getId()))
                return false;
            if (!html.getId().equals(link.html.getId()))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = html.getId().hashCode();
            result = 31 * result + dynamicFile.getId().hashCode();
            return result;
        }
    }

    public static class OptionValueLink extends LinkSupport<CreativeOptionValue> {
        private final OptionLink optionLink;

        public OptionValueLink(OptionLink optionLink, CreativeOptionValue html, CreativeOptionValue dynamicFile) {
            super(html, dynamicFile);
            this.optionLink = optionLink;
        }

        public OptionLink getOptionLink() {
            return optionLink;
        }
    }
}
