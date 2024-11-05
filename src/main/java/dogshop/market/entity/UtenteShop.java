package dogshop.market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String keycloakId;

    @NotBlank(message = "Firstname is required")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;
    @NotBlank(message = "Address is required")
    private String address;
    @Size(min = 10, message = "NumberCell should be at least 10 characters")
    private String numberCell;
    private String role;

    @NotBlank(message = "Username is required")
    private String username;


    @OneToMany(mappedBy = "utenteShop", cascade = CascadeType.ALL)
    private List<CustomerOrder> orders;
}
