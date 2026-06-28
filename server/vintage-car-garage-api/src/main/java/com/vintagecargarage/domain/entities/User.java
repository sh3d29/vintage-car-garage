package com.vintagecargarage.domain.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public void setPassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.passwordVersion++;
    }
}
