package com.foros.session.fileman;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.session.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class RegexpZipEntryFilter implements ZipEntryFilter {
    public static final Logger logger = Logger.getLogger(RegexpZipEntryFilter.class.getName());

    private List<Pattern> excludePatterns;

    @Override
    public boolean check(ZipEntry entry) {
        initPatterns();
        for (Pattern pattern : excludePatterns) {
            if (pattern.matcher(entry.getName()).matches()) {
                return false;
            }
        }
        return true;
    }

    private void initPatterns() {
        if (excludePatterns != null) {
            return;
        }

        ConfigService config = ServiceLocator.getInstance().lookup(ConfigService.class);
        List<String> strings = config.get(ConfigParameters.UPLOAD_ZIP_FILE_FILTERS);
        List<Pattern> patterns = new ArrayList<>(strings.size());
        for (String s : strings) {
            Pattern pattern = null;
            try {
                pattern = Pattern.compile(s);
            } catch (Exception e) {
                // skip bad regexps
                logger.log(Level.WARNING, "Failed to compile regex: " + s, e);
            }
            patterns.add(pattern);
        }

        excludePatterns = patterns;
    }
}
