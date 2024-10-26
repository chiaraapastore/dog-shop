package com.example.dogshop.controller;

import com.example.dogshop.entity.Cart;
import com.example.dogshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        Cart savedCart = cartService.saveCart(cart);
        return new ResponseEntity<>(savedCart, HttpStatus.CREATED);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}/with-products")
    public ResponseEntity<Cart> getCartWithProductsByUserId(@PathVariable Long userId) {
        Cart cart = cartService.getCartWithProductsByUserId(userId);
        return ResponseEntity.ok(cart);
    }
}
