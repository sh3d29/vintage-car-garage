package com.vintagecargarage.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class User extends BaseEntity {
    private String email;
    private String password;
    private int passwordVersion;

    protected User(String email, String password, int passwordVersion) {
        this.email = email;
        this.password = password;
        this.passwordVersion = passwordVersion;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.passwordVersion++;
    }
}
