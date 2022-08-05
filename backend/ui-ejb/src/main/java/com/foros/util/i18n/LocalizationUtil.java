package com.foros.util.i18n;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;

import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.LocalizedResourceHelper;

public final class LocalizationUtil {
    private LocalizationUtil() {
    }

    public static Resource getResource(ResourceLoader loader, String resource, Locale locale) {
        String extension = FilenameUtils.getExtension(resource);
        extension = extension.isEmpty() ? "" : "." + extension;
        String baseName = FilenameUtils.removeExtension(resource);

        return new LocalizedResourceHelper(loader).findLocalizedResource(baseName, extension, locale);
    }

    public static Locale getCurrentLocale() {
        return CurrentUserSettingsHolder.getLocale();
    }

}
