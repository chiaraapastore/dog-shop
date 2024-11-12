package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UtenteShopRepository utenteShopRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Payment savePayment(Payment pagamento) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) throw new RuntimeException("Utente non trovato");

        CustomerOrder ordine = new CustomerOrder();
        ordine.setUtenteShop(utenteShop);
        ordine.setOrderDate(LocalDate.now());
        ordine.setStatus("PENDING");
        ordine.setTotalAmount(calculateTotalAmount(utenteShop));

        String orderNumber = generateOrderNumber();
        ordine.setOrderNumber(orderNumber);

        CustomerOrder ordineSalvato = customerOrderRepository.save(ordine);

        Cart carrello = cartRepository.findCartWithProductsByUtenteShop(utenteShop);
        if (carrello == null) throw new RuntimeException("Carrello vuoto");

        List<CartProduct> prodottiCarrello = cartProductRepository.findByCart(carrello);
        List<OrderDetail> dettagliOrdine = new ArrayList<>();

        for (CartProduct cartProduct : prodottiCarrello) {
            OrderDetail dettaglioOrdine = new OrderDetail();
            dettaglioOrdine.setId(new OrderDetailId(ordineSalvato.getId(), cartProduct.getProduct().getId()));
            dettaglioOrdine.setCustomerOrder(ordineSalvato);
            dettaglioOrdine.setProduct(cartProduct.getProduct());
            dettaglioOrdine.setPaymentDate(LocalDate.now());
            dettagliOrdine.add(dettaglioOrdine);

            Product prodotto = cartProduct.getProduct();
            int nuovaQuantitaDisponibile = prodotto.getAvailableQuantity() - cartProduct.getQuantity();
            if (nuovaQuantitaDisponibile < 0) {
                throw new RuntimeException("Quantità del prodotto insufficiente per l'ordine: " + prodotto.getProductName());
            }
            prodotto.setAvailableQuantity(nuovaQuantitaDisponibile);
            productRepository.save(prodotto);

            Category categoria = prodotto.getCategory();
            categoria.setCountProduct(categoria.getCountProduct() - cartProduct.getQuantity());
            categoryRepository.save(categoria);
        }

        pagamento.setPaymentDate(LocalDate.now());
        pagamento.setStatus("SUCCESS");
        Payment pagamentoSalvato = paymentRepository.save(pagamento);
        if (pagamentoSalvato == null) throw new RuntimeException("Errore nel salvataggio del pagamento");

        ordineSalvato.setPayment(pagamentoSalvato);
        customerOrderRepository.save(ordineSalvato);

        orderDetailRepository.saveAll(dettagliOrdine);

        // elimino i prodotti dal carrello e il carrello
        cartProductRepository.deleteAll(prodottiCarrello);
        cartRepository.delete(carrello);

        return pagamentoSalvato;
    }


    private double calculateTotalAmount(UtenteShop utenteShop) {
        Cart cart = cartRepository.findCartWithProductsByUtenteShop(utenteShop);
        List<CartProduct> cartProducts = cartProductRepository.findByCart(cart);
        return cartProducts.stream()
                .mapToDouble(cartProduct -> cartProduct.getProduct().getPrice() * cartProduct.getQuantity())
                .sum();
    }

    private String generateOrderNumber() {
        Long lastOrderId = customerOrderRepository.findMaxOrderId();
        int newOrderId = lastOrderId != null ? lastOrderId.intValue() + 1 : 10000;
        return "#" + String.format("%05d", newOrderId);
    }
}