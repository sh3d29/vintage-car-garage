package com.vintagecargarage.auth;

import java.io.IOException;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.vintagecargarage.entities.User;
import com.vintagecargarage.errors.ErrorResponse;
import com.vintagecargarage.repositories.CarOwnerRepository;
import com.vintagecargarage.repositories.StorageOwnerRepository;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
@ApplicationScoped
public class PasswordVersionFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Inject
    CarOwnerRepository carOwnerRepository;

    @Inject
    StorageOwnerRepository storageOwnerRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("sign-in") || path.contains("sign-up")) {
            return;
        }

        String userId = jwt.getSubject();
        String role = jwt.getGroups().stream().findFirst().orElse(null);
        Object pwVersion = jwt.getClaim("pw_ver");

        int tokenPwVersion;
        try {
            tokenPwVersion = Integer.parseInt(String.valueOf(pwVersion));
        } catch (NumberFormatException e) {
            abortUnauthorized(requestContext, "Ungültiges Token");
            return;
        }

        User user = null;
        if (UserRole.STORAGE_OWNER.equals(role)) {
            user = storageOwnerRepository.findById(userId);
        } else if (UserRole.CAR_OWNER.equals(role)) {
            user = carOwnerRepository.findById(userId);
        }

        if (user == null) {
            abortUnauthorized(requestContext, "Benutzer nicht gefunden");
            return;
        }

        if (tokenPwVersion < user.getPasswordVersion()) {
            abortUnauthorized(requestContext, "Token abgelaufen (Passwort geändert)");
        }
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse(message))
                .type(MediaType.APPLICATION_JSON)
                .build());
    }
}
