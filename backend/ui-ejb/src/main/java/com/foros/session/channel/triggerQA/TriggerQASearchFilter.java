package com.foros.session.channel.triggerQA;

import com.foros.model.campaign.CCGType;
import com.foros.session.ServiceLocator;
import com.foros.session.campaign.CampaignCreativeGroupService;

public enum TriggerQASearchFilter {
    ALL,
    CHANNEL,
    CCG;

    public String createFromClause(Long ccgId) {
        StringBuilder query = new StringBuilder();
        switch (this) {
            case ALL:
                query.append(" FROM triggers t")
                        .append(" LEFT JOIN channeltrigger ct on ct.trigger_id = t.trigger_id")
                        .append(" LEFT JOIN channel c on c.channel_id = ct.channel_id")
                        .append(" LEFT JOIN account a on c.account_id = a.account_id");
                break;
            case CHANNEL:
                query.append(" FROM channel c ")
                        .append(" JOIN account a on c.account_id = a.account_id")
                        .append(" JOIN channeltrigger ct on c.channel_id = ct.channel_id")
                        .append(" JOIN triggers t on ct.trigger_id = t.trigger_id");
                break;
            case CCG:
                CCGType ccgType = ccgId != null ? fetchCCGType(ccgId) : CCGType.DISPLAY;
                appendCcgFromClause(query, ccgType);
                query.append(" JOIN campaign cmp on ccg.campaign_id = cmp.campaign_id")
                        .append(" JOIN account a on cmp.account_id = a.account_id")
                        .append(" JOIN channeltrigger ct on c.channel_id = ct.channel_id")
                        .append(" JOIN triggers t on ct.trigger_id = t.trigger_id");
                break;
            default:
                throw new IllegalArgumentException("Unknown TriggerQASearchFilter");
        }
        return query.toString();
    }

    private void appendCcgFromClause(StringBuilder query, CCGType ccgType) {
        switch (ccgType) {
            case DISPLAY:
                query.append(" FROM campaigncreativegroup ccg")
                        .append(" LEFT JOIN ccgkeyword kwd on ccg.ccg_id = kwd.ccg_id")
                        .append(" LEFT JOIN channel c on (ccg.channel_id = c.channel_id or kwd.channel_id = c.channel_id)");
                break;
            case TEXT:
                query.append(" FROM campaigncreativegroup ccg")
                        .append(" JOIN ccgkeyword kwd on ccg.ccg_id = kwd.ccg_id")
                        .append(" JOIN channel c on kwd.channel_id = c.channel_id");
                break;
            default:
                throw new IllegalArgumentException("Unknown CCGType");
        }
    }

    private CCGType fetchCCGType(Long ccgId) {
        CampaignCreativeGroupService ccgService = ServiceLocator.getInstance().lookup(CampaignCreativeGroupService.class);
        return ccgService.find(ccgId).getCcgType();
    }

    public String createAccountWhereClause(Long accountId) {
        if (accountId == null) {
            return "";
        }
        switch (this) {
            case ALL:
            case CHANNEL:
                return " AND a.account_id = :accountId";
            case CCG:
                return accountId < 0 ? " AND a.agency_account_id is null" : "AND a.agency_account_id = :accountId"; // handle none option
            default:
                throw new IllegalArgumentException("Unknown TriggerQASearchFilter");
        }
    }
}
