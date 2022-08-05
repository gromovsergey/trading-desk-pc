package com.foros.session.db;

import com.foros.util.ExceptionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

public enum DBConstraint {
    DEFAULT(null, "name"),

    CAMPAIGN_NAME("campaign_name_uidx", "name", "campaign.errors.nameAlreadyExists"),
    CCG_NAME("campaigncreativegroup_name_uidx", "name", "ccg.errors.nameAlreadyExists"),
    CCGKEYWORD_TRIGGER("ccgkeyword_ccg_type_kw_uk", "originalKeyword", "ccgKeyword.errors.alreadyExists"),
    ACTION_NAME("action_name_uk", "name"),
    CURRENCY_CODE("currency_currency_code_uk", "currencyCode", "ccg.errors.nameAlreadyExists"),
    CHANNEL_NAME("channel_name_adl_uidx", "name", "channel.errors.nameAlreadyExists"),
    DISCOVER_CHANNEL_NAME("channel_name_v_uidx", "name", "channel.errors.nameAlreadyExists"),
    COUNTRY_CODE("country_pkey", "countryCode", "Country.errors.alreadyExists"),
    SIZETYPE_NAME("sizetype_name_uk", "defaultName"),
    BIRTREPORT_NAME("birtreport_name_uk", "name"),
    ACCOUNTTYPE_NAME("accounttype_name_uk", "name"),
    WDREQUESTMAPPING_NAME("wdrequestmapping_name_uk", "name"),
    ACCOUNT_NAME("account_agency_name_uidx", "name"),
    USERROLE_NAME("userrole_name_uk", "name"),
    CREATIVESIZE_NAME("creativesize_name_uk", "defaultName"),
    CREATIVESIZE_PROTOCOL_NAME("creativesize_protocol_name_uk", "protocolName"),
    TEMPLATE_NAME("template_name_uk", "name"),
    APPFORMAT_NAME("appformat_name_uk", "name"),
    OPTIONGROUP_NAME("optiongroup_name_uidx", "defaultName"),
    OPTIONS_NAME("options_name_uidx", "defaultName"),
    BEHAVIORALPARAMETERSLIST_NAME("behavioralparameterslist_name_uk", "name"),
    SITE_NAME("site_name_uk", "name"),
    PLACEMENT_BLACKLIST_SIZE_URL("placementblacklist_channel_id_url_idx", null, "admin.placementsBlacklist.bulkUpload.version");

    private static final Logger logger = Logger.getLogger(DBConstraint.class.getName());
    private static final Map<String, DBConstraint> byDatabaseName = initByConstraint();

    private static Map<String, DBConstraint> initByConstraint() {
        Map<String, DBConstraint> res = new HashMap<>();
        for (DBConstraint constraint : values()) {
            if (constraint.getConstraintName() != null) {
                res.put(constraint.getConstraintName(), constraint);
            }
        }
        return res;
    }

    private final String constraintName;
    private final String field;
    private final String resourceKey;

    DBConstraint(String constraintName, String field, String resourceKey) {
        this.constraintName = constraintName;
        this.field = field;
        this.resourceKey = resourceKey;
    }

    DBConstraint(String databaseName, String field) {
        this(databaseName, field, "errors.duplicate.name");
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getField() {
        return field;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public boolean match(Exception e) {
        return this == fromException(e);
    }

    public static DBConstraint fromException(Exception e) {
        PSQLException psqlException = ExceptionUtil.getCause(e, PSQLException.class);
        if (psqlException == null) {
            return null;
        }

        String cName = PGExceptionInspector.getConstraint(psqlException);
        if (cName == null) {
            return null;
        }

        DBConstraint res = byDatabaseName.get(cName);
        if (res == null) {
            logger.warning("Unknown constraint: " + cName + ", need to create new DBConstraint enum value for it");
            res = DEFAULT;
        }

        return res;
    }
}
