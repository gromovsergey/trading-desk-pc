package com.foros.ssj;

import com.sun.tools.attach.VirtualMachine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JmxClient {
    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

    private MBeanServerConnection mbeanServerConnection;
    private ArrayList<JmxObject> jmxObjects;

    private boolean checkPid(String jvmPid) {
        boolean isPidValid = true;
        try {
            if (Long.valueOf(jvmPid) < 1) {
                isPidValid = false;
            }
        } catch (Exception e) {
            isPidValid = false;
        }
        if (!isPidValid) {
            System.err.println("Error: invalid PID=" + jvmPid);
        }
        return isPidValid;
    }

    public boolean connectToJVM(String jvmPid, boolean initMBeans) {
        try {
            if (checkPid(jvmPid)) {
                VirtualMachine vm = VirtualMachine.attach(jvmPid);
                String connectorAddress = getConnectorAddress(vm);
                JMXServiceURL url = new JMXServiceURL(connectorAddress);
                JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
                mbeanServerConnection = jmxConnector.getMBeanServerConnection();

                if (initMBeans) {
                    // means only for Glassfish, for Replication Utility there is no such MBean
                    ObjectName amxSupport = new ObjectName("amx-support:type=boot-amx");
                    mbeanServerConnection.invoke(amxSupport, "bootAMX", new Object[0], new String[0]);
                }
                return true;
            }
        }
        catch (Throwable ex) {
            System.err.println("Exception caught: " + ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    private Object execScript(JmxObject obj, Object value) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
        jsEngine.put("value", value);
        try {
            jsEngine.eval(obj.getValue());
            return jsEngine.get("value");
        } catch (ScriptException e) {
            System.err.println("Exception occurred executing script of property " + obj.getPropertyName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return value;
    }

    private String getConnectorAddress(VirtualMachine vm) throws Exception {
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        if (connectorAddress == null) {
            // no connector address, so we start the JMX agent
            String agent = vm.getSystemProperties().getProperty("java.home") +
                File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);

            // agent is started, get the connector address
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

            if (connectorAddress == null) {
                throw new RuntimeException("Error: can't fetch connector address.");
            }
        }

        return connectorAddress;
    }

    public void printErrorValues() {
        for (JmxObject obj: jmxObjects) {
            System.out.println(obj.getPropertyName() + " = -1");
        }
    }

    public boolean printJmxValues() {
        Map<Object, Object> cache = new HashMap<Object, Object>();

        boolean result = true;
        for (JmxObject obj: jmxObjects) {
            Object res = "-1";
            try {
                Object cacheKey = obj.getCacheKey();
                Object value = cache.get(cacheKey);
                if (value == null) {
                    value = mbeanServerConnection.getAttribute(obj.getObjectName(), obj.getAttribute());
                    cache.put(cacheKey, value);
                }

                if (obj.getAttributeParam() != null) {
                    // Composite object expected
                    if (value instanceof CompositeData) {
                        res = ((CompositeData)value).get(obj.getAttributeParam());
                    } else {
                        result = false;
                        System.err.println("Error: type of " + obj.getPropertyName() + "is not composite.");
                    }
                } else {
                    res = value;
                }
            } catch (InstanceNotFoundException infex) {
                System.err.println("Not found: " + obj.getPropertyName());
                result = false;
            } catch (Exception ex) {
                result = false;
                System.err.println("Not Available: " + obj.getPropertyName());
                ex.printStackTrace();
            }
            res = execScript(obj, res);
            System.out.println(obj.getPropertyName() + " = " + res);
        }
        return result;
    }

    public boolean readProperties(String propertiesFileName) {
        jmxObjects = new ArrayList<JmxObject>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(propertiesFileName), Charset.forName("UTF-8")));
            JSONArray jsonArray = new JSONArray(new JSONTokener(br));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                JmxObject jmxObject = new JmxObject(json.getString("name"), json.getString("mbean"), json.getString("attribute"));
                if (json.has("param")) {
                    jmxObject.setAttributeParam(json.getString("param"));
                }
                if (json.has("value")) {
                    jmxObject.setValue(json.getString("value"));
                }
                jmxObjects.add(jmxObject);
            }
            return true;
        } catch (Exception ex) {
            System.err.println("Can't read file: " + propertiesFileName);
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    System.err.println("Can't close stream of the file: " + propertiesFileName);
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
}
