package com.foros.session.context;

public class EjbContext {

    private static ThreadLocal<Integer> value = new ThreadLocal<Integer>();

    public static boolean inContext() {
        return getValue() > 0;
    }

    public static void setContextOn() {
        value.set(getValue() + 1);
    }

    public static void setContextOff() {
        value.set(getValue() - 1);
    }

    private static int getValue() {
        Integer result = value.get();
        return result == null ? 0 : result;
    }

}
