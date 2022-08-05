package app.programmatic.ui;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

import app.programmatic.ui.fileNew.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import app.programmatic.ui.common.config.ApplicationManagerConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@EnableConfigurationProperties({FileStorageProperties.class})
@SpringBootApplication
public class ApplicationManager {
    @Value("${backend.readOnlyAccessMode}")
    private boolean READ_ONLY_ACCESS_MODE;

    public static void main(String[] args) {
        Locale.setDefault(LOCALE_RU);

        SpringApplication application = new SpringApplication(ApplicationManagerConfig.class);
        application.run(ApplicationManager.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (READ_ONLY_ACCESS_MODE) {
            System.setProperty ("jsse.enableSNIExtension", "false");
        }
    }
}
