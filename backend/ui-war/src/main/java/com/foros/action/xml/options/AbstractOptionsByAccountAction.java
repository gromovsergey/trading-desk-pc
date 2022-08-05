package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.AbstractConverter;
import com.foros.action.xml.options.filter.Filter;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;
import com.foros.util.annotation.AnnotationUtil;

import java.util.Collection;

import javax.ejb.EJB;

public abstract class AbstractOptionsByAccountAction<T> extends AbstractOptionsAction<T> {
    @EJB
    protected AccountService accountService;

    private static class PairFetcher implements AnnotationUtil.Fetcher<AccountId, String, Long> {
        @Override
        public Long fetch(String value, AccountId annotation) {
            if (StringUtil.isPropertyNotEmpty(value)) {
                if (annotation.isPair())
                    return PairUtil.fetchId(value);
                else
                    return Long.parseLong(value);
            } else {
                return null;
            }
        }

    }

    protected AbstractOptionsByAccountAction(AbstractConverter<? super T> converter) {
        super(converter);
    }

    protected AbstractOptionsByAccountAction(AbstractConverter<? super T> converter, Filter<? super T> filter) {
        super(converter, filter);
    }

    @Override
    protected Collection<? extends T> getOptions() throws ProcessException {
        Long id = fetchAccountId();
        checkAccountId(id);
        return getOptionsByAccount(id);
    }

    protected void checkAccountId(Long id) throws ProcessException {
        if (!SecurityContext.isInternal()) {
            Long myAccountId = accountService.getMyAccount().getId();
            if (!isAccountIdValid(myAccountId, id)) {
                throw new ProcessException("Security exception. You are trying to get data from foreign account." +
                        " You id=" + myAccountId + ", request id=" + id);
            }
        }
    }

    private boolean isAccountIdValid(Long myAccountId, Long accountId) {
        Account account = accountService.find(accountId);

        if(AccountRole.ADVERTISER.equals(account.getRole())) {
            //account is of advertiser type
            AdvertiserAccount advertiserAccount = (AdvertiserAccount) account;

            if(advertiserAccount.isInAgencyAdvertiser()) {
                //advertiser has an agency
                return advertiserAccount.getAgency().getId().equals(myAccountId);
            }
        }

        return myAccountId.equals(accountId);
    }

    protected abstract Collection<? extends T> getOptionsByAccount(Long accountId);

    protected Long fetchAccountId() throws ProcessException {
        return AnnotationUtil.fetchAnnotatedValue(this, AccountId.class, new PairFetcher());
    }

    public void setConcatResultForValue(boolean concatResultForValue) {
        getConverter().setConcatForValue(concatResultForValue);
    }

    @Override
    public AbstractConverter<? super T> getConverter() {
        return (AbstractConverter<? super T>) super.getConverter();
    }
}
