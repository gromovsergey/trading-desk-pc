package app.programmatic.ui.common.foros.service;

import app.programmatic.ui.authorization.service.AuthorizationServiceConfigurator;

import org.springframework.beans.factory.annotation.Autowired;

import java.net.Inet4Address;
import java.net.UnknownHostException;


public class TestCurUserTokenKeyService {

    @Autowired
    private ForosServiceConfigurator forosServiceConfigurator;

    @Autowired
    private AuthorizationServiceConfigurator authorizationServiceConfigurator;

    private final String key;
    private final String token;
    private final String ip;

    public TestCurUserTokenKeyService(String token, String key) {
        this.key = key;
        this.token = token;

        String localIp;
        try {
            localIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localIp = "127.0.0.1";

        }
        ip = localIp;
    }

    public void configureServicesForAdmin() {
        forosServiceConfigurator.configure(token, key);
        authorizationServiceConfigurator.configure(token, ip, 52560000);
    }

    public String getKey() {
        return key;
    }

    public String getToken() {
        return token;
    }
}
