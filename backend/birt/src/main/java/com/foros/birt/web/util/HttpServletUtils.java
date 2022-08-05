package com.foros.birt.web.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public abstract class HttpServletUtils {

    public static HttpServletRequest addParameters(HttpServletRequest request, Map<String, Object> parameters) {
        return new HttpServletRequestWithAdditionalParameters(request, parameters);
    }

    private static final class HttpServletRequestWithAdditionalParameters extends HttpServletRequestWrapper {

        private Map<String, String[]> parameters;

        private HttpServletRequestWithAdditionalParameters(HttpServletRequest request, Map<String, Object> parameters) {
            super(request);
            this.parameters = convert(parameters, request.getParameterMap());
        }

        private Map<String, String[]> convert(Map<String, Object> parameters,
                                              Map<String, String[]> existingParameters) {
            HashMap<String, String[]> result = new HashMap<String, String[]>(existingParameters);
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Iterable) {
                    result.put(entry.getKey(), toStringArray((Iterable) value));
                } else {
                    result.put(entry.getKey(), new String[]{ String.valueOf(value) });
                }
            }
            return result;
        }

        private String[] toStringArray(Iterable<?> values) {
            ArrayList<String> result = new ArrayList<String>();
            for (Object value : values) {
                result.add(String.valueOf(value));
            }
            return result.toArray(new String[result.size()]);
        }

        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            return values == null ? null : values[0];

        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameters;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return new IteratorEnumeration<String>(parameters.keySet().iterator());
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = parameters.get(name);

            if (values == null || values.length == 0) {
                return null;
            }

            return values;
        }

        @Override
        public String getCharacterEncoding() {
            String characterEncoding = super.getCharacterEncoding();
            return characterEncoding != null ? characterEncoding : "UTF-8";
        }

    }
}
