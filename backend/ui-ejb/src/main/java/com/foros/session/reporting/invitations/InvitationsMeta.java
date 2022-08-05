package com.foros.session.reporting.invitations;

import static com.foros.reporting.meta.MetaDataBuilder.buildColumn;
import static com.foros.reporting.meta.MetaDataBuilder.metaData;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.meta.ResolvableMetaData;


public class InvitationsMeta {

    public static final DbColumn DATE = buildColumn("date", "sdate", ColumnTypes.date());
    public static final DbColumn INVITATIONS_SHOWN = buildColumn("invitationsShown", "total_invitations_shown", ColumnTypes.number());
    public static final DbColumn NO_DECISION = buildColumn("noDecision", "total_no_decision", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_IN = buildColumn("buttonOptIn", "total_button_in", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_IN = buildColumn("popupOptIn", "total_popup_in", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_OUT = buildColumn("buttonOptOut", "total_button_out", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_OUT = buildColumn("popupOptOut", "total_popup_out", ColumnTypes.number());

    public static final ResolvableMetaData<DbColumn> TOTAL_META_DATA = metaData("invitationsReport")
        .metricsColumns(DATE, INVITATIONS_SHOWN, NO_DECISION, BUTTON_OPT_IN, POPUP_OPT_IN, BUTTON_OPT_OUT, POPUP_OPT_OUT).build();


    public static final DbColumn INVITATIONS_SHOWN_TOTAL = buildColumn("invitationsShownTotal", "total_invitations_shown", ColumnTypes.number());
    public static final DbColumn INVITATIONS_SHOWN_CHROME = buildColumn("invitationsShownChrome", "chrome_invitations_shown", ColumnTypes.number());
    public static final DbColumn INVITATIONS_SHOWN_FIREFOX = buildColumn("invitationsShownFirefox", "firefox_invitations_shown", ColumnTypes.number());
    public static final DbColumn INVITATIONS_SHOWN_MSIE = buildColumn("invitationsShownMsie", "msie_invitations_shown", ColumnTypes.number());
    public static final DbColumn NO_DECISION_TOTAL = buildColumn("noDecisionTotal", "total_no_decision", ColumnTypes.number());
    public static final DbColumn NO_DECISION_CHROME = buildColumn("noDecisionChrome", "chrome_no_decision", ColumnTypes.number());
    public static final DbColumn NO_DECISION_FIREFOX = buildColumn("noDecisionFirefox", "firefox_no_decision", ColumnTypes.number());
    public static final DbColumn NO_DECISION_MSIE = buildColumn("noDecisionMsie", "msie_no_decision", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_IN_TOTAL = buildColumn("buttonOptInTotal", "total_button_in", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_IN_CHROME = buildColumn("buttonOptInChrome", "chrome_button_in", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_IN_FIREFOX = buildColumn("buttonOptInFirefox", "firefox_button_in", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_IN_MSIE = buildColumn("buttonOptInMsie", "msie_button_in", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_IN_TOTAL = buildColumn("popupOptInTotal", "total_popup_in", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_IN_CHROME = buildColumn("popupOptInChrome", "chrome_popup_in", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_IN_FIREFOX = buildColumn("popupOptInFirefox", "firefox_popup_in", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_IN_MSIE = buildColumn("popupOptInMsie", "msie_popup_in", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_OUT_TOTAL = buildColumn("buttonOptOutTotal", "total_button_out", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_OUT_CHROME = buildColumn("buttonOptOutChrome", "chrome_button_out", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_OUT_FIREFOX = buildColumn("buttonOptOutFirefox", "firefox_button_out", ColumnTypes.number());
    public static final DbColumn BUTTON_OPT_OUT_MSIE = buildColumn("buttonOptOutMsie", "msie_button_out", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_OUT_TOTAL = buildColumn("popupOptOutTotal", "total_popup_out", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_OUT_CHROME = buildColumn("popupOptOutChrome", "chrome_popup_out", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_OUT_FIREFOX = buildColumn("popupOptOutFirefox", "firefox_popup_out", ColumnTypes.number());
    public static final DbColumn POPUP_OPT_OUT_MSIE = buildColumn("popupOptOutMsie", "msie_popup_out", ColumnTypes.number());

    public static final ResolvableMetaData<DbColumn> FAMILIES_META_DATA = metaData("invitationsReport")
        .metricsColumns(DATE,
            INVITATIONS_SHOWN_CHROME, INVITATIONS_SHOWN_FIREFOX, INVITATIONS_SHOWN_MSIE, INVITATIONS_SHOWN_TOTAL,
            NO_DECISION_CHROME, NO_DECISION_FIREFOX, NO_DECISION_MSIE, NO_DECISION_TOTAL,
            BUTTON_OPT_IN_CHROME, BUTTON_OPT_IN_FIREFOX, BUTTON_OPT_IN_MSIE, BUTTON_OPT_IN_TOTAL,
            POPUP_OPT_IN_CHROME, POPUP_OPT_IN_FIREFOX, POPUP_OPT_IN_MSIE, POPUP_OPT_IN_TOTAL,
            BUTTON_OPT_OUT_CHROME, BUTTON_OPT_OUT_FIREFOX, BUTTON_OPT_OUT_MSIE, BUTTON_OPT_OUT_TOTAL,
            POPUP_OPT_OUT_CHROME, POPUP_OPT_OUT_FIREFOX, POPUP_OPT_OUT_MSIE, POPUP_OPT_OUT_TOTAL).build();

    private InvitationsMeta() {
    }
}
