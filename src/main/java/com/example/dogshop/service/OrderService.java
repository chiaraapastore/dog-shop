package com.example.dogshop.service;
import com.example.dogshop.entity.CustomerOrder;
import com.example.dogshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<CustomerOrder> findOrdersByUserId(Long userId) {
        return orderRepository.findByUtenteId(userId);
    }

    public CustomerOrder saveOrder(CustomerOrder order) {
        return orderRepository.save(order);
    }
}

