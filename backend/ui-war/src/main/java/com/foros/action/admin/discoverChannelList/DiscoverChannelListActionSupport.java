package com.foros.action.admin.discoverChannelList;

import com.foros.action.IdNameVersionForm;
import com.foros.action.LanguageBean;
import com.foros.action.admin.discoverChannel.AbstractDiscoverChannelActionSupport;
import com.foros.action.channel.behavioral.BehavioralParametersForm;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.util.BeanUtils;
import com.foros.util.CountryHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class DiscoverChannelListActionSupport extends AbstractDiscoverChannelActionSupport<DiscoverChannelList> {
    private List<IdNameVersionForm> channelsVersions = new LinkedList<IdNameVersionForm>();
    private List<IdNameVersionForm> channelsToLink = new LinkedList<IdNameVersionForm>();

    private List<LanguageBean> availableLanguages;
    private List<BehavioralParametersList> availableBehavioralParameters;

    public List<BehavioralParametersList> getAvailableBehavioralParameters() {
        return availableBehavioralParameters;
    }

    public List<LanguageBean> getAvailableLanguages() {
        return availableLanguages;
    }

    public List<IdNameVersionForm> getChannelsVersions() {
        return channelsVersions;
    }

    public void setChannelsVersions(List<IdNameVersionForm> channelsVersions) {
        this.channelsVersions = channelsVersions;
    }

    public List<IdNameVersionForm> getChannelsToLink() {
        return channelsToLink;
    }

    public void setChannelsToLink(List<IdNameVersionForm> channelsToLink) {
        this.channelsToLink = channelsToLink;
    }

    protected List<BehavioralParametersForm> populateBehavioralParameters() throws Exception {
        BehavioralParametersList behavParamsList = getModel().getBehavParamsList();
        if (behavParamsList == null) {
            return Collections.emptyList();
        }
        List<BehavioralParametersForm> list = new ArrayList<BehavioralParametersForm>(behavParamsList
                .getBehavioralParameters().size());
        Locale locale = CurrentUserSettingsHolder.getLocale();
        for (BehavioralParameters parameter : behavParamsList.getBehavioralParameters()) {
            BehavioralParametersForm bParamsForm = new BehavioralParametersForm();
            BeanUtils.copyProperties(bParamsForm, parameter, NumberFormat.getInstance(locale));
            bParamsForm.setEnabled(true);
            list.add(bParamsForm);
        }
        return list;
    }

    protected void populateDependenciesForSave() throws Exception {
        availableBehavioralParameters = behavioralParamsListService.findAll();
        availableLanguages = populateAvailableLanguages();
        setCountries(CountryHelper.sort(countryService.getIndex()));
        initChannelOwners();
    }

    protected void populateDependenciesForEdit() throws Exception {
        availableBehavioralParameters = behavioralParamsListService.findAll();
        availableLanguages = populateAvailableLanguages();
        setCountries(CountryHelper.sort(countryService.getIndex()));
    }

    @Override
    public Account getExistingAccount() {
        return model.getAccount();
    }
}
