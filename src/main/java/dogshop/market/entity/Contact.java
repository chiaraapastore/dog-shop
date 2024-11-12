package dogshop.market.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Contact {
    @Id
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String message;

}
