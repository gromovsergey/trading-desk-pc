package resource;

import group.Resource;
import group.Unit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class ResourceConsistencyTest extends Assert {

    @Test
    public void testChars() throws Exception {

        String content = resource.ResourceUtil.readFileContent("target/classes/resource/applicationResource.properties");
        String[] lines = content.split("\\n");
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                assertFalse("Illegal char in following line: " + line, c < 32 || c > 126);
            }
        }
    }
}