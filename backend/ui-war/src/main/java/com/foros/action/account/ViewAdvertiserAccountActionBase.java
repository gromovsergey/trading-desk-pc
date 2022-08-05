package com.foros.action.account;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.finance.Invoice;
import com.foros.session.campaignCredit.CampaignCreditService;
import com.foros.session.campaignCredit.CampaignCreditTO;
import com.foros.session.finance.AdvertisingFinanceService;

import java.math.BigDecimal;
import javax.ejb.EJB;
import java.util.List;

public class ViewAdvertiserAccountActionBase extends ViewAccountActionBase<AdvertisingAccountBase> {
    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private CampaignCreditService campaignCreditService;

    private List<Invoice> invoices;
    private List<CampaignCreditTO> campaignCredits;
    private BigDecimal creditBalance;

    public boolean isAgencyFlag() {
        return account instanceof AgencyAccount;
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser();
    }

    @Deprecated
    /**
     * @deprecated OUI-28825
     */
    public List<Invoice> getInvoices() {
        if (invoices != null) {
            return invoices;
        }

        invoices = advertisingFinanceService.findInvoicesByAccount(account.getId());

        return invoices;
    }

    public List<CampaignCreditTO> getCampaignCredits() {
        if (campaignCredits != null) {
            return campaignCredits;
        }

        campaignCredits = campaignCreditService.findCampaignCredits(account.getId());

        return campaignCredits;
    }

    public BigDecimal getCreditBalance() {
        if (creditBalance == null) {
            creditBalance = advertisingFinanceService.getCreditBalance(account.getId());
        }
        return creditBalance;
    }

    public boolean isCommissionPresent() {
        if (!account.isFinancialFieldsPresent() || account.getFinancialSettings().getCommission() == null) {
            return false;
        }

        return account instanceof AgencyAccount || ((AdvertiserAccount) account).isInAgencyAdvertiser();
    }

    public boolean isBudgetLimitPresent() {
        return account.isFinancialFieldsPresent() &&
                account.getFinancialSettings().getData().getPrepaidAmount() != null;
    }
}
