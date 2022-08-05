package com.foros.rs.provider;

import com.foros.restriction.AccessRestrictedException;
import com.foros.session.BusinessException;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.code.InputErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import com.sun.jersey.api.NotFoundException;
import java.security.AccessControlException;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.ParamException;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof AccessRestrictedException) {
            AccessRestrictedException e = (AccessRestrictedException) exception;
            return toResponse(Response.Status.FORBIDDEN, ConstraintViolationsBean.create(e.getConstraintViolations()));
        } else if (exception instanceof AccessControlException) {
            return toResponse(Response.Status.FORBIDDEN, new ExceptionBean(exception));
        } else if (exception instanceof NotFoundException) {
            return toResponse(Response.Status.NOT_FOUND);
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) exception;
            return toResponse(Response.Status.PRECONDITION_FAILED, ConstraintViolationsBean.create(e.getConstraintViolations()));
        } else if (exception instanceof BusinessException) {
            return toResponse(Response.Status.PRECONDITION_FAILED, ConstraintViolationsBean.create(BusinessErrors.GENERAL_ERROR, exception.getMessage()));
        } else if (exception instanceof ParamException) {
            ParamException pe = (ParamException) exception;
            ConstraintViolationsBean cvb = ConstraintViolationsBean.create(InputErrors.PARAMETER_PARSE_ERROR, pe.getMessage(), pe.getParameterName());
            return toResponse(Response.Status.PRECONDITION_FAILED, cvb);
        } else if (exception instanceof WebApplicationException) {
            Response response = ((WebApplicationException) exception).getResponse();
            if (Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode() == response.getStatus()) {
                return toResponse(Status.UNSUPPORTED_MEDIA_TYPE,
                    ConstraintViolationsBean.create(InputErrors.PARAMETER_PARSE_ERROR, Status.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase()));
            } else {
                return Response
                    .fromResponse(response)
                    .entity(new ExceptionBean(exception))
                    .build();
            }
        } else if (exception instanceof EntityNotFoundException) {
            return toResponse(Response.Status.PRECONDITION_FAILED, ConstraintViolationsBean.create(BusinessErrors.ENTITY_NOT_FOUND, exception.getMessage()));
        }

        return Response
                .serverError()
                .entity(new ExceptionBean(exception))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML)
                .build();
    }

    private Response toResponse(Response.Status status, Object entity) {
        return Response
                .status(status)
                .entity(entity)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML)
                .build();
    }

    private Response toResponse(Response.Status status) {
        return Response
                .status(status)
                .entity("")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML)
                .build();
    }
}
