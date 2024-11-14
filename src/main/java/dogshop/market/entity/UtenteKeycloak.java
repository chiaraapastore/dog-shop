package dogshop.market.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.keycloak.representations.idm.UserRepresentation;

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

}
