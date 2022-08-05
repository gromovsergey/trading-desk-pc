package com.foros.model.template;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.foros.model.template.CreativeToken.*;

public enum SubstitutionCategory {
    GENERIC(ADIMAGE_SERVER, ADSERVER, APP_FORMAT, RANDOM, TAGHEIGHT, TAGSIZE, TAGWIDTH, TESTREQUEST),

    ADVERTISER(ADIMAGE_PATH, ADVID, CCID, CGID, CID, CLICK, HEIGHT, KEYWORD, PRECLICK, SIZE, TEMPLATE, WIDTH, IDFA,
            ADVERTISING_ID, EXT_TRACK_PARAMS, TNS_COUNTER_DEVICE_TYPE, APPLICATION_ID, CLICK0, PRECLICK0),

    PUBLISHER(PASSBACK_CODE, PASSBACK_PIXEL, PASSBACK_TYPE, PASSBACK_URL, PUBID, SITEID, TAGID, DOMAIN, BR_ID, BS_ID, BP_ID),

    INTERNAL(COHORT, COLOID, ORIGLINK, PUBPIXELS, PUBPIXELSOPTIN, PUBPIXELSOPTOUT, REFERER, SECTOK,
            TRACKPIXEL, UID, USERBIND, USERSTATUS, UNSIGNEDUID, UNSIGNEDCOOKIEUID, EXTERNALID);

    private List<CreativeToken> tokens;

    SubstitutionCategory(CreativeToken... tokens) {
        this.tokens = Collections.unmodifiableList(Arrays.asList(tokens));
    }

    public List<CreativeToken> getTokens() {
        return tokens;
    }
}
