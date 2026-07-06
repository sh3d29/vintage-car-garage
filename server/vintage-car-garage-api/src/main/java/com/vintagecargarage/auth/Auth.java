package com.vintagecargarage.auth;

import java.security.PrivateKey;
import java.time.Duration;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.credential.TokenCredential;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.util.KeyUtils;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Auth {

    private static final String PRIVATE_KEY = "/privateKey.pem";

    public Boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }

    public String hashPassword(String password) {
        return BcryptUtil.bcryptHash(password);
    }

    public TokenCredential generateAuthToken(String userId, String role, int passwordVersion) {
        try {
            PrivateKey privateKey = KeyUtils.readPrivateKey(PRIVATE_KEY);
            return new TokenCredential(Jwt.issuer("vintage-car-garage-api")
                    .subject(userId)
                    .groups(role)
                    .expiresIn(Duration.ofHours(2))
                    .claim("pw_ver", passwordVersion)
                    .sign(privateKey), "jwt");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load signing key", e);
        }
    }
}
