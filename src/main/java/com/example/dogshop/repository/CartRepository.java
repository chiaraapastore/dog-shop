package com.example.dogshop.repository;


import com.example.dogshop.entity.Cart;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c JOIN c.cartProducts cp WHERE c.utente.id = :userId")
    Cart findCartWithProductsByUserId(@Param("userId") Long userId);
}

