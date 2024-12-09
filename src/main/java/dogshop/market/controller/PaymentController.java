package dogshop.market.controller;

import dogshop.market.entity.CustomerOrder;
import dogshop.market.entity.Payment;
import dogshop.market.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/checkout")
    public ResponseEntity<CustomerOrder> checkout() {
        CustomerOrder ordine = paymentService.checkout();
        return new ResponseEntity<>(ordine, HttpStatus.CREATED);
    }

    @PostMapping("/acquista")
    public ResponseEntity<Payment> acquista(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.acquista(payment);
        return new ResponseEntity<>(savedPayment, HttpStatus.OK);
    }

}