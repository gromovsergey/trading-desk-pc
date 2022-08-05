package resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

public class ResourceUtil {
    /**
     * Read a external file and return his content as string
     * @param fileName a full file name
     * @return file content
     * @throws java.io.IOException
     */
    public static String readFileContent(String fileName) throws IOException {
        return readFileContent(new File(fileName));
    }

    private static String readFileContent(File file) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String result;

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            StringWriter stringWriter = new StringWriter();

            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }

            StringBuffer buffer = stringWriter.getBuffer();
            result = buffer.toString();
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }

            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return result;
    }

}
