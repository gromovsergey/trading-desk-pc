package app.programmatic.ui.common.foros.service;

public interface ForosServiceConfigurator {
    void configure(String userKey, String userToken);
    void cleanUp();
}
