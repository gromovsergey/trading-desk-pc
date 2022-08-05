package resource;

import group.Resource;
import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.Format;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category({ Unit.class, Resource.class })
public class ParametersMatchTest extends Assert {
    private static final Logger logger = Logger.getLogger(ParametersMatchTest.class.getName());
    private static final String[] LANG_SUFFIXES = {"ru"};

    private static final String[] exclusions = new String[] {
        "LDAP", "URL", "ISP", "ID", "CCID", "RON", "CCID", "RON", "CPA", "CPM","CPC",
        "eCPA", "eCPM", "CTR", "CSV", "Excel", "Webwise Discover", "Webwise", "WD",
        "Swift", "BACS", "XSLT", "kB", "Mb", "BIRT", "Rptdesign", "pdf",
        "FOROS", "Open Internet Exchange", "ARPM", "ARPU", "HTML", "RSS", "Atom",
        "IBAN", "MIME", "GMT", "CMP", "PWP", "HTTP", "HTTPS"
    };

    /**
     * Test that the parameters in translated resources are the same as in English ones
     *
     * @throws Exception
     */
    @Test
    public void testParametersMatch() throws Exception {
        int errorCount = 0;

        String defaultContent = resource.ResourceUtil.readFileContent("target/classes/resource/applicationResource.properties");
        Map<String, String> defaultProps = getPropsMap(defaultContent, "default");

        for (Map.Entry<String, String> entry : defaultProps.entrySet()) {
            String defaultValue = entry.getValue();
            if (defaultValue.matches(".*\\$\\{number\\(.*\\)\\}.*")) {
                logger.severe("Wrong decimal parameter for en:" + entry.getKey());
                errorCount++;
            }
        }

        for (String langSuffix : LANG_SUFFIXES) {
            String content = resource.ResourceUtil.readFileContent("target/classes/resource/applicationResource_" + langSuffix + ".properties");
            Map<String, String> props = getPropsMap(content, langSuffix);

            for (Map.Entry<String, String> entry : defaultProps.entrySet()) {
                String defaultValue = entry.getValue();
                String value = props.get(entry.getKey());

                if (value != null) {

                    // Check {X, number, #} params
                    final Pattern numberFormattedParamPattern = Pattern.compile("^.*\\{[\\d\\s,]+number[\\s,]+([#.]+).*$");
                    final Matcher numberFormattedParamMatcher = numberFormattedParamPattern.matcher(defaultValue);
                    if (numberFormattedParamMatcher.matches()) {
                        Matcher langMatcher = numberFormattedParamPattern.matcher(value);
                        boolean equals = langMatcher.matches() && numberFormattedParamMatcher.groupCount() == langMatcher.groupCount();
                        for (int i = 1; equals && i <= langMatcher.groupCount(); i++) {
                            equals = numberFormattedParamMatcher.group(i).equals(langMatcher.group(i));
                        }
                        if (!equals) {
                            logger.severe("Wrong parameters for " + langSuffix + ": " + entry.getKey() + "; {X, number, #} formatting is absent or differs from default (en)");
                            errorCount++;
                        }
                    }

                    // Check ##...## params
                    Pattern pattern = Pattern.compile("(?<=^|[^#])##\\w+##(?=[^#]|$)");
                    Matcher matcher = pattern.matcher(defaultValue);
                    while (matcher.find()) {
                        String param = matcher.group(0);
                        if (!value.contains(param)) {
                            logger.severe("Wrong parameters for " + langSuffix + ":" + entry.getKey() + "; parameter " + param + " is absent");
                            errorCount++;
                        }
                    }

                    if (defaultValue.matches(".*\\$\\{.+\\}.*")) {
                        Set<String> args = new HashSet<String>();
                        int start = 0, end = 0;

                        do {
                            start = defaultValue.indexOf("${", end);
                            end = defaultValue.indexOf("}", start);
                            if (start > 0 && end > 0) {
                                args.add(defaultValue.substring(start, end + 1));
                            }
                        } while (start > 0);

                        for (String arg : args) {
                            if (!value.contains(arg)) {
                                logger.severe("Wrong parameters for " + langSuffix + ":" + entry.getKey() + "; parameter " + arg + " is absent");
                                errorCount++;
                            } else {
                                value = value.replaceAll(Pattern.quote(arg), "");
                            }
                        }
                    }

                    defaultValue = defaultValue.replaceAll("\\$\\{.+\\}", "");
                    if (value.matches(".*\\$\\{.+\\}.*")) {
                        value = value.replaceAll("\\$\\{.+\\}", "");
                        logger.severe("Wrong parameters for " + langSuffix + ":" + entry.getKey() + "; excess parameter");
                        errorCount++;
                    }

                    try {
                        Format[] defaultFormats = new MessageFormat(defaultValue).getFormats();
                        if (defaultFormats.length > 0) {
                            Format[] formats = new MessageFormat(value).getFormats();
                            Format[] formatsByIndex = new MessageFormat(value).getFormatsByArgumentIndex();
                            Format[] defaultFormatsByIndex = new MessageFormat(defaultValue).getFormatsByArgumentIndex();

                            if (defaultFormats.length != formats.length || defaultFormatsByIndex.length != formatsByIndex.length) {
                                logger.severe("Wrong parameters for " + langSuffix + ": " + entry.getKey());
                                errorCount++;
                            }
                        }
                    } catch (IllegalArgumentException ex) {
                        logger.severe("Wrong resource record " + langSuffix + ": " + entry.getKey());
                        errorCount++;
                    }

                    pattern = Pattern.compile(":[a-zA-Z]\\w*\\b");
                    matcher = pattern.matcher(defaultValue);
                    while (matcher.find()) {
                        String param = matcher.group(0);
                        if (!value.contains(param)) {
                            logger.severe("Wrong parameters for " + langSuffix + ":" + entry.getKey() + "; parameter " + param + " is absent");
                            errorCount++;
                        }
                    }

                    // Check exclusions
                    if (!langSuffix.equals("ru")) {
                        value = value.toLowerCase();
                        String defaultValue1 = defaultValue.toLowerCase();

                        for (String exclusion : exclusions) {
                            if (defaultValue1.matches(".*\\b" + exclusion.toLowerCase() + "\\b.*")) {
                                if ((langSuffix == "pt" && !value.matches(".*\\b" + exclusion.toLowerCase() + "\\b.*"))
                                        || (langSuffix != "pt" && !value.contains(exclusion.toLowerCase()))) {
                                    logger.severe("Wrong translation " + langSuffix + ": " + entry.getKey() + "; ");
                                    logger.severe("\"" + exclusion + "\" is an exclusion");
                                    errorCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

        assertEquals(0, errorCount);
    }

    private Map<String, String> getPropsMap(String content, String suffix) {
        String[] rows = content.split("\n");
        Map<String, String> props = new HashMap<String, String>();

        for (String row : rows) {
            String key = getKey(row, suffix);
            if (key == null) {
                continue;
            }

            assertFalse("Remove duplicates for " + suffix + ": " + key, props.containsKey(key));

            String value = row.substring(key.length() + 1).trim();
            if (!value.isEmpty()) {
                props.put(key, value);
            }
        }

        return props;
    }

    private String getKey(String row, String suffix) {
        if (row.trim().length() == 0 || row.startsWith("#") || row.startsWith("!")) {
            return null;
        }

        int pos = row.indexOf("=");

        assertTrue("Wrong resource record " + suffix + ": " + row, pos > 0);

        return row.substring(0, pos);
    }
}
