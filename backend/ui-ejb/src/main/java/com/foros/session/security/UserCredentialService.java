package com.foros.session.security;

import com.foros.model.security.UserCredential;

import javax.ejb.Local;

@Local
public interface UserCredentialService {

    Long create(UserCredential user);

    UserCredential findByEmail(String email);

    UserCredential writeLockByEmail(String email);

    UserCredential findByEmailAndPassword(String email, String password);

    UserCredential findByToken(String token);

    void delete(Long id);

    UserCredential find(Long id);

    void changeRsCredentials(Long userId);

    void authenticationFailed(Long credentialId, String remoteAddress);

    void authenticationSuccess(Long credentialId, String remoteAddress);

}
