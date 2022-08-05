package com.foros.util.templates.bundle;

import java.util.Map;

/**
 * Author: Boris Vanin
 */
public final class Bundles {

    private Bundles() {}

    public static Bundle createBundle(Map<String, String> bundle) {
        return bundle != null ? new MapBundle(bundle) : null;
    }

    public static MultiBundle createMultiBundle(Bundle... bundles) {
        return new MultiBundle(bundles);
    }

}
