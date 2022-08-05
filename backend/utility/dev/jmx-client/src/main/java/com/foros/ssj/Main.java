package com.foros.ssj;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Number of parameters is invalid");
            System.out.println("\nThis utility is intended for reading hard-coded\n" +
                "JMX attributes only from local JVM.\n" +
                "Args:\n" +
                "  <PID>: PID of monitored JVM process\n" +
                "  <JMX Objects file>: file with the MBeans to monitor\n" +
                "  0|1: 0 - don't initialize MBean's tree before a read; 1 - do");
            System.exit(1);
        }

        String jvmPid = args[0];
        String propertiesFileName = args[1];
        boolean initMBeans = args[2].equals("1");

        JmxClient client = new JmxClient();
        if (!client.connectToJVM(jvmPid, initMBeans)) {
            System.exit(1);
        }

        if (!client.readProperties(propertiesFileName)) {
            System.exit(1);
        }

        if (!client.printJmxValues()) {
            System.exit(1);
        }
    }
}
