package com.foros.util.templates.bundle;

import java.util.List;
import java.util.ArrayList;

/**
 * Author: Boris Vanin
 */
public class MultiBundle implements Bundle {

    List<Bundle> bundles = new ArrayList<Bundle>();

    public MultiBundle(Bundle...bundles) {
        for (Bundle bundle : bundles) {
            addBundle(bundle);
        }
    }

    public void addBundle(Bundle bundle) {
        if (bundle != null) {
            bundles.add(bundle);
        }
    }

    public String get(String key) {
        for (Bundle bundle : bundles) {
            String result = bundle.get(key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

}
