package com.vintagecargarage.auth;

import java.io.IOException;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.json.internal.json_simple.JSONValue;

import com.vintagecargarage.entities.User;
import com.vintagecargarage.entities.UserRole;
import com.vintagecargarage.repository.CarOwnerRepository;
import com.vintagecargarage.repository.StorageOwnerRepository;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.NonNull;

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
        if (path.contains("login") || path.contains("register")) {
            return;
        }

        String userId = jwt.getSubject();
        String role = jwt.getGroups().stream().findFirst().get();
        Object pwVersion = jwt.getClaim("pw_ver");

        int tokenPwVersion = Integer.parseInt(pwVersion.toString());
        User user = null;

        if (role.equals(UserRole.STORAGE_OWNER)) {
            user = storageOwnerRepository.findById(userId);
        } else if (role.equals(UserRole.CAR_OWNER)) {
            user = carOwnerRepository.findById(userId);
        }

        if (tokenPwVersion < user.getPasswordVersion()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(JSONValue.toJSONString("Token expired due to password change."))
                    .build());
        }
    }
}
