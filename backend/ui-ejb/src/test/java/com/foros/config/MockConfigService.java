package com.foros.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;

public class MockConfigService implements ConfigService {
    public static final File DATA_ROOT = makeFolder(new File(System.getProperty("java.io.tmpdir")), "data-root-" + System.currentTimeMillis());

    static {
        makeFolder(DATA_ROOT, "fmroot");
        makeFolder(DATA_ROOT, "tags");
        makeFolder(DATA_ROOT, "fmroot/op");
        makeFolder(DATA_ROOT, "fmroot/Creatives");
        makeFolder(DATA_ROOT, "fmroot/terms");
        makeFolder(DATA_ROOT, "fmroot/Templates");
        makeFolder(DATA_ROOT, "fmroot/reports");
        makeFolder(DATA_ROOT, "fmroot/OFInvoices");
        makeFolder(DATA_ROOT, "fmroot/Publ");
        makeFolder(DATA_ROOT, "Discover");
        makeFolder(DATA_ROOT, "fmroot/KWMTool");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(DATA_ROOT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Map<ConfigParameter<?>, Object> parameters = new HashMap<ConfigParameter<?>, Object>();

    public MockConfigService() {
        setDefault();
    }

    private void setDefault() {
        set(ConfigParameters.DATA_ROOT, DATA_ROOT.getAbsolutePath());
        set(ConfigParameters.ALLOWED_FILE_TYPES, Arrays.asList("bmp", "gif", "jpeg", "png", "swf", "flv", "html", "js", "css"));
        set(ConfigParameters.ADMIN_FILE_MANAGER_FOLDER, "fmroot");
        set(ConfigParameters.PREVIEW_FOLDER, "Preview");
        set(ConfigParameters.TAGS_FOLDER, "tags");
        set(ConfigParameters.OPPORTUNITIES_FOLDER, "fmroot/op");
        set(ConfigParameters.ACCOUNT_DOCUMENTS_FOLDER, "fmroot/accountDocuments");
        set(ConfigParameters.CHANNEL_REPORT_FOLDER, "fmroot/ChanelReport");
        set(ConfigParameters.CREATIVES_FOLDER, "fmroot/Creatives");
        set(ConfigParameters.TERMS_FOLDER, "fmroot/terms");
        set(ConfigParameters.TEMPLATES_FOLDER, "fmroot/Templates");
        set(ConfigParameters.REPORTS_FOLDER, "fmroot/reports");
        set(ConfigParameters.PUBLISHER_ACCOUNTS_FOLDER, "fmroot/Publ");
        set(ConfigParameters.DISCOVER_FOLDER, "Discover");
        set(ConfigParameters.KWM_TOOL_FOLDER, "fmroot/KWMTool");
        set(ConfigParameters.PREVIEW_PATH, "/PREVIEW_PATH/");
    }

    private static File makeFolder(File parent, String child) {
        File file = new File(parent, child);
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
        return file;
    }

    @Override
    public Config detach() {
        return this;
    }

    @Override
    public <T> T get(ConfigParameter<T> parameter) {
        Object o = parameters.get(parameter);
        if (o == null) {
            o = parameter.getDefaultValue();
        }
        if (o == null && parameter.isRequired()) {
            throw new ConfigurationException("Required parameter not found: " + parameter);
        }
        return parameter.getType().cast(o);
    }

    public <T> T set(ConfigParameter<T> parameter, T value) {
        parameters.put(parameter, value);
        return get(parameter);
    }

    public void setParameters(Map<ConfigParameter<?>, Object> parameters) {
        this.parameters = parameters;
    }

    public void clear() {
        parameters.clear();
        setDefault();
    }
}
