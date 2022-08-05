package app.programmatic.ui.user.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.common.aspect.prePersistProcessor.PrePersistProcessorContext;
import app.programmatic.ui.common.config.TestConfig;
import app.programmatic.ui.common.foros.service.TestCurUserTokenKeyService;
import app.programmatic.ui.common.testtools.TestEnvironment;
import app.programmatic.ui.common.testtools.TestEnvironmentVariables;
import app.programmatic.ui.common.tool.javabean.emptyValues.ConfigurableEmptyValuesStrategy;
import app.programmatic.ui.common.tool.javabean.emptyValues.PreviousValueEmptyValuesStrategy;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserCredential;
import app.programmatic.ui.user.dao.model.UserOpts;
import app.programmatic.ui.user.dao.model.UserRole;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import app.programmatic.ui.user.dao.model.UserRoleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE, classes = {TestConfig.class})
public class UserServiceIntegrationTest extends Assert {
    private static final String PASSWORD = "5cKehaXU";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private TestCurUserTokenKeyService curUserTokenKeyService;

    @Autowired
    private SearchAccountService searchAccountService;

    @Autowired
    private PrePersistProcessorContext entityProcessorContext;

    private TestEnvironmentVariables vars;

    @Before
    public void initialize() {
        vars = TestEnvironment.initialize(curUserTokenKeyService, searchAccountService);
    }


    @Test
    public void testUpdate() {
        int expectedRolesCount = 4;
        UserRole[] adminRole = new UserRole[1];
        ArrayList<UserRole> userRoles = new ArrayList<>(expectedRolesCount - 1);
        userRoleService.getAvailableForCreateRoles(AccountRole.AGENCY)
                .forEach( ur -> {
                    if (ur.getName().equals(UserRoleType.ADMIN.getName())) {
                        adminRole[0] = ur;
                    } else {
                        userRoles.add(ur);
                    }
                });
        assertEquals(expectedRolesCount - 1, userRoles.size());
        assertNotNull(adminRole[0]);

        // Expected that first role = admin role
        User newUser = createUser(adminRole[0]);
        newUser.setAdvertiserIds(Collections.emptyList());
        Long newUserId = userService.create(newUser);
        assertNotNull(newUserId);


        userRoles.forEach( ur -> {
            User user = userService.find(newUserId);
            user.setUserRole(ur);

            user.setAdvertiserIds(Collections.singletonList(vars.getAccountId()));
            user.setFlagsSet(EnumSet.of(UserOpts.ADVERTISER_LEVEL_ACCESS));
            entityProcessorContext.setEmptyValuesStrategy(PreviousValueEmptyValuesStrategy.getInstance());
            userService.update(user);
            user = userService.find(newUserId);
            assertEquals(1, user.getAdvertiserIds().size());

            user.setAdvertiserIds(Collections.emptyList());
            user.setFlagsSet(EnumSet.noneOf(UserOpts.class));
            entityProcessorContext.setEmptyValuesStrategy(
                    new ConfigurableEmptyValuesStrategy(Collections.singleton("advertiserIds")));
            userService.update(user);
            user = userService.find(newUserId);
            assertTrue(user.getAdvertiserIds().isEmpty());
        });

        User user = userService.find(newUserId);
        user.setUserRole(adminRole[0]);
        userService.update(user);
    }

    @Test
    public void testCaseInsensitiveLogin() {
        try {
            User user1 = userService.findUserByEmailUnrestricted("test@ocslab.com");
            assertNotNull(user1);

            User user2 = userService.findUserByEmailUnrestricted("Test@ocslab.com");
            assertNotNull(user2);

            assertEquals("The same user is expected", user1.getId(), user2.getId());
        } catch (UserRetrievalException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMultiLoginEmail() {
        String multiLoginEmail = "url_length@ocslab.com";
        try {
            userService.findUserByEmailUnrestricted(multiLoginEmail);
            assertTrue("Multi-login exception is expected for email " + multiLoginEmail, false);
        } catch (UserRetrievalException e) {
            assertTrue("Multi-login exception is expected for email "+ multiLoginEmail,
                      e.getType() == UserRetrievalException.Type.MULTI_LOGIN);
        }
    }

    @Test
    public void testDeleted() {
        String deleted = "sergey_gromov+117@ocslab.com";
        try {
            userService.findUserByEmailUnrestricted(deleted);
            assertTrue("Deleted user exception is expected for email " + deleted, false);
        } catch (UserRetrievalException e) {
            assertTrue("Deleted exception is expected for email "+ deleted,
                      e.getType() == UserRetrievalException.Type.DELETED);
        }
    }

    @Test
    public void testInactive() {
        String inactive = "sergey_gromov+118@ocslab.com";
        try {
            userService.findUserByEmailUnrestricted(inactive);
            assertTrue("Inactive user exception is expected for email " + inactive, false);
        } catch (UserRetrievalException e) {
            assertTrue("Inactive exception is expected for email "+ inactive,
                    e.getType() == UserRetrievalException.Type.INACTIVE);
        }
    }

    private User createUser(UserRole userRole) {
        User newUser = new User();

        newUser.setUserCredential(new UserCredential());
        newUser.getUserCredential().setPassword(PasswordHelper.encryptPassword(PASSWORD));

        newUser.setAccountId(vars.getAgencyId());
        newUser.setStatus('A');
        newUser.setFirstName("UserServiceIntegrationTest.User");
        newUser.setLastName(vars.getTimestamp().toString());
        newUser.setEmail("sergey_gromov+" + newUser.getFirstName() + newUser.getLastName() + "@ocslab.com");
        newUser.getUserCredential().setEmail(newUser.getEmail());
        newUser.setUserRole(userRole);

        return newUser;
    }
}
