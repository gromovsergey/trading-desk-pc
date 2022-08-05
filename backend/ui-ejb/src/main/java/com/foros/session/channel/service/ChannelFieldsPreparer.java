package com.foros.session.channel.service;

import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersChannel;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.BehavioralParametersListChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.LanguageChannel;
import com.foros.model.channel.TriggersChannel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;

public abstract class ChannelFieldsPreparer {

    protected abstract EntityManager getEM();

    public void prepareAccount(Channel channel) {
        if (channel.getAccount() != null) {
            channel.getAccount().unregisterChange("id");
            channel.setAccount(getEM().find(Account.class, channel.getAccount().getId()));
        }
    }

    public void prepareCountry(Channel channel) {
        if (channel.getCountry() != null) {
            channel.setCountry(getEM().getReference(Country.class, channel.getCountry().getCountryCode()));
        }
    }

    public void prepareCategoryChannel(TriggersChannel channel) {
        if (channel.isChanged("categories") && channel.getCategories() != null && !channel.getCategories().isEmpty()) {
            Set<CategoryChannel> oldCategories = channel.getCategories();
            Set<CategoryChannel> preparedCategories = new HashSet<CategoryChannel>(oldCategories.size());
            for (CategoryChannel categoryChannel : oldCategories) {                
                CategoryChannel existingCategoryChannel = getEM().getReference(CategoryChannel.class, categoryChannel.getId());
                preparedCategories.add(existingCategoryChannel);
            }
            channel.setCategories(preparedCategories);
        }
    }

    public static void initializeVisibilityAndRate(Channel channel) {
        if (channel.getVisibility() == null) {
            channel.setVisibility(ChannelUtils.getDefaultVisibility());
        }
        channel.setChannelRate(null);
    }

    public static void initializeStatuses(Channel channel) {
        if (!channel.isChanged("status")) {
            channel.setStatus(ChannelUtils.getDefaultStatus());
        }
        channel.setStatusChangeDate(new Date());

        // real status to be calculated in SP
        channel.setDisplayStatus(Channel.PENDING_FOROS);
    }

    public static void initializeQaStatus(Channel channel) {
        channel.setQaStatus(ApproveStatus.APPROVED);
        channel.setQaDescription(null);
        channel.setQaDate(null);
        channel.setQaUser(null);
    }

    public static void prepareBehavioralParameters(BehavioralParametersChannel channel) {
        if (channel.getBehavioralParameters() != null) {
            for (BehavioralParameters behavioralParameters : channel.getBehavioralParameters()) {
                behavioralParameters.setChannel((Channel) channel);
            }
        }
    }

    public <T extends Channel & BehavioralParametersListChannel> void prepareBehavParamsList(T channel) {
        BehavioralParametersList paramsList = channel.getBehavParamsList();
        if (channel.isChanged("behavParamsList") && paramsList != null) {
            channel.setBehavParamsList(getEM().getReference(BehavioralParametersList.class, paramsList.getId()));
        }
    }

    public static void initializeId(Channel channel) {
        channel.setId(null);
    }

    public <T extends Channel & LanguageChannel> void initializeLanguage(T channel) {
        if (channel.isChanged("language")) {
            // use supplied language
            return;
        }

        Country country = getEM().find(Country.class, channel.getCountry().getCountryCode());
        String language = country.getLanguage();
        if (language == null) {
            language = "en";
        }

        channel.setLanguage(language);
    }
}
