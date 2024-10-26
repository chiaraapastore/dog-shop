package com.example.dogshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utente_id")
    private Utente utente; // Cambia il nome per riflettere il "mappedBy" in Utente

    @OneToMany(mappedBy = "cartProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> cartProducts = new ArrayList<>();
}
