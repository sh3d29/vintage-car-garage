package com.vintagecargarage.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    @Override
    public Response toResponse(PersistenceException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException
                    || cause instanceof SQLIntegrityConstraintViolationException) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Eindeutigkeits-Constraint verletzt (z. B. Kennzeichen oder E-Mail bereits vergeben)")
                        .build();
            }
        }
        return Response.serverError().build();
    }
}
