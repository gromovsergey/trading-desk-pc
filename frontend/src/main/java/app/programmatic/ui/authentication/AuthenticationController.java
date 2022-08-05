package app.programmatic.ui.authentication;

import app.programmatic.ui.authentication.model.AuthenticationException;
import app.programmatic.ui.authentication.model.Credentials;
import app.programmatic.ui.authentication.service.AuthenticationService;
import app.programmatic.ui.authentication.view.LoginData;
import app.programmatic.ui.authorization.service.UserActivityAuthorizationService;
import app.programmatic.ui.user.dao.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;


@RestController
public class AuthenticationController {

    @Autowired
    private UserActivityAuthorizationService authorizationService;

    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(method = RequestMethod.POST, path = "/rest/login", produces = "application/json")
    public Credentials login(@RequestBody LoginData loginData) {
        Credentials result = authenticationService.get(loginData.getLogin(),
                                                       loginData.getPassword(),
                                                       authorizationService.getAuthUserInfo().getIp());
        authorizationService.notifyUserActivity(result.getId());
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/logout", produces = "application/json")
    public void logout() {
        User user = authorizationService.getAuthUser();
        String key = DatatypeConverter.printBase64Binary(user.getUserCredential().getRsKey());
        String token = user.getUserCredential().getRsToken();
        authenticationService.changeRsCredentials(key, token, authorizationService.getAuthUserInfo().getIp());
    }

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }
}
