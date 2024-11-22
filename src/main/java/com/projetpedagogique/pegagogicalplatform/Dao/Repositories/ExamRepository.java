package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Course;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
    // Méthode pour récupérer tous les examens créés par un enseignant
    List<Exam> findExamsByTeacherId(Long teacherId);
    // Méthode pour récupérer tous les examens liés à un cours spécifique
    List<Exam> findByCourseId(Long courseId);
    Exam findExamById(Long examId);

}
