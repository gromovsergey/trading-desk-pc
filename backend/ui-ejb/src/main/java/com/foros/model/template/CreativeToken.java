package com.foros.model.template;

public enum CreativeToken {
    // Build-in Creative Tokens as per https://confluence.ocslab.com/display/TDOC/Creative+Tokens+and+Templates
    WDTAGID(),
    TAG_ID(),
    TAGWIDTH(),
    TAGHEIGHT(),
    TAGSIZE(),
    ADSERVER_URL(),
    AD_FOOTER_URL(),
    ADIMAGE_SERVER("ADIMAGE-SERVER"),
    ADIMAGE_PATH("ADIMAGE-PATH"),
    APP_FORMAT(),
    TEMPLATE(),
    DISCOVER_DOMAIN(),
    WIDTH(),
    HEIGHT(),
    SIZE(),
    CRHTML(),
    IMAGE_FILE(),
    KEYWORD(),
    RANDOM(),
    CONVERSION_ID(),
    CONVERSION_DOMAIN(),
    PASSBACK_CODE(),
    PASSBACK_PIXEL(),
    PASSBACK_TYPE(),
    PASSBACK_URL(),
    CLICK(),
    CLICK0(),
    CLICKF(),
    FOROSCLICK(),
    CRCLICK(),
    PRECLICK(),
    PRECLICK0(),
    PRECLICKF(),
    FOROSPRECLICK(),
    ADSERVER(),
    USERBIND(),
    UID(),
    CLIENT_IP("CLIENT-IP"),
    ORIGLINK(),
    PUBPIXELS(),
    PUBPIXELSOPTIN(),
    PUBPIXELSOPTOUT(),
    USERSTATUS(),
    SECTOK(),
    COLOID(),
    COHORT(),
    TESTREQUEST(),
    TRACKPIXEL(),
    REFERER(),
    ADVID(),
    CID(),
    CGID(),
    CCID(),
    PUBID(),
    SITEID(),
    TAGID(),
    IDFA(),
    ADVERTISING_ID(),
    DOMAIN(),
    UNSIGNEDUID(),
    UNSIGNEDCOOKIEUID(),
    EXT_TRACK_PARAMS(),
    TNS_COUNTER_DEVICE_TYPE(),
    APPLICATION_ID(),
    BR_ID(),
    BS_ID(),
    BP_ID(),
    EXTERNALID(),


    // other
    DISPLAY_URL(),
    DESCRIPTION1(),
    DESCRIPTION2(),
    DESCRIPTION3(),
    DESCRIPTION4(),
    HEADLINE(),

    GLUE_DESCRIPTIONS(),

    PREVIEWPARAMS(),
    INVENTORYMODEPARAMS(),
    CREATIVE_HASH();


    private String overriddenName;

    CreativeToken() { }

    CreativeToken(String overriddenName) {
        this.overriddenName = overriddenName;
    }

    public String getName() {
        return overriddenName == null ? name() : overriddenName;
    }
}
