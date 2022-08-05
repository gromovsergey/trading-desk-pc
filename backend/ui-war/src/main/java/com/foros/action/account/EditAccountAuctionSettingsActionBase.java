package com.foros.action.account;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.CustomParametersBreadcrumbsElement;

public class EditAccountAuctionSettingsActionBase extends AccountAuctionSettingsActionBase {
    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(getInternalAccount()))
                .add(new EditAccountAuctionSettingsBreadcrumbsElement(getId()))
                .add(ActionBreadcrumbs.EDIT);
    }

    private static class EditAccountAuctionSettingsBreadcrumbsElement extends CustomParametersBreadcrumbsElement {
        private EditAccountAuctionSettingsBreadcrumbsElement(Long id) {
            super("AuctionSettings.breadcrumbs", "internal/account/viewAuctionSettings");
            putParameter("id", id.toString());
        }
    }
}

