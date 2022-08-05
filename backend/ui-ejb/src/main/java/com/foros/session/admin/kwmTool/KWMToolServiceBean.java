package com.foros.session.admin.kwmTool;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.config.ConfigurationException;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.fileman.PathProviderService;
import com.foros.util.ResourceUtil;
import com.foros.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless(name = "KWMToolService")
@Interceptors({RestrictionInterceptor.class})
public class KWMToolServiceBean implements KWMToolService {
    private static final Logger logger = Logger.getLogger(KWMToolServiceBean.class.getName());

    private static String toolPath;
    private static String ldLibraryPath;
    private static String kwmProxy;

    private static final String SEGMENTOR_TEST_MODE = "--mode=segmentor-test";

    private static final String SOURCE_FORMAT = "--source-text-format={0}";
    private static final String SOURCE_FORMAT_HTML = "HTML";
    private static final String SOURCE_FORMAT_TEXT = "TEXT";

    private static final String SOURCE_URL = "--source-urls={0}";

    private static final String MAX_TEXT_SIZE = "--text-maximum={0} bytes";
    private static final String MIN_TEXT_SIZE = "--text-minimum=1";
    private static final String MAX_HTML_SIZE = "--html-maximum={0} bytes";
    private static final String MIN_HTML_SIZE = "--html-minimum=1";

    private static final String LOOPS_COUNT = "--test-numbers={0}";

    private static final String XML_CONFIG = "--xml-config={0}";

    private static final String OUTPUT_SEPARATOR = "--output-separator={0}";
    private static final String OUTPUT_SEPARATOR_STRING = "----------===CTR5C789MXFKSXQDHHWRMXIUWXMGHSXSV===----------";

    private static final String HTTP_PROXY = "--http-proxy={0}";

    private static final String USER_AGENT = "--user-agent={0}";

    @EJB
    PathProviderService pathProviderService;

    @EJB
    ConfigService configService;

    @Override
    @Restrict(restriction="KWMTool.view")
    public KWMToolResult runKWMTool(int sourceFormat, String url, int maxSize, int loops, String xmlConfig, String inputText, String userAgent) throws KWMToolException {
        setupKWMToolProperties();

        String absoluteConfigPath = pathProviderService.getKwmTool().getPath(xmlConfig).getAbsolutePath();

        // Tool was temporary turned off in OUI-28867
        return new KWMToolResult("", OUTPUT_SEPARATOR_STRING, absoluteConfigPath);
        //return new KWMToolResult(runTool(sourceFormat, url, maxSize, loops, absoluteConfigPath, inputText, userAgent), OUTPUT_SEPARATOR_STRING, absoluteConfigPath);
    }

    private String runProcess(String[] args, String input) throws KWMToolException {
        String[] env = prepareEnvironmentVariables();
        String result;

        try {
            logger.info("Exec KWMTool process:\n" +
                    StringUtils.join(args, ' ') + '\n' +
                    "Environment:\n" + StringUtils.join(env, '\n') +
                    "\nInput: " + (input == null ? "" : input));
            Process toolProcess = Runtime.getRuntime().exec(args, env);

            if (StringUtil.isPropertyNotEmpty(input)) {
                toolProcess.getOutputStream().write(input.getBytes("UTF-8"));
                toolProcess.getOutputStream().close();
            }

            result = ResourceUtil.readStreamToString(toolProcess.getInputStream());
            logger.info("KWMTool's output is:\n" + (result == null ? "" : result));

            int exitCode = toolProcess.waitFor();
            if (exitCode != 0) {
                String errorMessage = ResourceUtil.readStreamToString(toolProcess.getErrorStream());
                logger.severe("Failed to execute KWM Tool: exit code " + exitCode + "\nError message is: " + errorMessage);
                throw new KWMToolException(errorMessage, exitCode);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to execute KWM Tool: IOException : " + e.getMessage(), e);
            throw new KWMToolException("error.executingKWMTool", e);
        }  catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to execute KWM Tool: InterruptedException : " + e.getMessage(), e);
            throw new KWMToolException("error.executingKWMTool", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Failed to execute KWM Tool: NullPointerException : " + e.getMessage(), e);
            throw new KWMToolException("error.executingKWMTool", e);
        }

        logger.info("KWMTool process ended successfully with exit code 0 ");
        return result;
    }

    private String[] prepareCommandLine(int sourceFormat, String url, int maxSize, int loops, String xmlConfig, String userAgent) throws KWMToolException {
        List<String> args = new LinkedList<String>();

        args.add(toolPath);
        args.add(SEGMENTOR_TEST_MODE);

        switch (sourceFormat) {
            case URL:
                args.add(MessageFormat.format(SOURCE_URL, url));
                if (maxSize != 0) {
                    args.add(MessageFormat.format(MAX_TEXT_SIZE, Integer.valueOf(maxSize).toString()));
                }
                break;
            case TEXT:
                args.add(MessageFormat.format(SOURCE_FORMAT, SOURCE_FORMAT_TEXT));
                args.add(MessageFormat.format(MAX_HTML_SIZE, Integer.valueOf(maxSize).toString()));
                args.add(MIN_HTML_SIZE);
                args.add(MessageFormat.format(MAX_TEXT_SIZE, Integer.valueOf(maxSize).toString()));
                args.add(MIN_TEXT_SIZE);
                break;
            case HTML:
                args.add(MessageFormat.format(SOURCE_FORMAT, SOURCE_FORMAT_HTML));
                args.add(MessageFormat.format(MAX_HTML_SIZE, Integer.valueOf(maxSize).toString()));
                args.add(MIN_HTML_SIZE);
                args.add(MessageFormat.format(MAX_TEXT_SIZE, Integer.valueOf(maxSize).toString()));
                args.add(MIN_TEXT_SIZE);
                break;
            default:
               throw new KWMToolException("error.badSourceFormat");
        }

        args.add(MessageFormat.format(LOOPS_COUNT, Integer.valueOf(loops).toString()));
        args.add(MessageFormat.format(XML_CONFIG, xmlConfig));
        args.add(MessageFormat.format(OUTPUT_SEPARATOR, OUTPUT_SEPARATOR_STRING));

        if (!kwmProxy.startsWith("localhost")) {
            args.add(MessageFormat.format(HTTP_PROXY, kwmProxy));
        }

        args.add(MessageFormat.format(USER_AGENT, userAgent));

        return args.toArray(new String[args.size()]);
    }

    private String runTool(int sourceFormat, String url, int maxSize, int loops, String xmlConfig, String inputText, String userAgent) throws KWMToolException {
        String[] args = prepareCommandLine(sourceFormat, url, maxSize, loops, xmlConfig, userAgent);

        return runProcess(args, sourceFormat == URL ? null : inputText);
    }

    private String[] prepareEnvironmentVariables() {
        Map<String, String> envMap = System.getenv();

        List<String> env = new ArrayList<String>(envMap.size() + 1);

        boolean addLDLibraryPath = true;

        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            if ("LD_LIBRARY_PATH".equals(name)) {
                env.add(name + "=" +  ldLibraryPath);
                addLDLibraryPath = false;
            } else {
                env.add(name + "=" + value);
            }
        }

        if (addLDLibraryPath) {
            env.add("LD_LIBRARY_PATH=" + ldLibraryPath);
        }

        return env.toArray(new String[env.size()]);
    }

    private void setupKWMToolProperties() throws KWMToolException {
        try {

            if (toolPath == null) {
                toolPath = configService.get(ConfigParameters.KWM_TOOL_PATH);
            }

            if (ldLibraryPath == null) {
                ldLibraryPath = configService.get(ConfigParameters.KWM_TOOL_LD_LIBRARY_PATH);
            }

            if (kwmProxy == null) {
                kwmProxy = configService.get(ConfigParameters.HTTP_PROXY_HOST) + ":" +
                        configService.get(ConfigParameters.HTTP_PROXY_PORT);
            }
        } catch (ConfigurationException e) {
            logger.severe("Invalid configuration : ConfigurationException : " + e.getMessage());
            throw new KWMToolException("error.configuration", e);
        }
    }
}
