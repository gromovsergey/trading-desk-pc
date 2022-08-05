package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.Foros;
import com.foros.rs.client.ForosCredentials;
import com.foros.rs.client.RsClientConfigurator;

import java.net.URI;


public class ForosBuilder {
    private RsClientConfigurator configurator;
    private URI hostUri;
    private Foros adminForos;

    public ForosBuilder(RsClientConfigurator configurator, URI hostUri, String adminToken, String adminKey) {
        this.configurator = configurator;
        this.hostUri = hostUri;
        this.adminForos = new Foros(configurator.configureMultiClient(
                () -> { return RsClientConfigurator.buildHttpContext(hostUri, adminToken, adminKey); }));
    }

    public Foros getAdminForos() {
        return adminForos;
    }

    public Foros getRestrictedForos(String userToken, String userKey) {
        return new Foros(configurator.configureMultiClient(
                () -> { return RsClientConfigurator.buildHttpContext(hostUri, userToken, userKey); }));
    }

    public Foros getRestrictedForos(ForosCredentials credentials) {
        return new Foros(configurator.configureMultiClient(
                () -> { return RsClientConfigurator.buildHttpContext(hostUri, credentials); }));
    }
}
