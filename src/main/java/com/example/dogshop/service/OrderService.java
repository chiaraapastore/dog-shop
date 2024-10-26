package com.example.dogshop.service;
import com.example.dogshop.entity.Order;
import com.example.dogshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}

