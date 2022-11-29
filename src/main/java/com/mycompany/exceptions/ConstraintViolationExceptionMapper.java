package com.mycompany.exceptions;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException e) {
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
        ErrorResponse errorResponse = new ErrorResponse(errorMessages);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
