package dogshop.market.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate paymentDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Status is required")
    private String status;

}
