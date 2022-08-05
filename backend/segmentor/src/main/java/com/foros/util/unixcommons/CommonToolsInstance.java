package com.foros.util.unixcommons;

import java.util.logging.Level;
import java.util.logging.Logger;

class CommonToolsInstance {

    private static final Logger logger = Logger.getLogger(CommonToolsInstance.class.getName());

    private static CommonsTools commonsTools;

    static CommonsTools get() {
        if (commonsTools == null) {
            initInternal();
        }
        return commonsTools;
    }

    private static void initInternal() {
        String normalizerLib = System.getProperty("foros.httpfunctions.lib");

        if (normalizerLib != null && normalizerLib.length() > 0) {
            initUnixCommons();
        } else {
            commonsTools = new NullCommonsTools();
            logger.log(Level.INFO, "NullNormalizer is successfully loaded");
        }
    }

    private static void initUnixCommons() {
        commonsTools = new UnixCommonsTools();
        logger.log(Level.INFO, "UnixCommonsNormalizer is successfully loaded");
    }
}
