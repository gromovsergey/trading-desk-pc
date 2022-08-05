package com.foros.session.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.channel.trigger.KeywordTrigger;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.session.UrlValidations;
import com.foros.util.TriggerUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.validator.XmlAllowableValidator;

import java.util.Collection;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class BaseTriggerListValidations {

    private static final Pattern BALANCED_QUOTE_PATTERN = Pattern.compile("[^\"]*([^\"]*\"[^\"]*\"[^\"]*)*");
    private static final Pattern PAGE_SQUARE_BRACKETS_PATTERN = Pattern.compile("^[^\\[\\]]*$");
    private static final Pattern SEARCH_SQUARE_BRACKETS_PATTERN = Pattern.compile("^\\[[^\\[\\]]+\\]$|^[^\\[\\]]*$");

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private UrlValidations urlValidations;

    public <T extends TriggersChannel> void validateTriggersRequired(ValidationContext context,
                                         T channel, T existing,
                                         Collection<BehavioralParameters> effectiveParameters) {

        if (effectiveParameters == null) {
            return;
        }

        if (TriggerType.findBehavioralParameters(effectiveParameters, TriggerType.PAGE_KEYWORD) != null) {
            if (channel.getPageKeywords().isEmpty()) {
                context.addConstraintViolation("errors.atLeastOneKeywordIsRequired").withPath("pageKeywords");
            }
        }

        if (TriggerType.findBehavioralParameters(effectiveParameters, TriggerType.SEARCH_KEYWORD) != null) {
            if (channel.getSearchKeywords().isEmpty()) {
                context.addConstraintViolation("errors.atLeastOneKeywordIsRequired").withPath("searchKeywords");
            }
        }

        if (TriggerType.findBehavioralParameters(effectiveParameters, TriggerType.URL) != null) {
            if (channel.getUrls().isEmpty()) {
                context.addConstraintViolation("errors.atLeastOneUrlIsRequired").withPath("urls");
            }
        }

        if (TriggerType.findBehavioralParameters(effectiveParameters, TriggerType.URL_KEYWORD) != null) {
            if (channel.getUrlKeywords().isEmpty()) {
                context.addConstraintViolation("errors.atLeastOneKeywordIsRequired").withPath("urlKeywords");
            }
        }

    }

    public void validate(ValidationContext context, TriggersChannel channel, TriggersChannel existing) {
        Account account = null;
        if (existing != null) {
            account = existing.getAccount();
        } else if (context.isReachable("account") && channel.getAccount() != null){ // todo!!!
            account = em.find(Account.class, channel.getAccount().getId());
        }
        TriggerListValidationRules rules =
                account != null ? new TriggerListValidationRules(account) : TriggerListValidationRules.DEFAULT_RULES;

        int keywordTriggersCount = 0;
        if (context.isReachable("pageKeywords")) {
            keywordTriggersCount += channel.getPageKeywords().size();
        } else if (existing != null) {
            keywordTriggersCount += existing.getPageKeywords().size();
        }

        if (context.isReachable("searchKeywords")) {
            keywordTriggersCount += channel.getSearchKeywords().size();
        } else if (existing != null) {
            keywordTriggersCount += existing.getSearchKeywords().size();
        }

        if (context.isReachable("urlKeywords")) {
            keywordTriggersCount += channel.getUrlKeywords().size();
        } else if (existing != null) {
            keywordTriggersCount += existing.getUrlKeywords().size();
        }

        if (context.isReachable("pageKeywords") || context.isReachable("searchKeywords") || context.isReachable("urlKeywords")) {
            if (!rules.isValidMaxKeywordsPerChannel(keywordTriggersCount)) {
                context.addConstraintViolation("channel.errors.maxKeywords")
                    .withParameters(rules.getMaxKeywordsPerChannel());
            }
        }

        if (context.isReachable("pageKeywords")) {
            validateKeywordTriggerList(context, "pageKeywords.positive", channel.getPageKeywords().getPositive(), rules);
            validateKeywordTriggerList(context, "pageKeywords.negative", channel.getPageKeywords().getNegative(), rules);
        }

        if (context.isReachable("searchKeywords")) {
            validateKeywordTriggerList(context, "searchKeywords.positive", channel.getSearchKeywords().getPositive(), rules);
            validateKeywordTriggerList(context, "searchKeywords.negative", channel.getSearchKeywords().getNegative(), rules);
        }

        if (context.isReachable("urls")) {
            if (!rules.isValidMaxUrlsPerChannel(channel.getUrls().getPositive().size() + channel.getUrls().getNegative().size())) {
                context.addConstraintViolation("channel.errors.maxUrls")
                        .withParameters(rules.getMaxUrlsPerChannel())
                        .withPath("urls");
            }
            validateUrlTriggers(context, "urls.positive", channel.getUrls().getPositive(), rules);
            validateUrlTriggers(context, "urls.negative", channel.getUrls().getNegative(), rules);
        }

        if (context.isReachable("urlKeywords")) {
            validateKeywordTriggerList(context, "urlKeywords.positive", channel.getUrlKeywords().getPositive(), rules);
            validateKeywordTriggerList(context, "urlKeywords.negative", channel.getUrlKeywords().getNegative(), rules);
        }
    }

    public void validateKeywordTriggerList(ValidationContext context, String path,
                                           Collection<? extends KeywordTrigger> keywordTriggers,
                                           TriggerListValidationRules rules) {
        if (keywordTriggers.isEmpty()) {
            return;
        }

        int index = 0;
        for (KeywordTrigger trigger : keywordTriggers) {
            validateKeywordTrigger(context, trigger, path + "[" + index++ + "]", rules);
        }
    }

    public void validateKeywordTrigger(ValidationContext context, KeywordTrigger trigger, String path, TriggerListValidationRules rules) {
        try {
            trigger.getNormalized();
        } catch (Exception e) {
            context.addConstraintViolation("errors.invalidKeyword")
                    .withParameters(trigger.getOriginal())
                    .withValue(trigger.getOriginal())
                    .withPath(path);
            return;
        }
        if (trigger.getBytes().length > TriggerUtil.MAX_KEYWORD_LENGTH) {
            context.addConstraintViolation("errors.keyword.bytesLength")
                    .withParameters(trigger.getOriginal())
                    .withValue(trigger.getOriginal())
                    .withPath(path);
        } else if (trigger.getOriginal().length() > rules.getMaxKeywordLength()) {
            context.addConstraintViolation("errors.keyword.maxlength")
                    .withParameters(trigger.getOriginal(), rules.getMaxKeywordLength())
                    .withValue(trigger.getOriginal())
                    .withPath(path);
        } else if (TriggerUtil.NEWLINE_PATTERN.matcher(trigger.getOriginal()).find()) {
            context.addConstraintViolation("errors.keyword.newLine")
                    .withParameters(trigger.getOriginal())
                    .withValue(trigger.getOriginal())
                    .withPath(path);
        } else if (!BALANCED_QUOTE_PATTERN.matcher(trigger.getOriginal()).matches()) {
            context.addConstraintViolation("errors.keyword.unbalancedQuote")
                    .withParameters(trigger.getOriginal())
                    .withValue(trigger.getOriginal())
                    .withPath(path);
        }

        switch (trigger.getTriggerType()) {
            case PAGE_KEYWORD:
            case URL_KEYWORD:
                if (!PAGE_SQUARE_BRACKETS_PATTERN.matcher(trigger.getTrimmed()).matches()) {
                    context.addConstraintViolation("errors.keyword.squareBracketsNotAllowed")
                            .withParameters(trigger.getOriginal())
                            .withValue(trigger.getOriginal())
                            .withPath(path);
                }
                break;
            case SEARCH_KEYWORD:
                if (!SEARCH_SQUARE_BRACKETS_PATTERN.matcher(trigger.getTrimmed()).matches()) {
                    context.addConstraintViolation("errors.keyword.squareBracketsIncorrectPlace")
                            .withParameters(trigger.getOriginal())
                            .withValue(trigger.getOriginal())
                            .withPath(path);
                }
                break;
            default:
                break;
        }

        context.validator(XmlAllowableValidator.class)
                .withPath(path)
                .validate(trigger.getOriginal());
    }

    public void validateUrlTriggers(ValidationContext context, String propertyPath, Collection<UrlTrigger> triggers, TriggerListValidationRules rules) {
        if (triggers.isEmpty()) {
            return;
        }

        int index = 0;
        for (UrlTrigger trigger : triggers) {
            String path = propertyPath + "[" + index++ + "]";

            if (trigger.getBytes().length > TriggerUtil.MAX_URL_LENGTH) {
                context.addConstraintViolation("errors.url.bytesLength")
                        .withValue(trigger.getOriginal())
                        .withPath(path);
                continue;
            }

            if (trigger.getOriginal().length() > rules.getMaxUrlLength()) {
                context.addConstraintViolation("errors.field.maxlength")
                        .withValue(trigger.getOriginal())
                        .withParameters(rules.getMaxUrlLength())
                        .withValue(trigger.getOriginal())
                        .withPath(path);
                continue;
            }

            urlValidations.validateTriggerUrl(context, trigger.getOriginal(), path);

            context.validator(XmlAllowableValidator.class)
                    .withPath(path)
                    .validate(trigger.getOriginal());
        }
    }
}
