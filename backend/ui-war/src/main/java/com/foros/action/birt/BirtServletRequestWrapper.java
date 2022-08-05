package com.foros.action.birt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class BirtServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameters;

    private Set<String> modifiedParams = new HashSet<String>();

    private ServletInputStream modifiedInputStream = null;

    public BirtServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String returnValue = null;
        String[] paramArray = getParameterValues(name);
        if (paramArray != null && paramArray.length > 0) {
            returnValue = paramArray[0];
        }
        return returnValue;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(getParameters());
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameters().keySet());
    }
    
    public Collection<String> getParameterNamesAsCollection() {
        return Collections.unmodifiableCollection(new ArrayList<String>(getParameters().keySet()));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] temp = getParameters().get(name);
        if (temp != null) {
            result = new String[temp.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
        }
        return result;
    }

    public void setParameter(String name, String value) {
        String[] oneParam = { value };
        setParameter(name, oneParam);
    }

    public void setParameter(String name, String[] values) {
        modifiedParams.add(name);
        getParameters().put(name, values);
    }
    
    public boolean removeParameter(String name) {
        modifiedParams.add(name);
        return getParameters().remove(name) != null;
        
    }
    
    private Map<String, String[]> getParameters() {
        Map<String, String[]> wrappedParameters = getRequest().getParameterMap();
        if (parameters == null) {
            parameters = new HashMap<String, String[]>(wrappedParameters);
        }
        /**
         * Use this check to handle additional parameters passed with jsp
         * includes (e.g. jsp:param tag). Otherwise parameter changes in the
         * underlying request would not be seen.
         */
        for (Entry<String, String[]> parameterEntry : wrappedParameters.entrySet()) {
            String name = parameterEntry.getKey();
            if (!modifiedParams.contains(name)) {
                String[] value = parameterEntry.getValue();
                parameters.put(name, value);
            }
        }
        return parameters;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (modifiedInputStream != null) {
            return modifiedInputStream;
        }
        return super.getInputStream();
    }

    public void setInputStream(ServletInputStream modifiedInputStream) {
        this.modifiedInputStream = modifiedInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
