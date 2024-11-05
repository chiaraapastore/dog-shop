package dogshop.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToOne
    @JoinColumn(name = "utente_id")
    private UtenteShop utenteShop;

    @OneToMany(mappedBy = "cartProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> cartProducts = new ArrayList<>();
}
