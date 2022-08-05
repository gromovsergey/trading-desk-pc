package com.foros.session.fileman;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.util.ValidationUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
@Stateless(name = "SwfToHtmlConverterService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class SwfToHtmlConverterServiceBean implements SwfToHtmlConverterService {

    private static final Logger logger = Logger.getLogger(SwfToHtmlConverterService.class.getName());

    private static final String SWIFFY_URL = "https://www.googleapis.com/rpc?key=AIzaSyCC_WIu0oVvLtQGzv4-g7oaWNoc-u8JpEI";

    @Override
    @Validate(validation = "SwfToHtmlConverter.convert", parameters = {"#fileManager", "#dir", "#swfFileName", "#withoutClickUrlMacro", "#htmlWithoutClickUrlMacroFileName", "#withClickUrlMacro", "#htmlWithClickUrlMacroFileName", "#clickMacro"})
    public List<String> convert(FileManager fileManager,
                        String dir,
                        String swfFileName,
                        Boolean withoutClickUrlMacro,
                        String htmlWithoutClickUrlMacroFileName,
                        Boolean withClickUrlMacro,
                        String htmlWithClickUrlMacroFileName,
                        String clickMacro
    ) {
        try (InputStream sourceStream = fileManager.readFile(dir, swfFileName).getStream()) {
            SwfToHtmlConversionResult conversionResult = convert(sourceStream);
            String html = conversionResult.getHtml();

            ValidationContext context = ValidationUtil.createContext();
            if (withoutClickUrlMacro) {
                checkClickTagPresence(context, html, "link1");
            }
            if (withClickUrlMacro) {
                checkClickTagPresence(context, html, clickMacro);
            }
            if (context.getConstraintViolations().size() > 0) {
                throw new ConstraintViolationException(context.getConstraintViolations());
            }

            if (withoutClickUrlMacro) {
                try (InputStream targetStream = IOUtils.toInputStream(html)) {
                    fileManager.createFile(dir, htmlWithoutClickUrlMacroFileName, targetStream);
                }
            }

            if (withClickUrlMacro && !StringUtil.isPropertyEmpty(clickMacro)) {
                html = html.replaceAll("<div id=\"swiffycontainer\".*>\\n\\s*</div>",
                        "<a href=\"##CLICK##\" target=\"_blank\">$0</a>");
                html = html.replaceAll("stage\\.start\\(\\);", "stage.setFlashVars(\"" + StringEscapeUtils.escapeEcmaScript(clickMacro) + "=\");$0");

                try (InputStream targetStream = IOUtils.toInputStream(html)) {
                    fileManager.createFile(dir, htmlWithClickUrlMacroFileName, targetStream);
                }
            }

            return conversionResult.getWarnings();
        } catch (FileManagerException | IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw ConstraintViolationException
                    .newBuilder("fileman.error.conversion")
                    .withParameters(swfFileName)
                    .withPath("sourceFileName")
                    .build();
        }
    }

    private void checkClickTagPresence(ValidationContext context, String html, String clickTag) {
        if (!html.contains("\"" + clickTag + "\"")) {
            context.addConstraintViolation("fileman.error.swiffyDoesNotContainTag").withParameters(clickTag).withPath("swiffy");
        }
    }

    private SwfToHtmlConversionResult convert(InputStream is) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (JsonGenerator jsonGenerator = new JsonFactory().createGenerator(byteArrayOutputStream)) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("apiVersion", "v1");
            jsonGenerator.writeStringField("method", "swiffy.convertToHtml");
            jsonGenerator.writeObjectFieldStart("params");
            jsonGenerator.writeStringField("client", "Swiffy Flash Extension");
            jsonGenerator.writeStringField("input", Base64.encodeBase64URLSafeString(IOUtils.toByteArray(is)));
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
            jsonGenerator.flush();
        }

        byte[] response = callMethod(byteArrayOutputStream.toByteArray());

        JsonNode rootNode = new ObjectMapper().readTree(response);

        JsonNode statusNode = rootNode.findValue("status");
        if (statusNode == null || !"SUCCESS".equals(statusNode.textValue())) {
            throw new IOException("Unsuccessful response from Swiffy: " + new String(response));
        }

        GoogleMessagesParser googleMessagesParser = new GoogleMessagesParser();
        googleMessagesParser.parse(rootNode);
        googleMessagesParser.throwIfHasViolations();

        JsonNode outputNode = rootNode.findValue("output");
        byte[] zip = Base64.decodeBase64(outputNode.textValue());
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(zip))) {
            return new SwfToHtmlConversionResult(IOUtils.toString(gzipInputStream), googleMessagesParser.getWarnings());
        }
    }

    private byte[] callMethod(byte[] request) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(SWIFFY_URL);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request);
            httpPost.setEntity(new InputStreamEntity(byteArrayInputStream));

            return httpClient.execute(httpPost, new ResponseHandler<byte[]>() {
                @Override
                public byte[] handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        return EntityUtils.toByteArray(response.getEntity());
                    } else {
                        throw new IOException("Unexpected response status from Swiffy: " + status);
                    }
                }
            });
        }
    }

    private class SwfToHtmlConversionResult {
        private final String html;
        private final List<String> warnings;

        private SwfToHtmlConversionResult(String html, List<String> warnings) {
            this.html = html;
            this.warnings = warnings;
        }

        public String getHtml() {
            return html;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }

    private class GoogleMessagesParser {
        private static final String MESSAGES_TAG = "messages";
        private static final String MESSAGE_TYPE_TAG = "type";
        private static final String MESSAGE_TYPE_ERROR = "ERROR";
        private static final String MESSAGE_TAG = "description";

        private List<String> warnings = new ArrayList<>(6);
        private ValidationContext context;

        public void parse(JsonNode rootNode) {
            JsonNode messagesNode = rootNode.findValue(MESSAGES_TAG);
            if (messagesNode == null) {
                return;
            }

            boolean hasErrors = messagesNode.findValuesAsText(MESSAGE_TYPE_TAG).contains(MESSAGE_TYPE_ERROR);
            if (hasErrors) {
                parseErrors(messagesNode);
            } else {
                parseWarnings(messagesNode);
            }
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void throwIfHasViolations() throws ConstraintViolationException {
            if (context != null) {
                throw new ConstraintViolationException(context.getConstraintViolations());
            }
        }

        private void parseErrors(JsonNode messagesNode) {
            context = ValidationUtil.createContext();
            for (String description : messagesNode.findValuesAsText(MESSAGE_TAG)) {
                context.addConstraintViolation("fileman.error.swiffyMessage")
                        .withParameters(description).withPath("swiffy");
            }
        }

        private void parseWarnings(JsonNode messagesNode) {
            for (String description : messagesNode.findValuesAsText(MESSAGE_TAG)) {
                warnings.add(description);
            }
        }
    }
}
