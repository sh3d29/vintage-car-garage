package com.vintagecargarage.errors.mapper;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;

import com.vintagecargarage.errors.ErrorResponse;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    private static final Logger LOG = Logger.getLogger(PersistenceExceptionMapper.class);

    @Override
    public Response toResponse(PersistenceException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException
                    || cause instanceof SQLIntegrityConstraintViolationException) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse(
                                "Eindeutigkeits-Constraint verletzt (z. B. Kennzeichen oder E-Mail bereits vergeben)"))
                        .build();
            }
        }
        LOG.error("Unerwarteter Persistenzfehler", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Interner Datenbankfehler"))
                .build();
    }
}
