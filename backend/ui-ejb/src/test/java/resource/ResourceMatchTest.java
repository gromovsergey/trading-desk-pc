package resource;

import com.foros.model.security.Language;
import com.foros.session.channel.service.ChannelUtils;
import com.foros.util.PropertyHelper;
import group.Resource;
import group.Unit;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Category({ Unit.class, Resource.class })
public class ResourceMatchTest extends Assert {

    private String[] knownHtmlSnippets = new String[] {
        "nbsp;",
        "<br>"
    };

    // Resources there are no translation needed
    private List<String> denyTranslation = Arrays.asList(
        "user.language.RO",
        "user.language.EN",
        "user.language.JA",
        "user.language.KO",
        "user.language.PT",
        "user.language.RU",
        "user.language.ZH",
        "user.language.TR",
        "form.copyright"
    );

    // Resources which have more then one appearance of '='
    private List<String> stickedResources = Arrays.asList(
        "errors.invalid.country.price.format",
        "errors.prefix",
        "errors.tagPricing.format",
        "evicted.class.id",
        "evicted.collection.id",
        "report.comment.isp",
        "report.comment.referrer",
        "chart.getFlashPlayer",
        "InternalUser.tipkey.maxCreditLimit",
        "DiscoverChannel.errors.invalidCsv",
        "admin.placementsBlacklist.bulkUpload.columnsDescription",
        "creative.upload.error.duplicateCreativeOption"
    );

    public int errorCount = 0;

    // Test that for each Iso Language code has is a English name
    @Test
    public void testIsoLanguagesResource() throws Exception {
        String[] rows = getProperties(Language.EN);

        Set<String> allKeys = new HashSet<String>();
        Set<String> languageKeys = new HashSet<String>();

        final String PREFIX = "global.language.";
        final String SUFFIX = ".name";
        for (String lang : ChannelUtils.getAvailableLanguages()) {
            languageKeys.add(PREFIX + lang + SUFFIX);
        }

        for (String row : rows) {
            String[] namevalue = row.split("=");
            if (namevalue.length != 2) {
                continue;
            }
            allKeys.add(namevalue[0]);
        }

        assertTrue(allKeys.containsAll(languageKeys));
    }

    // Prints an non-localized keys for mandatory languages (currently Russian)
    @Test
    public void testIsMandatoryLanguagesTranslated() throws Exception {
        Map<String, String> enProps = Collections.unmodifiableMap(getPropertiesAsMap(Language.EN));
        for (Language lan : new Language[] { Language.RU }) {
            Map<String, String> lanProps = getPropertiesAsMap(lan);

            Set<String> enKeys = new HashSet<String>(enProps.keySet());
            enKeys.removeAll(lanProps.keySet());
            enKeys.removeAll(denyTranslation);

            print(enKeys, "Translation not found for", lan);
        }

        assertEquals(0, errorCount);
    }

    @Test
    public void testIsTranslationCorrect() throws Exception {
        Map<String, String> enProps = Collections.unmodifiableMap(getPropertiesAsMap(Language.EN));
        for (Language lan : new Language[] { Language.RU }) {
            Map<String, String> lanProps = Collections.unmodifiableMap(getPropertiesAsMap(lan));
            String[] rows = getProperties(lan);

            // check for denied translations
            if (lan != Language.EN) {
                Set<String> lanKeys = new HashSet<String>(lanProps.keySet());
                lanKeys.retainAll(denyTranslation);
                print(lanKeys, "Remove not needed translation for", lan);
            }

            // check for more then one resources on a line, for example
            // color.name=green color.resourceName=green color
            {
                for (String row : rows) {
                    String key = getKey(row, lan);

                    // skip the exclusions
                    if (!stickedResources.contains(key)) {
                        if (key.length() != row.lastIndexOf("=")) {
                            System.out.println("There are sticked resources on a single line " + lan.getIsoCode() + ": " + row);
                            errorCount ++;
                        }
                    }
                }
            }

            // check that translations are trimmed
            {
                for (String row : rows) {
                    String value = getValue(row);
                    if (value.startsWith(" ")) {
                        System.out.println("Remove prefix spaces for " + lan.getIsoCode() + ": " + row);
                        errorCount ++;
                    }
                }
            }

            // check for duplicates
            {
                Map<String, String> props = new HashMap<String, String>();
                for (String row : rows) {
                    String key = getKey(row, lan);
                    if (props.containsKey(key)) {
                        System.out.println("Remove duplicates for " + lan.getIsoCode() + ": " + key);
                        errorCount ++;
                        continue;
                    }

                    props.put(key, row);
                }
            }

            // check for not used resources
            if (lan != Language.EN) {
                Set<String> lanKeys = new HashSet<String>(lanProps.keySet());
                lanKeys.removeAll(enProps.keySet());
                print(lanKeys, "Remove unused", lan);
            }

            // check for html snippets like &nbsp; etc
            {
                for (String row : rows) {
                    String key = getKey(row, lan);

                    for (String html : knownHtmlSnippets) {
                        if (row.contains(html)) {
                            System.out.println("Remove html snippets from a translation " + lan.getIsoCode() + ": " + key);
                            errorCount ++;
                        }
                    }
                }
            }
        }

        assertEquals(0, errorCount);
    }

    private void print(Collection<String> keys, String msg, Language lan) {
        if (keys.isEmpty()) {
            return;
        }

        ArrayList<String> sorted = new ArrayList<String>(keys);
        Collections.sort(sorted);
        System.out.println(msg + " " + lan.getIsoCode() + ": ");
        for (String key : sorted) {
            System.out.println(key);
            errorCount ++;
        }
        System.out.println("====================\n");
    }

    /**
     * Some words to be translated from en to en_GB
     * <li>
     * <ul>behavioral -> behavioUral</ul>
     * </li>
     * @throws Exception e
     */
    @Test
    public void testEnGbTranslation() throws Exception {
        doTestEn(Arrays.asList("behavioral", "utilize"), new Locale("en", "GB"));
    }

    @Test
    public void testEnUsTranslation() throws Exception {
        doTestEn(Arrays.asList("behavioural", "utilise"), new Locale("en", "US"));
    }

    private void doTestEn(final List<String> toBeTranslated, final Locale locale) {
        final List<String> wrong = new ArrayList<String>();
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("resource/applicationResource", locale);

        doForAllResources(new Closure() {
            @Override
            public void execute(Object o) {
                String s = (String) o;
                String localized = resourceBundle.getString(s);

                for (String word : toBeTranslated) {
                    if (localized.toLowerCase().contains(word)) {
                        wrong.add(s);
                        System.out.println("To be translated to " + locale + ": " + s);
                    }
                }
            }
        });

        assertTrue(wrong.size() == 0);
    }

    private static String getKey(String row, Language language) {
        String suffix = language.getIsoCode();

        int pos = row.indexOf("=");
        assertTrue("Wrong resource record " + suffix + ": " + row, pos > 0);

        String key = row.substring(0, pos);
        assertFalse("Remove suffix spaces for " + suffix + ": " + row, key.endsWith(" "));
        assertFalse("Remove prefix spaces for " + suffix + ": " + row, key.startsWith(" "));

        return key;
    }

    private String getValue(String row) {
        int pos = row.indexOf("=");
        if (pos > 0) {
            return row.substring(pos + 1);
        } else {
            return row;
        }
    }

    private void doForAllResources(Closure closure) {
        Properties allProperties = PropertyHelper.readProperties("resource/applicationResource.properties");
        CollectionUtils.forAllDo(allProperties.stringPropertyNames(), closure);
    }

    public static Map<String, String> getPropertiesAsMap(Language language) throws Exception {
        String[] rows = getProperties(language);
        return arrayToMap(language, rows);
    }

    private static Map<String, String> arrayToMap(Language language, String[] rows) {
        Map<String, String> props = new HashMap<String, String>();
        for (String row : rows) {
            String key = getKey(row, language);
            props.put(key, row);
        }

        return props;
    }

    private static String[] getProperties(Language language) throws Exception {
        String lanSuffix = (language == Language.EN ? "" : "_" + language.getIsoCode());
        String fileName = "resource/applicationResource" + lanSuffix + ".properties";

        return readProperties(fileName);
    }

    private static String[] readProperties(String fileName) throws IOException {
        File resource = getResourceFile(fileName);

        String fileContent = ResourceUtil.readFileContent(resource.getPath());
        ArrayList<String> fileRows = new ArrayList<String>(Arrays.asList(fileContent.split("\n")));

        // skip comments
        int index = 0;
        String[] rows = new String[fileRows.size()];
        for (String row : fileRows) {
            if (row.trim().length() == 0 || row.startsWith("#") || row.startsWith("!")) {
                continue;
            }
            rows[index ++] = row;
        }

        return Arrays.copyOf(rows, index);
    }

    private static File getResourceFile(String resourceName) {
        URL url = ResourceMatchTest.class.getClassLoader().getResource(resourceName);
        File file = new File(url.getFile());
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalStateException("File " + resourceName + " not found");
        }

        return file;
    }
}
