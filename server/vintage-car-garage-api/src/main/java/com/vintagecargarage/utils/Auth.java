package com.vintagecargarage.utils;

import java.time.Duration;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.credential.TokenCredential;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Auth {

    public Boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }

    public String hashPassword(String password) {
        return BcryptUtil.bcryptHash(password);
    }

    public TokenCredential generateAuthToken(String userId, String role, int passwordVersion) {
        return new TokenCredential(Jwt.issuer("vintage-car-garage-api")
                .subject(userId)
                .groups(role)
                .expiresIn(Duration.ofHours(2))
                .claim("pw_ver", passwordVersion)
                .sign(), "jwt");
    }
}
