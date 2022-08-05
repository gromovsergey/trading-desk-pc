package com.foros.session.campaign;

import com.foros.model.account.Account;
import com.foros.model.campaign.Campaign;

import java.math.BigDecimal;

public class CampaignUtil {
    public static boolean canChangeBudget(Account account, Campaign existing) {
        if (existing == null) {
            return canCreateBudget(account);
        } else {
            return canUpdateBudget(existing);
        }
    }

    public static boolean canCreateBudget(Account account) {
        return !account.getAccountType().getIoManagement();
    }

    public static boolean canUpdateBudget(Campaign campaign) {
        BigDecimal budget = campaign.getBudget();
        return canUpdateBudget(campaign.getAccount(), budget);
    }

    public static boolean canUpdateBudget(Account account, BigDecimal budget) {
        Boolean ioManagement = account.getAccountType().getIoManagement();
        if (!ioManagement) {
            return true;
        }

        if (budget == null || BigDecimal.ZERO.compareTo(budget) != 0) {
            return true;
        }

        return false;
    }
}
