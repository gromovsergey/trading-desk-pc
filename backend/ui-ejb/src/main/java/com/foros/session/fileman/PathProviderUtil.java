package com.foros.session.fileman;

public class PathProviderUtil {
    public static PathProvider getNested(PathProvider parent, String dir) {
        return parent.getNested(dir);
    }

    public static PathProvider getNested(PathProvider parent, String dir, OnNoProviderRoot mode) {
        return parent.getNested(dir, mode);
    }
}
