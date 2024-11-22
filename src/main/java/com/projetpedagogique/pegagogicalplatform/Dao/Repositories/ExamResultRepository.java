package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.ExamResult;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByStudent(Student student);

    List<ExamResult> findByStudentId(Long studentId);
}
