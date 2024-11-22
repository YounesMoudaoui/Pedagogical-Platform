package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findUserByUsername(String username);
}
