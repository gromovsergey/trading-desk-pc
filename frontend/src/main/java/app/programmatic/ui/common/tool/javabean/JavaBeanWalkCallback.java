package app.programmatic.ui.common.tool.javabean;

public interface JavaBeanWalkCallback<T> {
    void process(String propertyName, MethodHandleProperty handle) throws Throwable;
}
