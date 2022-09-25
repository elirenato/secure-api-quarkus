package com.mycompany.exceptions;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionMappers {

    @Context
    HttpServerRequest request;

    @ServerExceptionMapper
    Response mapValidationConstraintException(javax.validation.ConstraintViolationException e) {
        List<ErrorResponse.ErrorMessage> errorMessages = e.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    String entityName = constraintViolation.getLeafBean().getClass().getSimpleName().toLowerCase();
                    String[] path = constraintViolation.getPropertyPath().toString().split("\\.");
                    String fieldName = path[path.length - 1];
                    return new ErrorResponse.ErrorMessage(
                            entityName + "." + fieldName,
                            constraintViolation.getMessage()
                    );
                })
                .collect(Collectors.toList());
        return buildErrorResponse(Response.Status.BAD_REQUEST, errorMessages);
    }

    @ServerExceptionMapper
    public Response mapWebApplication(WebApplicationException e) {
        return e.getResponse();
    }

    private Response mapHibernateConstraintException(org.hibernate.exception.ConstraintViolationException e) {
        String message = ResourceBundle.
                getBundle("messages", getLocaleFromRequest()).
                getString("constraint.error." + e.getConstraintName());
        return buildErrorResponse(Response.Status.BAD_REQUEST, message);
    }

    @ServerExceptionMapper
    Response mapGeneric(Throwable e) {
        // The while loop below is needed because when using a generic handler like mapGeneric(Throwable e) method,
        // it catches the exception first due to another error (io.quarkus.arc.ArcUndeclaredThrowableException: Error invoking subclass method)
        // that encapsulate the org.hibernate.exception.ConstraintViolationException.
        // The problem is similar to described here https://github.com/quarkusio/quarkus/issues/14281.
        Throwable cause = e;
        do {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
                return mapHibernateConstraintException((org.hibernate.exception.ConstraintViolationException) cause);
            }
            cause = cause.getCause();
        } while (cause != null);

        String errorId = UUID.randomUUID().toString();
        log.error("errorId[{}]", errorId, e);
        String defaultErrorMessage = ResourceBundle.getBundle("messages", getLocaleFromRequest()).getString("system.error");
        return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, defaultErrorMessage);
    }

    private Locale getLocaleFromRequest() {
        String language = request.getHeader("Accept-Language");
        if (StringUtils.isEmpty(language)) {
            return Locale.getDefault();
        } else {
            return new Locale(language);
        }
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(message);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return Response.status(status).entity(errorResponse).build();
    }

    private Response buildErrorResponse(
            Response.Status status,
            List<ErrorResponse.ErrorMessage> errorMessages
    ) {
        ErrorResponse errorResponse = new ErrorResponse(errorMessages);
        return Response.status(status).entity(errorResponse).build();
    }
}
