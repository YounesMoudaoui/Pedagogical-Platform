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
public class Teacher extends User {

    @ManyToMany(mappedBy = "teachers")
    private List<Course> courses;

    @OneToMany(mappedBy = "teacher")
    private List<Exam> exams;

    // Getters et setters
}
