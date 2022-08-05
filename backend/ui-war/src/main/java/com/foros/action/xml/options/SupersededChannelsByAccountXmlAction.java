package com.foros.action.xml.options;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.SearchChannelService;

import javax.ejb.EJB;
import java.util.Collection;

public class SupersededChannelsByAccountXmlAction extends AbstractOptionsByAccountAction<EntityTO> {

    @EJB
    private SearchChannelService searchChannelService;

    private String accountId;
    private String countryCode;
    private Long selectedId;
    private Long selfId;
    private String query;

    public SupersededChannelsByAccountXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @AccountId
    @RequiredStringValidator(key = "errors.required", message = "accountId")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @RequiredStringValidator(key = "errors.required", message = "countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(Long selectedId) {
        this.selectedId = selectedId;
    }

    @RequiredStringValidator(key = "errors.required", message = "selfId")
    public Long getSelfId() {
        return selfId;
    }

    public void setSelfId(Long selfId) {
        this.selfId = selfId;
    }

    @RequiredStringValidator(key = "errors.required", message = "query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return searchChannelService.findSupersededChannelsByAccountAndCountry(accountId, countryCode, selectedId, selfId, query, AUTOCOMPLETE_SIZE);
    }
}
