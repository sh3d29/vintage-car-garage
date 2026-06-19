package com.vintagecargarage.utils;

import com.vintagecargarage.entities.CarOwner;
import com.vintagecargarage.entities.StorageOwner;
import com.vintagecargarage.entities.User;
import com.vintagecargarage.entities.UserRole;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.json.internal.json_simple.JSONValue;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION)
@ApplicationScoped
public class PasswordVersionFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("login") || path.contains("register")) {
            return;
        }

        String userId = jwt.getSubject();
        String role = jwt.getGroups().stream().findFirst().get();
        Object claim = jwt.getClaim("pw_ver");

        if (claim == null || userId == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        int tokenPwVer = Integer.parseInt(claim.toString());
        User user = null;

        if (role.equals(UserRole.STORAGE_OWNER)) {
            user = StorageOwner.findById(userId);
        } else if (role.equals(UserRole.CAR_OWNER)) {
            user = CarOwner.findById(userId);
        }

        if (user == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        if (tokenPwVer < user.getPasswordVersion()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(JSONValue.toJSONString("Token expired due to password change."))
                    .build());
        }
    }
}