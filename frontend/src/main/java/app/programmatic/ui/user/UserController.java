package app.programmatic.ui.user;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.common.aspect.prePersistProcessor.PrePersistProcessorContext;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.tool.javabean.emptyValues.ConfigurableEmptyValuesStrategy;
import app.programmatic.ui.common.tool.javabean.emptyValues.PreviousValueEmptyValuesStrategy;
import app.programmatic.ui.common.tool.password.PasswordHelper;
import app.programmatic.ui.email.service.EmailService;
import app.programmatic.ui.user.dao.model.PasswordChangeData;
import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserOpts;
import app.programmatic.ui.user.dao.model.UserRole;
import app.programmatic.ui.user.service.UserRoleService;
import app.programmatic.ui.user.service.UserService;
import app.programmatic.ui.user.view.UserRoleView;
import app.programmatic.ui.user.view.UserView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.programmatic.ui.common.permission.dao.model.PermissionType.ADVERTISING_ACCOUNT;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PrePersistProcessorContext entityProcessorContext;

    @Value("${web.angularBaseUrl}")
    private String baseUrl;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/user/role", produces = "application/json")
    public List<UserRoleView> findUserRole(@RequestParam(value = "accountId", required = true) Long accountId) {
        AccountRole accountRole = accountService.findAdvertising(accountId).getRole();
        ArrayList<UserRoleView> result = new ArrayList<>();
        userRoleService.getAvailableForCreateRoles(accountRole).forEach(userRole -> {
            boolean advLevelAccessAvailable = !userRoleService.hasPermission(userRole.getId(), ADVERTISING_ACCOUNT, EDIT);
            result.add(new UserRoleView(userRole, advLevelAccessAvailable));
        });
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/user/list", produces = "application/json")
    public List<UserView> findUsers(@RequestParam(value = "accountId") Long accountId) {
        List<AdvertisingAccount> advertisers = accountService.findAdvertisersByAgency(accountId);
        AccountRole accountRole = accountService.findAdvertising(accountId).getRole();
        return userService.findUsersByAccountId(accountId, accountRole).stream()
                .map( user -> UserControllerHelper.toView(user, advertisers) )
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/user", produces = "application/json")
    public UserView findUser(@RequestParam(value = "userId", required = false) Long userId) {
        return UserControllerHelper.toView(userService.find(userId));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/user/profile", produces = "application/json")
    public UserView findCurrentUser() {
        return UserControllerHelper.toView(authorizationService.getAuthUser());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/user", produces = "application/json")
    @Transactional
    public Long createUser(@RequestBody UserView userView) {
        String password = PasswordHelper.generatePassword();
        UserRole userRole = userView.getRoleId() != null ? userRoleService.findForCreate(userView.getRoleId()) : null;

        User user = UserControllerHelper.createUserFromView(userView, userRole, password);
        if (user.getAdvertiserIds().size() > 0) {
            user.setFlagsSet(EnumSet.of(UserOpts.ADVERTISER_LEVEL_ACCESS));
        }

        Long result = userService.create(user);
        UserControllerHelper.sendPasswordInMail(emailService, userView, password, baseUrl);

        return result;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/user", produces = "application/json")
    public void updateUser(@RequestBody UserView userView) {
        UserRole userRole = userView.getRoleId() != null ? userRoleService.findForCreate(userView.getRoleId()) : null;
        User user = UserControllerHelper.updateUserFromView(userView, userRole);

        if (user.getAdvertiserIds().size() > 0) {
            user.setFlagsSet(EnumSet.of(UserOpts.ADVERTISER_LEVEL_ACCESS));
            entityProcessorContext.setEmptyValuesStrategy(PreviousValueEmptyValuesStrategy.getInstance());
        } else {
            entityProcessorContext.setEmptyValuesStrategy(
                    new ConfigurableEmptyValuesStrategy(Collections.singleton("advertiserIds")));
        }

        userService.update(user);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/user/operation", produces = "application/json")
    public Object doUserOperation(@RequestParam(value = "name") StatusOperation operation,
                                  @RequestParam(value = "userId") Long userId) {
        return userService.changeStatus(userId, operation);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/user/password", produces = "application/json")
    public void updateUserPassword(@RequestBody PasswordChangeData passwordChangeData) {
        userService.updateMyPassword(passwordChangeData);
    }
}
