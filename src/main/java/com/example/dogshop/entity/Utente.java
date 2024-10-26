package com.example.dogshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String numberCell;
    private String role;
    private String username;

    @OneToOne(mappedBy = "customerCart", cascade = CascadeType.ALL)
    private Cart carts;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL)
    private List<Order> orders;

}