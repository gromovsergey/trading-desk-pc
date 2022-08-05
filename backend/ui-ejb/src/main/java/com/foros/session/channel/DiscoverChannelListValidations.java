package com.foros.session.channel;

import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.session.BeanValidations;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless
@Validations
public class DiscoverChannelListValidations extends CommonChannelValidations {

    @EJB
    private BaseDiscoverChannelValidations baseValidations;

    @EJB
    private DiscoverChannelValidations discoverChannelValidations;

    @EJB
    private BaseTriggerListValidations baseTriggerListValidations;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private LanguageChannelValidations languageChannelValidations;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        validate(context.createSubContext(channel), channel, channelsToLink);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        validate(context.createSubContext(channel), channel, channelsToLink);
    }

    @Validation
    public void validateUpdateLinked(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) DiscoverChannel channel) {
        validateChannelName(context, channel);
        UploadUtils.addConstraintViolations(context, channel);

        if (UploadUtils.hasFatalError(channel)) {
            return;
        }

        DiscoverChannel existing = validateId(context, channel);
        if (existing == null) {
            return;
        }

        if (StringUtils.isBlank(channel.getBaseKeyword())) {
            context.addConstraintViolation("errors.field.required")
            .withPath("keywords")
            .withValue(channel.getBaseKeyword());
        }
        discoverChannelValidations.validateListChild(context, channel);
    }

    @Validation
    public void validateLink(ValidationContext context, DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        validateLinkInternal(context.createSubContext(channel), channel, channelsToLink);
    }

    private void validateLinkInternal(ValidationContext context, DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        int size = StringUtil.splitAndTrim(channel.getKeywordList()).length;
        size += channelsToLink.size();
        if (size > 2000) {
            context.addConstraintViolation("errors.discoverList.channelsCount").withPath("keywords");
            return;
        }

        DuplicateChecker<DiscoverChannel> duplicateIdChecker = DuplicateChecker.create(new EntityIdFetcher<DiscoverChannel>());
        DuplicateChecker<DiscoverChannel> duplicateNameChecker = DuplicateChecker.create(new DuplicateChecker.NameFetcher<DiscoverChannel>());

        duplicateNameChecker.populateIdentifiers(channel.getChildChannels());

        int index = 0;
        for (DiscoverChannel toLink : channelsToLink) {
            ValidationContext subContext = context.createSubContext(toLink, "channelsToLink", index++);

            // add base keyword parsing errors if any
            UploadUtils.addConstraintViolations(subContext);

            if (UploadUtils.hasFatalError(toLink)) {
                continue;
            }

            duplicateIdChecker.check(subContext, "id", toLink);
            if (!duplicateNameChecker.check(toLink)) {
                subContext.addConstraintViolation("errors.discoverList.duplicateChannels")
                    .withPath("name")
                    .withValue(toLink.getName())
                    .withParameters(toLink.getName());
            }

            validateLink(context, subContext, channel, toLink);
        }
    }

    @Validation
    public void validateUnlink(ValidationContext context, DiscoverChannel channelToUnlink) {
        if (channelToUnlink.getChannelList() == null) {
            context.addConstraintViolation("errors.discoverChannel.alreadyUnlinked")
                    .withPath("channelList");
        }
    }

    private void validateLink(ValidationContext context, ValidationContext childContext, DiscoverChannelList channel, DiscoverChannel toLink) {
        DiscoverChannel existing = validateId(childContext, toLink);
        if (existing == null) {
            return;
        }

        // should be in same account
        if (!channel.getAccount().equals(existing.getAccount())) {
            childContext.addConstraintViolation("errors.field.invalid")
                .withPath("account")
                .withValue(existing.getAccount());
        }

        // not linked to the list
        if (channel.equals(existing.getChannelList())) {
            childContext.addConstraintViolation("errors.field.invalid")
                .withPath("channelList")
                .withValue(existing.getChannelList());
        }

        if (!ObjectUtils.equals(existing.getVersion(), toLink.getVersion())) {
            childContext.addConstraintViolation("errors.version")
                .withValue(toLink.getVersion())
                .withPath("version");
        }

        if (StringUtils.isBlank(toLink.getBaseKeyword())) {
            context.addConstraintViolation("errors.field.required")
            .withPath("keywords")
            .withValue(toLink.getBaseKeyword());
        }

        discoverChannelValidations.validateListChild(childContext, toLink);
    }

    private DiscoverChannel validateId(ValidationContext childContext, DiscoverChannel toLink) {
        LinkValidator<DiscoverChannel> validator =
                beanValidations.linkValidator(childContext, DiscoverChannel.class)
                        .withRequired(true)
                        .withPath("id");

        validator.validate(toLink);

        return validator.getEntity();
    }


    private void validate(ValidationContext context, DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        // add parsing errors if any
        UploadUtils.addConstraintViolations(context);

        validateChannelName(context, channel);
        validateChannelNameMacro(context, channel);
        baseValidations.validateBehavioralParameters(context, channel);
        baseValidations.validateCountry(context, channel);
        languageChannelValidations.validate(context, channel);
        validateChildChannels(context, channel, channelsToLink);
        if (!context.hasViolations()) {
            validateLinkInternal(context, channel, channelsToLink);
        }
    }

    private void validateChildChannels(ValidationContext context, DiscoverChannelList channel, Collection<DiscoverChannel> channelsToLink) {
        if (!context.isReachable("childChannels")) {
            return;
        }

        String[] ignore = calculateIgnoredFields(context);

        int index = 0;
        DuplicateChecker<DiscoverChannel> duplicateChecker = DuplicateChecker.create(new DuplicateChecker.NameFetcher<DiscoverChannel>());
        for (DiscoverChannel child : channel.getChildChannels()) {
            ValidationContext childContext = context.createSubContext(child, "childChannels", index);

            UploadUtils.addConstraintViolations(childContext);

            if (!UploadUtils.hasFatalError(child)) {
                duplicateChecker.check(childContext, "name", child);
                // do not validate them.
                child.unregisterChange(ignore);
                discoverChannelValidations.validateListChild(childContext, child);
            }
            index++;
        }
    }

    private void validateChannelNameMacro(ValidationContext context, DiscoverChannelList channel) {
        String macro = channel.getChannelNameMacro();
        if (context.isReachable("channelNameMacro") && macro != null && !macro.isEmpty()) {
            if (!macro.contains(DiscoverChannelList.KEYWORD_TOKEN)) {
                context.addConstraintViolation("error.discoverList.field.channelNameMacro.mask")
                    .withPath("channelNameMacro");
            }
        }
    }

    private String[] calculateIgnoredFields(ValidationContext context) {
        ArrayList<String> ignore = new ArrayList<String>();
        if (!context.props("channelNameMacro").reachableAndNoViolations()) {
            ignore.add("name");
        }
        if (!context.props("keywordTriggerMacro").reachableAndNoViolations()) {
            ignore.add("keywordList");
        }
        if (!context.props("discoverQuery").reachableAndNoViolations()) {
            ignore.add("discoverQuery");
        }
        if (!context.props("discoverAnnotation").reachableAndNoViolations()) {
            ignore.add("discoverAnnotation");
        }
        if (!context.props("description").reachableAndNoViolations()) {
            ignore.add("description");
        }
        return ignore.toArray(new String[ignore.size()]);
    }

    @Validation
    public void validateNameConstraintViolation(ValidationContext context, DiscoverChannelList dcList) {
        Query query = em.createQuery("select count(list) from DiscoverChannelList list " +
                " where list.id <> :listId " +
                " and lower(list.name) = :name " +
                " and list.country.countryCode = :countryCode " +
                " and list.account.id = :accountId ")
                .setParameter("listId", dcList.getId())
                .setParameter("name", StringUtil.trimAndLower(dcList.getName()))
                .setParameter("countryCode", dcList.getCountry().getCountryCode())
                .setParameter("accountId", dcList.getAccount().getId());
        if ((Long) query.getSingleResult() > 0) {
            context
                    .addConstraintViolation("errors.duplicate")
                    .withPath("name")
                    .withParameters("{DiscoverChannelList.name}");
        }
    }
}
