package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findUserByUsername(String username);
}
