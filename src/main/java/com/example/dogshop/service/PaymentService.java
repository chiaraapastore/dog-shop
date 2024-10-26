package com.example.dogshop.service;

import com.example.dogshop.entity.Payment;
import com.example.dogshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByCustomerOrder_Id(orderId);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
