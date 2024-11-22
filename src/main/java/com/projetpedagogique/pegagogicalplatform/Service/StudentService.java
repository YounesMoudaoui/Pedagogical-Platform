package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.*;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private StudentInterestRepository studentInterestRepository;

    public List<Course> getAvailableCourses(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        return courseRepository.findAll().stream()
                .filter(course -> !course.getStudents().contains(student))
                .collect(Collectors.toList());
    }

    public Course enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.getStudents().add(student);
        return courseRepository.save(course);
    }

    public List<Exam> getExamsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return course.getExams();
    }

    public ExamResult submitExam(Long studentId, Long examId, double score) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Exam exam = examRepository.findById(examId).orElseThrow();
        ExamResult result = new ExamResult();
        result.setStudent(student);
        result.setExam(exam);
        result.setScore(score);
        return examResultRepository.save(result);
    }

    public List<ExamResult> getExamResults(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        return examResultRepository.findByStudent(student);
    }
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen non trouvé avec l'ID : " + examId));
    }
    public List<Course> getEnrolledCourses(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));
        return student.getCourses();  // Retourne la liste des cours auxquels l'étudiant est inscrit
    }
    public List<Exam> getExamsForEnrolledCourses(Long courseId){
        return examRepository.findByCourseId(courseId)
                .stream()
                .filter(Exam::getIsActive) // Filtrer les examens actifs
                .collect(Collectors.toList()); // Collecter les résultats en une liste
    }


    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow();
    }
    // Méthode pour récupérer les intérêts de l'étudiant
    public List<String> getStudentInterests(Long studentId) {
        List<StudentInterest> interests = studentInterestRepository.findByStudentId(studentId);
        return interests.stream().map(StudentInterest::getInterest).collect(Collectors.toList());
    }
    // Méthode pour récupérer les performances passées de l'étudiant
    public Map<Course, Double> getStudentPerformance(Long studentId) {
        List<ExamResult> examResults = examResultRepository.findByStudentId(studentId);

        Map<Course, Double> performanceByCourse = new HashMap<>();
        for (ExamResult result : examResults) {
            Course course = result.getExam().getCourse();
            performanceByCourse.put(course, result.getScore());
        }

        return performanceByCourse;
    }
    public Course getCourseById(Long id){
        return courseRepository.findById(id).orElseThrow();
    }
    public Student getUser(String username){
        return studentRepository.findUserByUsername(username);
    }
    // Méthode pour recommander des cours basés sur les intérêts et les performances
    public List<Course> recommendCourses(Long studentId) {
        List<String> interests = getStudentInterests(studentId);  // Récupérer les intérêts
        Map<Course, Double> performanceByCourse = getStudentPerformance(studentId);  // Récupérer les performances

        // Récupérer tous les cours
        List<Course> allCourses = courseRepository.findAll();

        List<Course> recommendedCourses = new ArrayList<>();

        for (Course course : allCourses) {
            // Si le cours correspond aux intérêts de l'étudiant et qu'il n'a pas déjà de mauvais résultats dans ce domaine
            for (String interest : interests) {
                if (course.getTitle().toLowerCase().contains(interest.toLowerCase())) {
                    Double performance = performanceByCourse.get(course);
                    if (performance == null || performance > 70) {  // Recommander si performance > 70 ou pas encore suivi
                        recommendedCourses.add(course);
                    }
                }
            }
        }

        return recommendedCourses;
    }
}