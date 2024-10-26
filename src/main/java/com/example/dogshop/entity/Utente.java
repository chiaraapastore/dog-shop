package com.example.dogshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToOne(mappedBy = "utente", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL)
    private List<CustomerOrder> orders;
}
