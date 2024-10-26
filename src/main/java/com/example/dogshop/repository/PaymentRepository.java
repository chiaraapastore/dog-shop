package com.example.dogshop.repository;

import com.example.dogshop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByCustomerOrder_Id(Long orderId);
}

