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
        Cart carrello = cartRepository.findCartWithProductsByUtenteShop(utenteShop);
        if (carrello == null) {
            throw new RuntimeException("Carrello vuoto");
        }


        CustomerOrder ordine = customerOrderRepository.findByIdWithLock(pagamento.getId())
                .orElseGet(() -> {
                    System.out.println("Nessun ordine trovato per l'utente. Creazione di un nuovo ordine.");
                    return createOrder(utenteShop);
                });

        // Verifica che l'ordine sia processabile
        if (!"PENDING".equals(ordine.getStatus())) {
            throw new RuntimeException("Ordine non processabile: stato non valido.");
        }

        try {
            System.out.println("Lock acquisito sull'ordine con ID: " + ordine.getId());
            Thread.sleep(5000); // Ritardo di 10 secondi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }



        // Processa i prodotti dell'ordine
        List<CartProduct> prodottiCarrello = cartProductRepository.findByCart(carrello);
        List<OrderDetail> dettagliOrdine = processCartProducts(ordine, prodottiCarrello);



        pagamento.setPaymentDate(LocalDate.now());
        pagamento.setStatus("SUCCESS");
        Payment pagamentoSalvato = paymentRepository.save(pagamento);


        ordine.setPayment(pagamentoSalvato);
        ordine.setStatus("COMPLETED");
        customerOrderRepository.save(ordine);


        orderDetailRepository.saveAll(dettagliOrdine);


        cartProductRepository.deleteAll(prodottiCarrello);
        cartRepository.delete(carrello);

        return pagamentoSalvato;
    }

    private CustomerOrder createOrder(UtenteShop utenteShop) {
        CustomerOrder ordine = new CustomerOrder();
        ordine.setUtenteShop(utenteShop);
        ordine.setOrderDate(LocalDate.now());
        ordine.setStatus("PENDING");
        ordine.setTotalAmount(calculateTotalAmount(utenteShop));
        ordine.setOrderNumber(generateOrderNumber());
        return customerOrderRepository.save(ordine);
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

            // Aggiorna la quantità disponibile del prodotto
            prodotto.setAvailableQuantity(nuovaQuantitaDisponibile);
            productRepository.save(prodotto);

            // Aggiorna il conteggio nella categoria del prodotto
            Category categoria = prodotto.getCategory();
            categoria.setCountProduct(categoria.getCountProduct() - cartProduct.getQuantity());
            categoryRepository.save(categoria);

            // Crea un nuovo dettaglio ordine
            OrderDetail dettaglioOrdine = new OrderDetail();
            dettaglioOrdine.setId(new OrderDetailId(ordine.getId(), prodotto.getId()));
            dettaglioOrdine.setCustomerOrder(ordine);
            dettaglioOrdine.setProduct(prodotto);
            dettaglioOrdine.setPaymentDate(LocalDate.now());
            dettaglioOrdine.setQuantity(cartProduct.getQuantity());

            dettagliOrdine.add(dettaglioOrdine);
        }

        return dettagliOrdine;
    }


    private double calculateTotalAmount(UtenteShop utenteShop) {
        Cart cart = cartRepository.findCartWithProductsByUtenteShop(utenteShop);
        return cartProductRepository.findByCart(cart).stream()
                .mapToDouble(cartProduct -> cartProduct.getProduct().getPrice() * cartProduct.getQuantity())
                .sum();
    }

    private String generateOrderNumber() {
        Long lastOrderId = customerOrderRepository.findMaxOrderId();
        int newOrderId = (lastOrderId != null) ? lastOrderId.intValue() + 1 : 10000;
        return "#" + String.format("%05d", newOrderId);
    }
}
