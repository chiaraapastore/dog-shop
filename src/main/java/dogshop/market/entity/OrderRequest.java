package dogshop.market.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequest {

    private Long utenteShopId;
    private List<Product> products;

    public Long getUtenteShopId() {
        return utenteShopId;
    }

}
