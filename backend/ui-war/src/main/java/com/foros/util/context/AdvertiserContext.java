package com.foros.util.context;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.session.ServiceLocator;
import com.foros.session.finance.AdvertisingFinanceService;

import java.io.Serializable;
import java.math.BigDecimal;

public class AdvertiserContext extends ContextBase<AdvertisingAccountBase> implements Serializable {

    private AgencyAdvertiserContext subContext;

    @Override
    public boolean switchTo(Long accountId) {
         return switchTo(AdvertisingAccountBase.class, accountId);
    }

    private boolean switchTo(Class<? extends AdvertisingAccountBase> clazz, Long accountId) {
        if (isSwitchedTo(accountId)) {
            return false;
        }

        switchInternal(findAccount(clazz, accountId));
        return true;
    }

    public boolean switchToAdvertiser(Long accountId) {
        return switchTo(findAccount(AdvertiserAccount.class, accountId));
    }

    public boolean switchToAgency(Long accountId) {
        return switchTo(findAccount(AgencyAccount.class, accountId));
    }

    @Override
    public boolean switchTo(AdvertisingAccountBase accountBase) {
        if (isSwitchedTo(accountBase.getId())) {
            return false;
        }
        switchInternal(accountBase);
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        if (subContext != null) {
            subContext.clear();
        }
    }

    @Override
    protected String getNoContextMessageKey() {
        return "error.context.notset.advertiser";
    }

    public void clearAgencyContext() {
        checkSubContext();
        subContext.clear();
    }
    public boolean isAgencyContext() {
        checkSet();
        return subContext != null;
    }

    public Long getAgencyAdvertiserId() {
        checkSubContext();
        return subContext.getAccountId();
    }

    public boolean isAdvertiserSet() {
        if (!isSet()) {
            return false;
        }

        if (isAgencyContext()) {
            return isAgencyAdvertiserSet();
        } else {
            return true;
        }
    }
    public Long getAdvertiserId() {
        if (isAgencyContext()) {
            return subContext.getAccountId();
        } else {
            return getAccountId();
        }
    }

    public AdvertiserAccount getAdvertiser() {
        return findAccount(AdvertiserAccount.class, getAdvertiserId());
    }

    public AdvertiserAccount getAgencyAdvertiser() {
        checkSubContext();
        return subContext.getAccount();
    }

    public BigDecimal getCreditBalance() {
        AdvertisingFinanceService advertisingFinanceService =
                ServiceLocator.getInstance().lookup(AdvertisingFinanceService.class);
        return advertisingFinanceService.getCreditBalance(getAccountId());
    }

    public boolean isFinancialFieldsPresent() {
        AdvertisingAccountBase account = getAccount();
        return account != null && account.isFinancialFieldsPresent();
    }

    public boolean isAgencyAdvertiserSet() {
        return subContext != null && subContext.isSet();
    }

    private void switchInternal(AdvertisingAccountBase accountBase) {
        if (accountBase instanceof AgencyAccount) {
            // switch to agency context without agency advertiser
            subContext = new AgencyAdvertiserContext();
            super.switchTo(accountBase);
        } else if (accountBase instanceof AdvertiserAccount) {
            AdvertiserAccount advertiserAccount = (AdvertiserAccount) accountBase;
            if (advertiserAccount.getAgency() == null) {
                // switch non-agency advertiser context
                subContext = null;
                super.switchTo(accountBase);
            } else {
                // switch to agency context with agency advertiser
                if (subContext == null) {
                    subContext = new AgencyAdvertiserContext();
                }
                subContext.switchTo(advertiserAccount);
                if (!super.switchTo(advertiserAccount.getAgency())) {
                    getListener().onSwitchTo(this);
                }
            }
        }
    }

    private void checkSubContext() {
        if (subContext == null) {
            throw new RuntimeException("No agency advertiser context for non-agency advertiser");
        }
    }

    protected boolean isSwitchedTo(Long accountId) {
        if (super.isSwitchedTo(accountId)) {
            return true;
        }

        if (subContext != null && subContext.isSwitchedTo(accountId)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        AdvertiserContext that = (AdvertiserContext) o;

        if (subContext != null ? !subContext.equals(that.subContext) : that.subContext != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subContext != null ? subContext.hashCode() : 0);
        return result;
    }

    private static class AgencyAdvertiserContext extends ContextBase<AdvertiserAccount> {

        @Override
        protected String getNoContextMessageKey() {
            return "error.context.notset.agencyAdvertiser";
        }

        @Override
        protected void checkCanBeSwitched(Long accountId) {
            // no restrictions there
        }
    }
}
