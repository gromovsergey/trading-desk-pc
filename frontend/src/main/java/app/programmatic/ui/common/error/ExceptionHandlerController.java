package app.programmatic.ui.common.error;

import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_FORBIDDEN_CODE;
import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_OPTIMISTIC_LOCK_CODE;

import com.foros.rs.client.RsException;
import com.foros.rs.client.RsNotAuthorizedException;
import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.common.validation.exception.ExpectedForosViolationsException;
import app.programmatic.ui.common.validation.JsonErrorsFormatter;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;


@ControllerAdvice
public class ExceptionHandlerController {
    private static final Logger logger = Logger.getLogger(ExceptionHandlerController.class.getName());

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public void handleException(HttpServletResponse response, Exception e) throws IOException {
        logger.log(Level.WARNING, "Unhandled error", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(value = {RsException.class})
    public void handleRsException(HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof RsNotAuthorizedException) {
            logger.log(Level.INFO, "Unauthorized RS Request");
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String error = "RS Request error";
        if (e instanceof RsConstraintViolationException) {
            StringBuilder msg = new StringBuilder(error + ". ConstraintViolations.\n");
            for(ConstraintViolation violation : ((RsConstraintViolationException) e).getConstraintViolations()){
                if (RS_API_OPTIMISTIC_LOCK_CODE.equals(violation.getCode())) {
                    handleOptimisticLock(response);
                    return;
                }
                if (RS_API_FORBIDDEN_CODE.equals(violation.getCode())) {
                    handleForbiddenException(response);
                    return;
                }

                msg.append(violation.getCode());
                msg.append(". Path: ");
                msg.append(violation.getPath());
                msg.append(". Value: ");
                msg.append(violation.getValue());
                msg.append(". Msg: ");
                msg.append(violation.getMessage());
                msg.append('\n');
            }

            error = msg.toString();
        }

        logger.log(Level.WARNING, error, e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class,
                               HttpRequestMethodNotSupportedException.class,
                               MethodArgumentTypeMismatchException.class})
    public void handleBadRequestExceptions(HttpServletResponse response, Exception e) throws IOException {
        String msg = e instanceof MissingServletRequestParameterException ? buildMessage((MissingServletRequestParameterException)e) :
                e.getMessage();
        logger.log(Level.INFO, msg);

        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public void handleConstraintViolationExceptions(HttpServletResponse response, ConstraintViolationException e) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildJsonOutput(e));
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    @ExceptionHandler(value = { ValidationException.class })
    public void handleValidationExceptions(HttpServletResponse response, ValidationException e) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildJsonOutput(e));
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    @ExceptionHandler(value = { StaleObjectStateException.class, ObjectOptimisticLockingFailureException.class })
    public void handleOptimisticLock(HttpServletResponse response, Exception e) throws IOException {
        handleOptimisticLock(response);
    }

    private void handleOptimisticLock(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildOptimisticLockJsonOutput());
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public void handleEntityNotFound(HttpServletResponse response, EntityNotFoundException e) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildEntityNotFoundJsonOutput(e.getId()));
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }

    private String buildMessage(MissingServletRequestParameterException e) {
        return String.format("Missing required param %s of type %s", e.getParameterName(), e.getParameterType());
    }

    @ExceptionHandler(value = { ForbiddenException.class })
    public void handleForbiddenException(HttpServletResponse response, ForbiddenException e) throws IOException {
        handleForbiddenException(response);
    }

    private void handleForbiddenException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(value = { ExpectedForosViolationsException.class })
    public void handleForbiddenException(HttpServletResponse response, ExpectedForosViolationsException e) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildJsonOutput(e));
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    // Intended for interception of Big File Upload exceptions, but currently not working because of auto-configuration
    // We need instantiate dispatcherServlet manually with custom multipart config
    @ExceptionHandler(value = { MultipartException.class })
    public void handleForbiddenException(HttpServletResponse response, MultipartException e) throws IOException {
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        response.getWriter().println(JsonErrorsFormatter.buildJsonOutput(e));
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    @ExceptionHandler(value = { HttpMessageNotReadableException.class })
    public void handleTypeConversionExceptions(HttpServletResponse response, HttpMessageNotReadableException e) throws IOException {
        String jsonErrorObject = null;
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException mappingException = (InvalidFormatException)(e.getCause());
            jsonErrorObject = JsonErrorsFormatter.buildJsonOutput(mappingException);
        } else if (e.getCause() instanceof JsonMappingException) {
            JsonMappingException mappingException = (JsonMappingException)(e.getCause());
            jsonErrorObject = JsonErrorsFormatter.buildJsonOutput(mappingException);
        }

        if (jsonErrorObject == null) {
            handleBadRequestExceptions(response, e);
            return;
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(jsonErrorObject);
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }

    @ExceptionHandler(value = { CannotGetJdbcConnectionException.class })
    public void handleValidationExceptions(HttpServletResponse response, CannotGetJdbcConnectionException e) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(JsonErrorsFormatter.buildServerIsBusyJsonOutput());
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
    }
}
