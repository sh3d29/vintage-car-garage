package com.vintagecargarage.errors.mapper;

import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import com.vintagecargarage.errors.ErrorResponse;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
