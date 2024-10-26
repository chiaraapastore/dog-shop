package com.example.dogshop.service;

import com.example.dogshop.entity.Cart;
import com.example.dogshop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findCartWithProductsByUserId(userId);
    }


    public Cart getCartWithProductsByUserId(Long userId) {
        return cartRepository.findCartWithProductsByUserId(userId);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
}
