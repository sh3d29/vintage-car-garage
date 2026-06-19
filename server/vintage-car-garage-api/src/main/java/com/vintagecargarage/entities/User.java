package com.vintagecargarage.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    protected String email;
    protected String password;
    protected int passwordVersion;

    public User(String email, String password, int passwordVersion) {
        this.email = email;
        this.password = password;
        this.passwordVersion = passwordVersion;
    }

    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.passwordVersion++;
    }
}
