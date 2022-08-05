package com.foros.util;

import java.util.Arrays;
import java.util.List;

public class MultiKey {
    private List<Object> keyElements;

    public MultiKey(Object... keyElements) {
        this.keyElements = Arrays.asList(keyElements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiKey multiKey = (MultiKey) o;

        if (!keyElements.equals(multiKey.keyElements)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return keyElements.hashCode();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Object o : keyElements) {
            str.append("<").append(o).append(">");
        }
        return str.toString();
    }
}
