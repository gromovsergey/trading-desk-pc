package com.foros.security.principal;

import com.foros.model.security.AuthenticationToken;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;

public abstract class ApplicationPrincipalFactory {

    private ApplicationPrincipalFactory() {
    }

    public static ApplicationPrincipal createPrincipalByUser(String token, User user, String remoteAddress) {
        UserRole role = user.getRole();
        AccountRole accountRole = role.getAccountRole();

        return new SecurityPrincipal(
                token,
                user.getEmail(),
                user.getId(),
                user.getUserCredential().getId(),
                user.getAccount().getId(),
                role.getId(),
                (long) accountRole.getId(),
                remoteAddress
        );
    }

    public static ApplicationPrincipal createPrincipalByToken(AuthenticationToken token, String remoteAddress) {
        return createPrincipalByUser(token.getToken(), token.getUser(), remoteAddress);
    }

    public static ApplicationPrincipal createAnonymousPrincipal(String remoteAddress) {
        return new AnonymousPrincipal(remoteAddress);
    }
}
