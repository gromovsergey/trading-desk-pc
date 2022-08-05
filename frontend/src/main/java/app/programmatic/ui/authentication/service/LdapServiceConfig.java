package app.programmatic.ui.authentication.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@EnableConfigurationProperties({ LdapServiceConfig.LdapSettings.class})
public class LdapServiceConfig {

    @Bean
    public LdapService ldapService(LdapSettings ldapSettings) {
        LdapServiceImpl result = new LdapServiceImpl();
        result.init(ldapSettings.getLdapBaseDn(), ldapSettings.getLdapDirectory(), ldapSettings.getLdapConnectionTimeout());
        return result;
    }

    @ConfigurationProperties("ldap")
    public static class LdapSettings {
        private String ldapBaseDn;
        private String ldapDirectory;
        private Long ldapConnectionTimeout;

        public String getLdapBaseDn() {
            return ldapBaseDn;
        }

        public void setLdapBaseDn(String ldapBaseDn) {
            this.ldapBaseDn = ldapBaseDn;
        }

        public String getLdapDirectory() {
            return ldapDirectory;
        }

        public void setLdapDirectory(String ldapDirectory) {
            this.ldapDirectory = ldapDirectory;
        }

        public Long getLdapConnectionTimeout() {
            return ldapConnectionTimeout;
        }

        public void setLdapConnectionTimeout(Long ldapConnectionTimeout) {
            this.ldapConnectionTimeout = ldapConnectionTimeout;
        }
    }
}
