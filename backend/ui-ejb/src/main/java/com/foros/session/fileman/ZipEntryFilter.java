package com.foros.session.fileman;

import java.util.zip.ZipEntry;

public interface ZipEntryFilter {
    boolean check(ZipEntry entry);
}
