package dogshop.market.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private UtenteShop utenteShop;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    public UtenteShop getUtenteShop() {
        return utenteShop;
    }
    public void setUtenteShop(UtenteShop utenteShop) {
        this.utenteShop = utenteShop;
    }

}

