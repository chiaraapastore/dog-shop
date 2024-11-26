package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CartProductRepository cartProductRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;
    private final CartRepository cartRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            CustomerOrderRepository customerOrderRepository,
            CartProductRepository cartProductRepository,
            AuthenticationService authenticationService,
            UtenteShopRepository utenteShopRepository,
            CartRepository cartRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.paymentRepository = paymentRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.cartProductRepository = cartProductRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.cartRepository = cartRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Payment savePayment(Payment pagamento) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new RuntimeException("Utente non trovato");
        }
        CustomerOrder ordine = customerOrderRepository.findById(pagamento.getId())
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        if (!"COMPLETED".equals(ordine.getStatus())) {
            throw new RuntimeException("L'ordine non è completato. Impossibile effettuare il pagamento.");
        }

        pagamento.setPaymentDate(LocalDate.now());
        pagamento.setStatus("SUCCESS");
        Payment pagamentoSalvato = paymentRepository.save(pagamento);

        ordine.setPayment(pagamentoSalvato);
        customerOrderRepository.save(ordine);

        return pagamentoSalvato;
    }


    private List<OrderDetail> processCartProducts(CustomerOrder ordine, List<CartProduct> prodottiCarrello) {
        List<OrderDetail> dettagliOrdine = new ArrayList<>();

        for (CartProduct cartProduct : prodottiCarrello) {
            Product prodotto = productRepository.findById(cartProduct.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

            int nuovaQuantitaDisponibile = prodotto.getAvailableQuantity() - cartProduct.getQuantity();
            if (nuovaQuantitaDisponibile < 0) {
                throw new RuntimeException("Quantità insufficiente per il prodotto: " + prodotto.getProductName());
            }


            prodotto.setAvailableQuantity(nuovaQuantitaDisponibile);
            productRepository.save(prodotto);


            Category categoria = prodotto.getCategory();
            categoria.setCountProduct(categoria.getCountProduct() - cartProduct.getQuantity());
            categoryRepository.save(categoria);

            OrderDetail dettaglioOrdine = new OrderDetail();
            dettaglioOrdine.setId(new OrderDetailId(ordine.getId(), prodotto.getId()));
            dettaglioOrdine.setCustomerOrder(ordine);
            dettaglioOrdine.setProduct(prodotto);
            dettaglioOrdine.setPaymentDate(LocalDate.now());
            dettagliOrdine.add(dettaglioOrdine);
        }

        return dettagliOrdine;
    }

    @Transactional
    public CustomerOrder checkout(Long orderId) {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new RuntimeException("Utente non trovato");
        }

        // Blocca l'ordine con il lock pessimistico
        CustomerOrder ordine = customerOrderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        try {
            System.out.println("Lock acquisito sull'ordine con ID: " + ordine.getId());
            Thread.sleep(10000); // Ritardo simulato per testare il lock
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        if (!"PENDING".equals(ordine.getStatus())) {
            throw new RuntimeException("L'ordine non è processabile: stato non valido.");
        }


        Cart carrello = cartRepository.findCartWithProductsByUtenteShop(utenteShop);
        if (carrello == null) {
            throw new RuntimeException("Carrello vuoto");
        }

        List<CartProduct> prodottiCarrello = cartProductRepository.findByCart(carrello);
        List<OrderDetail> dettagliOrdine = processCartProducts(ordine, prodottiCarrello);

        ordine.setStatus("COMPLETED");
        ordine.setOrderDate(LocalDate.now());
        customerOrderRepository.save(ordine);

        orderDetailRepository.saveAll(dettagliOrdine);

        cartProductRepository.deleteAll(prodottiCarrello);
        cartRepository.delete(carrello);

        return ordine;
    }


}
