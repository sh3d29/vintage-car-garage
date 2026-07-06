package com.vintagecargarage.errors.mapper;

import com.vintagecargarage.errors.ErrorResponse;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        Response.StatusType status = original.getStatusInfo();

        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = status.getReasonPhrase();
        }

        return Response.status(status.getStatusCode())
                .entity(new ErrorResponse(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
