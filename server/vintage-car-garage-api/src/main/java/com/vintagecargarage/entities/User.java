package com.vintagecargarage.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * Abstrakte Oberklasse der Benutzerrollen CarOwner (Einlagerer) und StorageOwner
 * (Lagerhalter); Information Expert für die eigenen Zugangsdaten.
 */
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

    /** ändert die E-Mail (Anmeldename) */
    public void changeEmail(String email) {
        this.email = email;
    }

    /** setzt neues Passwort + erhöht passwordVersion (invalidiert alte Tokens) */
    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.passwordVersion++;
    }
}
