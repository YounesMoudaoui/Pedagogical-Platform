package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;
    @OneToMany(mappedBy="role")
    private Collection<User> users;
}
