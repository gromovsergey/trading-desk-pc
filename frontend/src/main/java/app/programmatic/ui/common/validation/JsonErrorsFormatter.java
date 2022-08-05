package app.programmatic.ui.common.validation;

import static app.programmatic.ui.common.validation.ConstraintViolationBuilder.CURRENT_ENTITY_MARKER;
import static app.programmatic.ui.common.validation.ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME;

import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.validation.exception.ExpectedForosViolationsException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


public class JsonErrorsFormatter {
    private static String SINGLE_ERROR_MSG_TEMPLATE = "{\"%s\":[\"%s\"]}";
    private static Pattern SPRING_CUTTER = Pattern.compile(".*[.]?" + CURRENT_ENTITY_MARKER + "[.]");
    private static Pattern JACKSON_CUTTER = Pattern.compile(".*\\[([^\\]]+)\\].*");
    private static MessageInterpolator MESSAGE_INTERPOLATOR = MessageInterpolator.getDefaultMessageInterpolator();
    private static ObjectMapper mapper = new ObjectMapper();


    public static String buildJsonOutput(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        Map<String, Set<String>> violationDescriptions = new HashMap<>(violations.size());

        for (ConstraintViolation violation : violations) {
            String path = SPRING_CUTTER.matcher(violation.getPropertyPath().toString()).replaceFirst("");
            addViolationDescription(path, violation.getMessage(), violationDescriptions);
        }

        return buildJsonFieldErrorsOutput(violationDescriptions);
    }

    public static String buildJsonOutput(ValidationException e) {
        try (StringWriter sw = new StringWriter(); JsonGenerator generator = new JsonFactory().createGenerator(sw)) {
            generator.writeStartObject();
            generator.writeArrayFieldStart(GENERAL_ERROR_FIELD_NAME);

            String[] errorMessages = e.getMessage().split("\n");
            for (String errorMessage : errorMessages) {
                generator.writeString(errorMessage);
            }

            generator.writeEndArray();
            generator.writeEndObject();
            generator.flush();

            return sw.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static String buildOptimisticLockJsonOutput() {
        return String.format(SINGLE_ERROR_MSG_TEMPLATE,
                GENERAL_ERROR_FIELD_NAME,
                MESSAGE_INTERPOLATOR.interpolate("entity.error.version"));
    }

    public static String buildEntityNotFoundJsonOutput(Object id) {
        return String.format(SINGLE_ERROR_MSG_TEMPLATE,
                GENERAL_ERROR_FIELD_NAME,
                MESSAGE_INTERPOLATOR.interpolate("entity.error.notFound", id));
    }

    public static String buildJsonOutput(ExpectedForosViolationsException e) {
        return buildJsonFieldErrorsOutput(e.getViolationDescriptions());
    }

    public static String buildJsonOutput(JsonMappingException mappingException) {
        String path = fetchPathFromMappingException(mappingException);
        String errorMsg = MESSAGE_INTERPOLATOR.interpolate("entity.field.error.invalidValue");
        Map<String, Set<String>> violationDescriptions = new HashMap<>();
        addViolationDescription(path, errorMsg, violationDescriptions);

        return buildJsonFieldErrorsOutput(violationDescriptions);
    }

    public static String buildJsonOutput(InvalidFormatException mappingException) {
        String path = fetchPathFromMappingException(mappingException);
        String errorMsg = toClassMappingErrorMsg(mappingException.getTargetType(), mappingException.getValue());
        Map<String, Set<String>> violationDescriptions = new HashMap<>();
        addViolationDescription(path, errorMsg, violationDescriptions);

        return buildJsonFieldErrorsOutput(violationDescriptions);
    }

    private static String fetchPathFromMappingException(JsonMappingException mappingException) {
        return mappingException.getPath().stream()
                .map( ref -> normalizePathFromJackson(ref) )
                .collect(Collectors.joining("."));
    }

    private static String buildJsonFieldErrorsOutput(Map<String, Set<String>> violationDescriptions) {
        try (StringWriter sw = new StringWriter(); JsonGenerator generator = new JsonFactory().createGenerator(sw)) {
            generator.setCodec(mapper);
            Map root = new HashMap();

            for (Map.Entry<String, Set<String>> entry : violationDescriptions.entrySet()) {
                String[] paths = entry.getKey().split("\\.");
                Map container = root;
                for (int i = 0; i < paths.length; i++) {
                    String path = paths[i];
                    int index = 0;
                    boolean isArrays = path.matches(".+\\[\\d+\\]");
                    boolean isLast = i == paths.length - 1;
                    if (isArrays) {
                        index = Integer.parseInt(path.substring(path.indexOf('[') + 1, path.indexOf(']')));
                        path = path.substring(0, path.indexOf('['));
                    }

                    Map newContainer;
                    if (isArrays) {
                        List list = (List) container.get(path);
                        if (list == null) {
                            list = new ArrayList();
                            container.put(path, list);
                        }
                        if (isLast) {
                            newContainer = new HashMap();
                            list.addAll(entry.getValue());
                        } else {
                            if (list.size() <= index) {
                                list.addAll(Arrays.asList(new Object[index + 1 - list.size()]));
                            }
                            newContainer = list.get(index) == null ? new HashMap() : (Map) list.get(index);
                            list.set(index, newContainer);
                        }
                    } else {
                        newContainer = container.get(path) == null ?  new HashMap() : (Map)container.get(path);
                        container.put(path, isLast ? entry.getValue() : newContainer);
                    }
                    container = newContainer;
                }
            }
            generator.writeObject(root);
            return sw.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static void addViolationDescription(String path, String message, Map<String, Set<String>> map) {
        Set<String> messages = map.get(path);
        if (messages == null) {
            messages = new HashSet<>();
            map.put(path, messages);
        }

        messages.add(message);
    }

    private static <T> String toClassMappingErrorMsg(Class<T> clazz, Object value) {
        if (Number.class.isAssignableFrom(clazz)) {
            return MESSAGE_INTERPOLATOR.interpolate("entity.field.error.notNumber", value);
        }
        return MESSAGE_INTERPOLATOR.interpolate("entity.field.error.cantConvert", value);
    }

    private static String normalizePathFromJackson(JsonMappingException.Reference ref) {
        String path = ref.getFieldName();
        if (path == null) {
            if (!(ref.getFrom() instanceof Collection)) {
                throw new RuntimeException("Can't build path from element " + ref.toString());
            }
            return "rows[" + ref.getIndex() + "]";
        }

        Matcher jacksonMatcher = JACKSON_CUTTER.matcher(path);
        return jacksonMatcher.matches() ? jacksonMatcher.group(1) : path;
    }

    public static String buildServerIsBusyJsonOutput() {
        return String.format(SINGLE_ERROR_MSG_TEMPLATE,
                GENERAL_ERROR_FIELD_NAME,
                MESSAGE_INTERPOLATOR.interpolate("errors.serverIsBusy"));
    }
}
