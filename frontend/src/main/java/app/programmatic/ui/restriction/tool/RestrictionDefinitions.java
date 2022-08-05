package app.programmatic.ui.restriction.tool;

import app.programmatic.ui.restriction.model.Restriction;

import java.util.HashMap;
import java.util.Map;

import static app.programmatic.ui.restriction.model.Restriction.*;

public class RestrictionDefinitions {
    private static final Map<String, Restriction> definitions = initDefinitions();

    public static Restriction find(String name) {
        return definitions.get(name);
    }

    private static final Map<String, Restriction> initDefinitions() {
        HashMap<String, Restriction> result = new HashMap<>();

        result.put("flight.create", CREATE_CAMPAIGN);
        result.put("flight.edit", UPDATE_CAMPAIGN); // flight_id -> campiagn_id
        //result.put("flight.status", UPDATE_CAMPAIGN);  // Now in LocalRestrictionService // flight_id -> campiagn_id

        result.put("lineItem.create", CREATE_CCG); // flight_id -> campiagn_id
        result.put("lineItem.edit", UPDATE_CCG); // li_id -> ccg_id
        result.put("lineItem.status", UPDATE_CCG); // li_id -> ccg_id

        result.put("advertiserEntity.view", VIEW_ADVERTISER_ENTITY);
        result.put("advertisingAccount.view", VIEW_ADVERTISING_ACCOUNT);
        result.put("agencyAdvertiserAccount.view", VIEW_AGENCY_ADVERTISER_ACCOUNT);

        result.put("advertiser.create", CREATE_ADVERTISER_IN_AGENCY);
        result.put("advertiser.edit", UPDATE_ADVERTISER_IN_AGENCY);
        result.put("advertiser.status", UPDATE_ADVERTISER_IN_AGENCY);

        //result.put("account.create", null); // Ability disabled
        //result.put("account.edit", null); // Ability disabled
        result.put("account.status", UPDATE_ACCOUNT);

        result.put("user.create", CREATE_USER);
        result.put("user.edit", UPDATE_USER);
        result.put("user.status", UPDATE_USER);

        result.put("channel.create", CREATE_ADVERTISING_CHANNEL);
        result.put("channel.edit", UPDATE_ADVERTISING_CHANNEL);
        result.put("channel.status", UPDATE_ADVERTISING_CHANNEL);

        result.put("conversion.create", CREATE_CONVERSION);
        result.put("conversion.edit", UPDATE_CONVERSION);
        result.put("conversion.status", UPDATE_CONVERSION);

        result.put("creative.create", CREATE_CREATIVE);
        result.put("creative.edit", UPDATE_CREATIVE);
        result.put("creative.status", UPDATE_CREATIVE);

        result.put("agentReport.view", VIEW_AGENT_REPORT);
        result.put("agentReport.edit", EDIT_AGENT_REPORT);

        result.put("publisherReport.run", RUN_PUBLISHER_REPORT0);
        result.put("referrerReport.run", RUN_REFERRER_REPORT0);

        return result;
    }
}
