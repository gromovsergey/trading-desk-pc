package com.foros.model;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.session.ServiceLocator;

public class VersionHelper {
    public static final String VERSION_PROPERTY = "oui.build.version";
    public static final String TIMESTAMP_PROPERTY = "oui.build.timestamp";

    /** Returns UI & PGDB versions */
    public static String getVersion() {
        ConfigService configService = ServiceLocator.getInstance().lookup(ConfigService.class);
        String uiVersion = configService.get(ConfigParameters.UI_VERSION);
        String pgdbVersion = configService.get(ConfigParameters.PGDB_VERSION);

        // do not localize, they are products names
        return "UI " + uiVersion + ", PGDB " + pgdbVersion;
    }

    /** Returns UI timestamp */
    public static String getBuildTimestamp() {
        String uiTimestamp = ServiceLocator.getInstance().lookup(ConfigService.class).get(ConfigParameters.UI_TIMESTAMP);
        return uiTimestamp;
    }
}
