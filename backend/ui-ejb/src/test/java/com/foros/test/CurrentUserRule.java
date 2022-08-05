package com.foros.test;

import com.foros.model.security.User;
import com.foros.security.MockPrincipal;
import com.foros.security.SecurityContextMock;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityPrincipal;

import java.util.Locale;
import java.util.TimeZone;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class CurrentUserRule implements TestRule {

    private SecurityContextMock securityContextMock;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    CurrentUserSettingsHolder.set("127.0.0.1", TimeZone.getDefault(), Locale.UK);

                    SecurityPrincipal DEFAULT_ADMIN_PRINCIPAL = new MockPrincipal(
                            "test@ocslab.com",
                            1L,
                            1L,
                            2L,
                            0L
                    );

                    securityContextMock = SecurityContextMock.getInstance();
                    securityContextMock.setPrincipal(DEFAULT_ADMIN_PRINCIPAL);

                    base.evaluate();
                } finally {
                    SecurityContextMock.getInstance().tearDown();
                    CurrentUserSettingsHolder.clear();
                }
            }
        };
    }

    public void setPrincipal(User user) {
        securityContextMock.setPrincipal(user);
    }

    public void setPrincipal(ApplicationPrincipal principal) {
        securityContextMock.setPrincipal(principal);
    }

    public void setPrincipal(UserDefinition userDefinition) {
        User user = userDefinition.getUser();
        MockPrincipal principal = new MockPrincipal(
                user.getFullName(),
                user.getId(),
                user.getAccount().getId(),
                user.getRole().getId(),
                (long)user.getAccount().getRole().getId()
        );

        SecurityContextMock.getInstance().setPrincipal(principal);
    }

    public void setLocale(Locale locale) {
        CurrentUserSettingsHolder.setLocale(locale);
    }
}
