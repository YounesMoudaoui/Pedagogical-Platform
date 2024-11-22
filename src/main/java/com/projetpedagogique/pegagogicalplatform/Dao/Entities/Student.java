package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Student extends User {

    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    @OneToMany(mappedBy = "student")
    private List<ExamResult> examResults;

    // Getters et setters
}
