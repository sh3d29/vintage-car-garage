package com.vintagecargarage.errors.mapper;

import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import com.vintagecargarage.errors.ErrorResponse;
import com.vintagecargarage.errors.exceptions.NotFoundException;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
