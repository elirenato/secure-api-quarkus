package com.mycompany.exceptions;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

@Provider
@Slf4j
public class TakeAllExceptionsMapper implements ExceptionMapper<Throwable> {
    @Context
    HttpServerRequest request;

    private Locale getLocaleFromRequest() {
        String language = request.getHeader("Accept-Language");
        if (StringUtils.isEmpty(language)) {
            return Locale.getDefault();
        } else {
            return new Locale(language);
        }
    }

    private Response mapHibernateConstraintException(org.hibernate.exception.ConstraintViolationException e) {
        String message = ResourceBundle.
                getBundle("messages", getLocaleFromRequest()).
                getString("constraint.error." + e.getConstraintName());
        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(message);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }

    @Override
    public Response toResponse(Throwable e) {
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
        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(defaultErrorMessage);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
