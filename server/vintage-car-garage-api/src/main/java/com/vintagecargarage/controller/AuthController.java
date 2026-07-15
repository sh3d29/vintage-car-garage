package com.vintagecargarage.controller;

import java.util.Optional;

import com.vintagecargarage.auth.AuthResult;
import com.vintagecargarage.auth.UserRole;
import com.vintagecargarage.dtos.auth.AuthUserDto;
import com.vintagecargarage.dtos.auth.SignInDto;
import com.vintagecargarage.errors.ErrorResponse;
import com.vintagecargarage.services.AuthService;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

/**
 * Systemgrenze für die Authentifizierung unter /auth: Anmeldung
 * (/sign-in), aktueller Benutzer (/me), Abmeldung (/sign-out). Token als
 * HttpOnly-Cookie
 */
@Path("/auth")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    @PermitAll
    @POST
    @Path("/sign-in")
    public Response auth(SignInDto signInDto) {
        Optional<AuthResult> result = authService.authenticate(signInDto);

        if (result.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Ungültige E-Mail oder ungültiges Passwort")).build();
        }

        NewCookie cookie = new NewCookie.Builder("auth_token")
                .value(result.get().token().getToken())
                .path("/")
                .maxAge(3600)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        return Response.ok(result.get().user()).cookie(cookie).build();
    }

    @RolesAllowed({ UserRole.CAR_OWNER, UserRole.STORAGE_OWNER })
    @GET
    @Path("/me")
    public Response me() {
        String userId = jwt.getSubject();
        String role = jwt.getGroups().stream().findFirst().orElse(null);

        Optional<AuthUserDto> user = authService.getAuthenticatedUser(userId, role);
        return user.map(u -> Response.ok(u).build())
                .orElseGet(() -> Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Nicht authentifiziert")).build());
    }

    @RolesAllowed({ UserRole.CAR_OWNER, UserRole.STORAGE_OWNER })
    @POST
    @Path("/sign-out")
    public Response signOut() {
        NewCookie cookie = new NewCookie.Builder("auth_token")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        return Response.noContent().cookie(cookie).build();
    }
}
