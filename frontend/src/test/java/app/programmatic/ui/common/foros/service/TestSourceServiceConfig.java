package app.programmatic.ui.common.foros.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Lazy
@TestConfiguration
public class TestSourceServiceConfig {
    private static final String URL = "http://dev03:4080";
    private static final String KEY = "69y3dTfhUdRXMvq9vq/BwMY1RxUMwt9qZVZjesKTVKULYxznuumaJJajMClRoJSs6ENb0usNcucBZ1xyILhPGQ==";
    private static final String TOKEN = "b9e15099-63a7-439b-a7e0-48e233336b51";

    private SourceServiceConfig sourceServiceConfig = new SourceServiceConfig();

    @Bean
    public ForosBuilder foros() {
        SourceServiceConfig.ApiSettings apiSettings = new SourceServiceConfig.ApiSettings();
        apiSettings.setUrl(URL);
        apiSettings.setKey(KEY);
        apiSettings.setUserToken(TOKEN);

        return sourceServiceConfig.foros(apiSettings);
    }

    @Bean
    public SourceServiceImpl sourceService() {
        return sourceServiceConfig.sourceService();
    }

    @Bean
    public TestCurUserTokenKeyService testCurUserTokenKey() {
        return new TestCurUserTokenKeyService(TOKEN, KEY);
    }
}
