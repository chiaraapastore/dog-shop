package com.example.dogshop.entity;


import lombok.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;

import java.util.Collections;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UtenteKeycloak extends UserRepresentation {
    private String username;
    private String email;
    private String role;

    public UtenteKeycloak(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.role = role;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(password);
        credential.setTemporary(false);
        this.setCredentials(Collections.singletonList(credential));
        this.setEnabled(true);
    }
}
