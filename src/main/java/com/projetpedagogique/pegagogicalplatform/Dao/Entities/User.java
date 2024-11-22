package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Transactional
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role1;
    @ManyToOne
    private Roles role;

    // Getters et setters
}
