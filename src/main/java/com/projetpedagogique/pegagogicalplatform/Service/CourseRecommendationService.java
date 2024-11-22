package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Course;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.ExamResult;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.CourseRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.ExamResultRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.StudentInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseRecommendationService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentService studentService;

    public List<Course> recommendCoursesForStudent(Long studentId) {
        // Récupérer les intérêts de l'étudiant
        List<String> interests = studentService.getStudentInterests(studentId);

        // Liste de recommandations
        List<Course> recommendedCourses = new ArrayList<>();

        // Récupérer tous les cours (ou restreindre cela selon vos besoins)
        List<Course> allCourses = courseRepository.findAll();

        for (Course course : allCourses) {
            // Extraire les intérêts du cours et les séparer (par exemple, par virgule)
            String[] courseInterests = course.getInterest().split(",");

            // Comparer chaque intérêt de l'étudiant avec les intérêts du cours
            for (String studentInterest : interests) {
                for (String courseInterest : courseInterests) {
                    // Supprimer les espaces supplémentaires et comparer en minuscules
                    if (courseInterest.trim().equalsIgnoreCase(studentInterest.trim())) {
                        recommendedCourses.add(course);
                        break; // Ajouter le cours si une correspondance est trouvée, puis passer au cours suivant
                    }
                }
            }
        }

        // Supprimer les doublons
        return recommendedCourses.stream().distinct().collect(Collectors.toList());
    }
}