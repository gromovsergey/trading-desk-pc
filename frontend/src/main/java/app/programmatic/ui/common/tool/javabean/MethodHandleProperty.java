package app.programmatic.ui.common.tool.javabean;

import java.lang.invoke.MethodHandle;

public class MethodHandleProperty {
    private MethodHandle setMethod;
    private MethodHandle getMethod;

    public MethodHandleProperty(MethodHandle setMethod, MethodHandle getMethod) {
        this.setMethod = setMethod;
        this.getMethod = getMethod;
    }

    public MethodHandle getSetMethod() {
        return setMethod;
    }

    public MethodHandle getGetMethod() {
        return getMethod;
    }
}
