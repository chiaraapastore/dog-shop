package com.example.dogshop.controller;
import com.example.dogshop.entity.CustomerOrder;
import com.example.dogshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/user/{userId}")
    public List<CustomerOrder> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.findOrdersByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<CustomerOrder> placeOrder(@RequestBody CustomerOrder order) {
        CustomerOrder savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(savedOrder);
    }
}
