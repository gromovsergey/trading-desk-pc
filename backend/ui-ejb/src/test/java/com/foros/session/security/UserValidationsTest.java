package com.foros.session.security;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.Account;
import com.foros.model.security.User;
import com.foros.model.security.UserCredential;
import com.foros.security.AuthenticationType;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.UserTestFactory;
import com.foros.util.NameValuePair;
import com.foros.util.RandomUtil;

import group.Db;
import group.Validation;
import java.util.LinkedList;
import java.util.List;
import javax.naming.directory.BasicAttributes;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.easymock.classextension.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class UserValidationsTest extends AbstractValidationsTest {

    @Rule
    public EasyMockRule mockRule = new EasyMockRule(this);

    @TestSubject
    @Autowired
    private UserValidations userValidations;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private UserCredentialServiceBean userCredentialService;

    @Mock
    private LdapService ldapService;

    @Test
    public void testChangePasswordWithWrongOldOne() {
        String oldPassword = "cool pwd";
        String newPassword = "Even cooler pwd!";
        User user = userTF.createPersistentWithPassword(oldPassword);
        validate("User.changePassword", user.getId(), newPassword, newPassword, newPassword);
        assertHasViolation("oldPassword");
    }

    @Test
    public void testChangePasswordWithTooShortNewOne() {
        invokeTestWithIncorrectNewPassword("");
    }

    @Test
    public void testChangePasswordWithTooLongNewOne() {
        invokeTestWithIncorrectNewPassword(RandomUtil.getRandomString(201, RandomUtil.Alphabet.LETTERS));
    }

    private void invokeTestWithIncorrectNewPassword(String newPassword) {
        String oldPassword = "cool pwd";
        User user = userTF.createPersistentWithPassword(oldPassword);
        validate("User.changePassword", user.getId(), oldPassword, newPassword, newPassword);
        assertHasViolation("newPassword");
    }

    @Test
    public void testChangeForgotPasswordWithTooShortNewOne() {
        invokeTestWithIncorrectForgotNewPassword("");
    }

    @Test
    public void testChangeForgotPasswordWithTooLongNewOne() {
        invokeTestWithIncorrectForgotNewPassword(RandomUtil.getRandomString(201, RandomUtil.Alphabet.LETTERS));
    }

    @Test
    public void testEmailCaptcha() {
        validate("User.emailCaptcha", "captcha", "wrongcaptcha", "wrongEmail");
        assertHasViolation("captcha");
        assertHasViolation("email");
    }

    private void invokeTestWithIncorrectForgotNewPassword(String newPassword) {
        String oldPassword = "cool pwd";
        User user = userTF.createPersistentWithPassword(oldPassword);
        UserCredential existingUserCredential = userCredentialService.findByEmail(user.getEmail());
        validate("User.forgotPasswordChange", existingUserCredential.getId(), "dummyValue", newPassword, newPassword);
        assertHasViolation("password");
    }

    @Test
    public void testChangePasswordForLDAPUser() {
        User user = userTF.create();
        user.setDn("testDN1");
        userTF.persist(user);
        validate("User.changePassword", user.getId(), "old", "new", "new");
        assertViolationsCount(1);
    }

    @Test
    public void testUpdateUserWithNotExistingLdapDn() {
        User user = userTF.create();
        user.setAuthType(AuthenticationType.LDAP);
        user.setDn("testDN1");


        userTF.persist(user);
        user.setDn("testDN2");

        EasyMock.expect(ldapService.getAttrsByDn("testDN2")).andReturn(null);
        EasyMock.replay(ldapService);

        validate("User.update", user);
        assertHasViolation("dn");
    }

    @Test
    public void testUpdateUserWithWrongLdapDn() {
        User user = userTF.create();
        user.setAuthType(AuthenticationType.LDAP);
        user.setDn("testDN1");

        userTF.persist(user);
        user.setDn("testDN3");

        EasyMock.expect(ldapService.getAttrsByDn("testDN3")).andReturn(new BasicAttributes());
        EasyMock.expect(ldapService.findDnsForRole(user.getRole())).andReturn(getDns());
        EasyMock.replay(ldapService);

        validate("User.update", user);
        assertHasViolation("dn");
    }

    @Test
    public void testUpdateUserWithCorrectLdapDn() {
        User user = userTF.create();
        user.setAuthType(AuthenticationType.LDAP);
        user.setDn("testDN1");
        userTF.persist(user);

        user.setDn("testDN2");

        EasyMock.expect(ldapService.getAttrsByDn("testDN2")).andReturn(new BasicAttributes());
        EasyMock.expect(ldapService.findDnsForRole(user.getRole())).andReturn(getDns());
        EasyMock.replay(ldapService);

        validate("User.update", user);
        assertHasNoViolation("dn");
    }

    private List<NameValuePair<String, String>> getDns() {
        LinkedList<NameValuePair<String, String>> dns = new LinkedList<>();
        dns.add(new NameValuePair<>("aaa", "testDN1"));
        dns.add(new NameValuePair<>("bbb", "testDN2"));

        return dns;
    }

    @Test
    public void testCreateInternalUserUsingAnotherUserEmail() {
        Account externalAccount = publisherAccountTF.createPersistent();
        User externalUser = userTF.createPersistent(externalAccount);

        Account internalAccount = internalAccountTF.createPersistent();
        User internalUser = userTF.create(internalAccount);
        internalUser.setEmail(externalUser.getEmail());

        checkCreateEmailConstraintViolated(internalUser);
    }

    @Test
    public void testUpdateInternalUserUsingAnotherUserEmail() {
        Account externalAccount = publisherAccountTF.createPersistent();
        User externalUser = userTF.createPersistent(externalAccount);

        Account internalAccount = internalAccountTF.createPersistent();
        User internalUser = userTF.createPersistent(internalAccount);

        internalUser = userTF.copy(internalUser);
        internalUser.setEmail(externalUser.getEmail());

        checkUpdateEmailConstraintViolated(internalUser);
    }

    @Test
    public void testCreateExternalUserUsingInternalUserEmail() {
        Account internalAccount = internalAccountTF.createPersistent();
        User internalUser = userTF.createPersistent(internalAccount);

        Account externalAccount = publisherAccountTF.createPersistent();
        User externalUser = userTF.create(externalAccount);
        externalUser.setEmail(internalUser.getEmail());

        checkCreateEmailConstraintViolated(externalUser);
    }

    @Test
    public void testUpdateExternalUserUsingAnotherUserEmail() {

        Account account1 = publisherAccountTF.createPersistent();
        User user1 = userTF.createPersistent(account1);

        Account account2 = publisherAccountTF.createPersistent();
        User user2 = userTF.createPersistent(account2);

        user2 = userTF.copy(user2);
        user2.setEmail(user1.getEmail());

        checkUpdateEmailConstraintViolated(user2);
    }

    @Test
    public void testCreateExternalUserUsingSameAccountUserEmail() {

        Account account = publisherAccountTF.createPersistent();
        User user1 = userTF.createPersistent(account);

        User user2 = userTF.create(account);
        user2.setEmail(user1.getEmail());

        checkCreateEmailConstraintViolated(user2);
    }

    @Test
    public void testUpdateMyPreferencesUsingAnotherUserEmail() {

        Account myAccount = publisherAccountTF.createPersistent();
        User myUser = userTF.createPersistent(myAccount);

        Account anotherAccount = publisherAccountTF.createPersistent();
        User anotherUser = userTF.createPersistent(anotherAccount);

        myUser = userTF.copy(myUser);
        myUser.setEmail(anotherUser.getEmail());

        validate("User.updateMyPreferences", anotherUser);
        assertHasNoViolation("email");
    }

    @Test
    public void testCreateExternalUserUsingAnotherExternalAccountUserEmail() {

    }


    private void checkCreateEmailConstraintViolated(User updatedUser) {
        validate("User.create", updatedUser);
        assertHasViolation("email");
    }

    private void checkUpdateEmailConstraintViolated(User updatedUser) {
        validate("User.update", updatedUser);
        assertHasViolation("email");
    }
}
