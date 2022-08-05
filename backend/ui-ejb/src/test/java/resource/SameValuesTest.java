package resource;

import com.foros.model.security.Language;

import group.Resource;
import group.Unit;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class SameValuesTest extends Assert {

    @Test
    public void testSameValues() throws Exception {
        int errorCount = 0;

        Properties defaultProps = readProperties(Language.EN);
        Map<String, List<String>> sameValuesMap = getValuesMap(defaultProps);

        for (Language language : new Language[]{}) {
            String langSuffix = language.getIsoCode();
            Properties langProps = readProperties(language);

            for (Map.Entry<String, List<String>> entry : sameValuesMap.entrySet()) {
                List<String> keys = entry.getValue();
                if (keys.size() > 1) {
                    String value = null;

                    for (String key : keys) {
                        String currValue = langProps.getProperty(key);
                        if (currValue == null) {
                            continue;
                        }
                        if (value == null) {
                            value = currValue;
                        }
                        if (!value.equalsIgnoreCase(currValue)) {
                            System.out.println("Different translation for " + langSuffix + ": \"" + entry.getKey() + "\"");
                            for (String printKey : keys) {
                                System.out.println("  key: " + printKey);
                            }
                            System.out.println();

                            errorCount++;
                            break;
                        }
                    }
                }
            }
        }

        assertEquals(0, errorCount);
    }

    private Map<String, List<String>> getValuesMap(Properties props) throws IOException {
        Map<String, List<String>> values = new HashMap<>();

        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key).toUpperCase();
            if (!value.isEmpty()) {
                List<String> keys = values.get(value);
                if (keys == null) {
                    keys = new ArrayList<>();
                    values.put(value, keys);
                }
                keys.add(key);
            }
        }

        return values;
    }

    private Properties readProperties(Language language) throws IOException {
        String lanSuffix = (language == Language.EN ? "" : "_" + language.getIsoCode());
        String name = "resource/applicationResource" + lanSuffix + ".properties";
        try(InputStream is = SameValuesTest.class.getClassLoader().getResourceAsStream(name)) {
            Properties props = new Properties();
            props.load(is);
            return props;
        }
    }
}