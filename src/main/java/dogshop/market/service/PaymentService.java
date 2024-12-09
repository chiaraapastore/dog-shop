package dogshop.market.service;

import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.*;
import dogshop.market.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CartProductRepository cartProductRepository;
    private final AuthenticationService authenticationService;
    private final UtenteShopRepository utenteShopRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductService productService;

    public PaymentService(PaymentRepository paymentRepository, CustomerOrderRepository customerOrderRepository, CartProductRepository cartProductRepository, AuthenticationService authenticationService, UtenteShopRepository utenteShopRepository, CartRepository cartRepository, ProductRepository productRepository, CategoryRepository categoryRepository, OrderProductRepository orderProductRepository, ProductService productService) {
        this.paymentRepository = paymentRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.cartProductRepository = cartProductRepository;
        this.authenticationService = authenticationService;
        this.utenteShopRepository = utenteShopRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productService = productService;
        this.orderProductRepository = orderProductRepository;
    }

  /* @Transactional
    public Payment savePayment(Payment pagamento) {
        try {

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
                        System.out.println("Nessun ordine trovato. Creazione di un nuovo ordine.");
                       return createOrder(utenteShop);
                   });

           if (!"PENDING".equals(ordine.getStatus())) {
               throw new RuntimeException("Ordine non processabile: stato non valido (" + ordine.getStatus() + ").");
           }

            List<CartProduct> prodottiCarrello = cartProductRepository.findByCart(carrello);

            pagamento.setPaymentDate(LocalDate.now());
            pagamento.setStatus("SUCCESS");
            Payment pagamentoSalvato = paymentRepository.save(pagamento);
            ordine.setPayment(pagamentoSalvato);
            ordine.setStatus("COMPLETED");
            customerOrderRepository.save(ordine);


            for (CartProduct cartProduct : prodottiCarrello) {
                Product prodotto = cartProduct.getProduct();
                if (prodotto.getAvailableQuantity() == 0) {
                    cartProductRepository.delete(cartProduct);
                    productRepository.delete(prodotto);
                }
            }


            cartProductRepository.deleteAll(prodottiCarrello);
            cartRepository.delete(carrello);

            System.out.println("Pagamento completato con successo.");
            return pagamentoSalvato;

        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione del pagamento: " + e.getMessage());
            throw new RuntimeException("Errore nell'elaborazione del pagamento", e);
        }
    }*/

    @Transactional
    public CustomerOrder checkout() {
        UtenteShop utenteShop = utenteShopRepository.findByUsername(authenticationService.getUsername());
        if (utenteShop == null) {
            throw new RuntimeException("Utente non trovato");
        }
        Cart carrello = cartRepository.findByUtenteShop(utenteShop)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));
        List<CartProduct> prodottiCarrello = cartProductRepository.findByCart(carrello);
        if (prodottiCarrello.isEmpty()) {
            throw new RuntimeException("Carrello vuoto");
        }
        CustomerOrder ordine = createOrder(utenteShop);
        ordine.setStatus("PENDING");
        customerOrderRepository.save(ordine);

        System.out.println("Ordine creato con successo.");
        return ordine;
    }



    @Transactional
    public Payment acquista(Payment pagamento) {
        if (pagamento.getId() == null) {
            System.err.println("Errore: ID dell'ordine mancante nel pagamento: " + pagamento);
            throw new RuntimeException("ID dell'ordine mancante nel pagamento");
        }
        CustomerOrder ordine = customerOrderRepository.findById(pagamento.getId())
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));
        if (!"PENDING".equals(ordine.getStatus())) {
            throw new RuntimeException("Ordine non processabile: stato non valido (" + ordine.getStatus() + ").");
        }

        pagamento.setPaymentDate(LocalDate.now());
        pagamento.setStatus("SUCCESS");
        Payment pagamentoSalvato = paymentRepository.save(pagamento);

        ordine.setPayment(pagamentoSalvato);
        ordine.setStatus("COMPLETED");
        customerOrderRepository.save(ordine);

        System.out.println("Pagamento completato con successo per l'ordine ID: " + ordine.getId());
        return pagamentoSalvato;
    }


    private CustomerOrder createOrder(UtenteShop utenteShop) {
        CustomerOrder ordine = new CustomerOrder();
        ordine.setUtenteShop(utenteShop);
        ordine.setOrderDate(LocalDate.now());
        ordine.setStatus("PENDING");
        ordine.setTotalAmount(calculateTotalAmount(utenteShop));
        ordine.setOrderNumber(generateOrderNumber());

        CustomerOrder savedOrder = customerOrderRepository.save(ordine);
        System.out.println("Ordine creato con stato: " + savedOrder.getStatus());
        return savedOrder;
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
