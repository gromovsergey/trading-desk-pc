package com.foros.test;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

public class Capture<T> implements IArgumentMatcher {
    private T value;
    @Override
    public boolean matches(Object o) {
        value = (T) o;
        return true;
    }

    @Override
    public void appendTo(StringBuffer buffer) {
        buffer.append("capture()");
    }

    public T getValue() {
        return value;
    }

    public static <T> T capture(Capture<T> capture)
    {
        EasyMock.reportMatcher(capture);

        return null;
    }
}
