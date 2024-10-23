package com.opcr.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_connections",
            joinColumns = @JoinColumn(name = "id_first_user"),
            inverseJoinColumns = @JoinColumn(name = "id_second_user")
    )
    private List<User> userConnections = new ArrayList<>();

}
