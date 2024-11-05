package dogshop.market.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    public UtenteKeycloak(String username, String email, String password) {
        this.username = username;
        this.email = email;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(password);
        credential.setTemporary(false);
        this.setCredentials(Collections.singletonList(credential));
        this.setEnabled(true);
    }
}
