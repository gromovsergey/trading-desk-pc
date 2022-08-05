package com.foros.action.admin.discoverChannel;

import com.foros.action.channel.behavioral.BehavioralParametersForm;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.DiscoverChannel;
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

import org.apache.commons.lang.StringUtils;

public abstract class DiscoverChannelActionSupport extends AbstractDiscoverChannelActionSupport<DiscoverChannel> {
    private boolean pageKeywordsOff;
    private boolean searchKeywordsOff;
    private boolean urlOff;
    private Long keywordsId;
    private Long urlsId;
    private String keywordsVersion;
    private String urlsVersion;

    protected List<BehavioralParametersForm> behavioralParameters = new LinkedList<BehavioralParametersForm>();
    private List<BehavioralParametersList> availableBehavioralParameters;

    public List<BehavioralParametersList> getAvailableBehavioralParameters() {
        return availableBehavioralParameters;
    }

    public List<BehavioralParametersForm> getBehavioralParameters() {
        return behavioralParameters;
    }

    public boolean isPageKeywordsOff() {
        return pageKeywordsOff;
    }

    public void setPageKeywordsOff(boolean pageKeywordsOff) {
        this.pageKeywordsOff = pageKeywordsOff;
    }

    public boolean isSearchKeywordsOff() {
        return searchKeywordsOff;
    }

    public void setSearchKeywordsOff(boolean searchKeywordsOff) {
        this.searchKeywordsOff = searchKeywordsOff;
    }

    public boolean isUrlOff() {
        return urlOff;
    }

    public void setUrlOff(boolean urlOff) {
        this.urlOff = urlOff;
    }

    public Long getKeywordsId() {
        return keywordsId;
    }

    public void setKeywordsId(Long keywordsId) {
        this.keywordsId = keywordsId;
    }

    public Long getUrlsId() {
        return urlsId;
    }

    public void setUrlsId(Long urlsId) {
        this.urlsId = urlsId;
    }

    public String getKeywordsVersion() {
        return keywordsVersion;
    }

    public void setKeywordsVersion(String keywordsVersion) {
        this.keywordsVersion = keywordsVersion;
    }

    public String getUrlsVersion() {
        return urlsVersion;
    }

    public void setUrlsVersion(String urlsVersion) {
        this.urlsVersion = urlsVersion;
    }

    protected void populateTriggerStatus() {
        BehavioralParametersList behavParamsList = getModel().getBehavParamsList();
        if (behavParamsList == null) {
            return;
        }
        List<BehavioralParameters> parameterForms = behavParamsList.getBehavioralParameters();
        boolean parameterTypePageKeyword = false;
        boolean parameterTypeSearchKeyword = false;
        boolean parameterTypeUrl = false;

        for (BehavioralParameters parameter : parameterForms) {
            switch (parameter.getTriggerType()) {
                case 'P':
                    parameterTypePageKeyword = true;
                    break;
                case 'S':
                    parameterTypeSearchKeyword = true;
                    break;
                case 'U':
                    parameterTypeUrl = true;
                    break;
            }
            if (parameterTypePageKeyword && parameterTypeSearchKeyword && parameterTypeUrl) {
                break;
            }
        }

        setPageKeywordsOff(!parameterTypePageKeyword && StringUtils.isNotBlank(getPageKeywords()));
        setSearchKeywordsOff(!parameterTypeSearchKeyword && StringUtils.isNotBlank(getSearchKeywords()));
        setUrlOff(!parameterTypeUrl && StringUtils.isNotBlank(getUrls()));
    }

    protected List<BehavioralParametersForm> populateBehavioralParameters() throws Exception {
        BehavioralParametersList behavParamsList = getModel().getBehavParamsList();

        if (behavParamsList == null || behavParamsList.getId() == null) {
            return Collections.emptyList();
        }

        behavParamsList = behavioralParamsListService.findWithNoErrors(behavParamsList.getId());

        if (behavParamsList == null) {
            getModel().setBehavParamsList(null);
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
        behavioralParameters = populateBehavioralParameters();
        loadAvailableLanguages();
        setCountries(CountryHelper.sort(countryService.getIndex()));
        initChannelOwners();
    }

    protected void populateDependenciesForEdit() throws Exception {
        availableBehavioralParameters = behavioralParamsListService.findAll();
        behavioralParameters = populateBehavioralParameters();
        loadAvailableLanguages();
        setCountries(CountryHelper.sort(countryService.getIndex()));
    }

    @Override
    public Account getExistingAccount() {
        return model.getAccount();
    }
}
