package resource;

import group.Resource;
import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.Set;

@Category({ Unit.class, Resource.class })
public class ExcelResourceTest extends Assert {
    static final String[] LANGSUFFIXES = {"", "_ru"};

    /**
     *
     * Test that for each excel string there is a non-excel string
     *
     * @throws Exception
     */
    @Test
    public void testExcelResource() throws Exception {
        for (String langSuffix : LANGSUFFIXES) {
            String content = resource.ResourceUtil.readFileContent("target/classes/resource/applicationResource" + langSuffix + ".properties");
            String[] rows = content.split("\n");

            Set<String> notExcelNames = new HashSet<String>();
            Set<String> excelNames = new HashSet<String>();

            for (String row : rows) {
                String[] namevalue = row.split("=");
                if (namevalue.length != 2) {
                    continue;
                }

                String name = namevalue[0];

                final String SUFFIX = ".excelSheet";

                if (name.endsWith(SUFFIX)) {
                    excelNames.add(name.replaceAll(SUFFIX, ""));
                } else {
                    notExcelNames.add(name);
                }
            }

            assertTrue(notExcelNames.containsAll(excelNames));
        }
    }
}
