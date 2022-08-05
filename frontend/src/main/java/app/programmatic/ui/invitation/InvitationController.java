package app.programmatic.ui.invitation;

import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.email.service.EmailService;
import app.programmatic.ui.external_services.exception.ExternalServiceException;
import app.programmatic.ui.external_services.model.RecaptchaResponse;
import app.programmatic.ui.external_services.service.recaptcha.RecaptchaExternalService;
import app.programmatic.ui.invitation.dao.model.ContractRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@Validated
public class InvitationController {
    private static final Logger logger = Logger.getLogger(InvitationController.class.getName());
    private static final MessageInterpolator messageInterpolator = MessageInterpolator.getDefaultMessageInterpolator();

    private static final String INVITATION_SUBJ = "Заявка формы обратной связи new-programmatic.com";
    private static final String INVITATION_TEXT_TEMPL = "Обращаться к '%s' (email: '%s', тел. '%s')\n\n%s";

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private RecaptchaExternalService recaptchaExternalService;

    @Autowired
    private EmailService emailService;

    @Value("${invitaion.sentTo:info@new-programmatic.com}")
    private String sendTo;

    @Value("${invitaion.checkCaptcha:false}")
    private boolean checkCaptcha;

    @Value("#{'${mail.blacklistedSenders:}'.split(',')}")
    private Set<String> blacklistedSenders;


    @RequestMapping(method = RequestMethod.POST,
                    path = "/rest/invitation/contractRequest",
                    consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8" })
    public ResponseEntity<String> contractRequest(@RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "tel", required = false) String tel,
                                                  @RequestParam(value = "email", required = false) String email,
                                                  @RequestParam(value = "text", required = false) String text,
                                                  @RequestParam(value = "g-recaptcha-response", required = false) String reCaptcha) {
        ContractRequestData contractRequestData = new ContractRequestData(name, tel, email, text, reCaptcha);
        Set<ConstraintViolation<ContractRequestData>> violations = validator.validate(contractRequestData);
        if (!violations.isEmpty()) {
            ConstraintViolation<ContractRequestData> violation = violations.iterator().next();
            String message = violation.getPropertyPath() + ": " + violation.getMessage();
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return contractRequest(contractRequestData);
    }

    private ResponseEntity<String> contractRequest(@Valid ContractRequestData contractRequestData) {
        logger.info("Invitation requested: " + contractRequestData.toString());

        if (contractRequestData.getName() != null
                && blacklistedSenders.contains(contractRequestData.getName())) {
            logger.info("Invitation filtered by name: " + contractRequestData.getName());

        } else if (!checkCaptcha
                || isNotRobot(contractRequestData.getReCaptcha())) {
            sendEmail(contractRequestData);
        }

        return new ResponseEntity<>(messageInterpolator.interpolate("invitation.successSubmit"),
                                    HttpStatus.OK);
    }

    private boolean isNotRobot(String reCaptcha) {
        try {
            RecaptchaResponse recaptchaResponse = recaptchaExternalService.doRequest(reCaptcha);
            logger.info("RecaptchaResponse: " + recaptchaResponse);

            return recaptchaResponse.getSuccess();
        } catch (ExternalServiceException e) {
            logger.severe("Can't verify captcha: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't verify captcha: " + e.getMessage(), e);
        }

        return false;
    }

    private void sendEmail(ContractRequestData data) {
        emailService.sendAsync(sendTo,
                               INVITATION_SUBJ,
                               String.format(INVITATION_TEXT_TEMPL, data.getName(), data.getEmail(), data.getTel(), data.getText()));
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class, BindException.class })
    public void handleExceptions(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
