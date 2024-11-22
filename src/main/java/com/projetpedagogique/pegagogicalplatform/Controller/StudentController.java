package com.projetpedagogique.pegagogicalplatform.Controller;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.*;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.StudentInterestRepository;
import com.projetpedagogique.pegagogicalplatform.Service.AIService;
import com.projetpedagogique.pegagogicalplatform.Service.CourseRecommendationService;
import com.projetpedagogique.pegagogicalplatform.Service.ExamService;
import com.projetpedagogique.pegagogicalplatform.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private AIService aiService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ExamService examService;
    @Autowired
    private CourseRecommendationService courseRecommendationService;
    @Autowired
    private StudentInterestRepository studentInterestRepository;

    private static final String UPLOAD_DIRECTORY = "uploads/";

    // Afficher la liste des cours disponibles
    @GetMapping("/courses")
    public String getAvailableCourses( Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        List<Course> courses = studentService.getAvailableCourses(student.getId());
        model.addAttribute("courses", courses); // Ajout de studentId
        return "student/student-courses";  // Renvoie la vue 'student-courses.html'
    }


    // Inscription à un cours
    @PostMapping("/courses/{courseId}/enroll")
    public String enrollInCourse( @PathVariable Long courseId, Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        Course course = studentService.enrollInCourse(student.getId(), courseId);
        model.addAttribute("course", course); // Ajout de studentId
        return "student/student-enrollment-success";  // Renvoie la vue 'student-enrollment-success.html'
    }




    // Afficher les résultats d'examen d'un étudiant
    @GetMapping("/results")
    public String getExamResults( Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        List<ExamResult> results = studentService.getExamResults(student.getId());
        List<Course> courses = studentService.getEnrolledCourses(student.getId());
        model.addAttribute("courses", courses);
        model.addAttribute("results", results);
        List<String> colors = Arrays.asList("blue", "green", "yellow", "brown", "purple", "orange");
        model.addAttribute("colors", colors);
        return "student/student-results";  // Renvoie la vue 'student-results.html'
    }
    // Afficher la page pour passer un examen
    @GetMapping("/exams/{examId}/take")
    public String takeExam( @PathVariable Long examId, Model model) {
        Exam exam = studentService.getExamById(examId);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", exam.getQuestions());
        return "student/take-exam";  // Renvoie la vue 'take-exam.html'
    }
    @PostMapping("/exams/{examId}/submit")
    public String submitExam( @PathVariable Long examId,
                             @RequestParam Map<String, String> allParams, Model model, Authentication authentication) {
        Student student1 = studentService.getUser(authentication.getName());
        // Récupérer les détails de l'examen et de l'étudiant
        Exam exam = studentService.getExamById(examId);
        Student student = studentService.getStudentById(student1.getId());
        // Variables pour le score
        int totalQuestions = exam.getQuestions().size();
        int correctAnswers = 0;
        // Itérer sur les questions de l'examen
        for (Question question : exam.getQuestions()) {
            // Récupérer la réponse de l'étudiant pour cette question
            String studentAnswer = allParams.get("answer_" + question.getId());
            System.out.println(studentAnswer.substring(0, 1));
            // Vérifier si l'étudiant a sélectionné une réponse pour cette question
            if (studentAnswer != null) {
                // Comparer avec la réponse correcte
                if (studentAnswer.substring(0, 1).equals(question.getCorrectAnswer())) {
                    correctAnswers++;
                }
            }
        }
        // Calculer le score en pourcentage
        double score =  (((double) correctAnswers / totalQuestions) * 100);
        // Sauvegarder le résultat dans la base de données
        examService.saveResult(exam, student, score);
        // Passer les informations au modèle pour l'affichage des résultats
        model.addAttribute("score", score);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("exam", exam);
        model.addAttribute("student", student);
        // Retourner une vue affichant les résultats
        return "student/exam-result";
    }

    @GetMapping("/course_{id}")
    public String courseDetail(Model model, @PathVariable Long id){
        model.addAttribute("course",studentService.getCourseById(id));
        return "student/course-details";
    }



    @GetMapping("/dashboard")
    public String showStudentDashboard(Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        List<Course> courses = studentService.getEnrolledCourses(student.getId()); // Récupère les cours inscrits
        model.addAttribute("courses", courses);

        return "student/student-dashboard";
    }

    // Afficher la liste des cours auxquels un étudiant est inscrit
    @GetMapping("/my-courses")
    public String getEnrolledCourses( Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());
        model.addAttribute("courses", enrolledCourses);
        return "student/enrolled-courses";  // Renvoie la vue 'enrolled-courses.html'
    }
    @GetMapping( "/courses/{courseId}/exams")
    public String getExamsForEnrolledCourses(@PathVariable Long courseId, Model model){
        List<Exam> exams = studentService.getExamsForEnrolledCourses(courseId);
        model.addAttribute("exams", exams);
        List<String> colors = Arrays.asList("blue", "green", "yellow", "brown", "purple", "orange");
        model.addAttribute("colors", colors);
        return "student/enrolledExams";
    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIRECTORY).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    @GetMapping("/recommendations")
    public String getRecommendations( Model model, Authentication authentication) {
        Student student = studentService.getUser(authentication.getName());
        List<Course> recommendedCourses = courseRecommendationService.recommendCoursesForStudent(student.getId());
        model.addAttribute("recommendedCourses", recommendedCourses);
            return "student/recommendations";  // Vue pour afficher les recommandations
    }
    @GetMapping("/interests")
    public String showInterestsForm( Model model) {
        return "student/interests-form";  // Formulaire pour ajouter des intérêts
    }

    @PostMapping("/interests")
    public String saveInterests(@RequestParam String interest, Authentication authentication) {
        Student student1 = studentService.getUser(authentication.getName());
        Student student = studentService.getStudentById(student1.getId());
        StudentInterest studentInterest = new StudentInterest(null, student, interest);
        studentInterestRepository.save(studentInterest);
        return "redirect:/students/interests";
    }
//    @GetMapping("/{studentId}/recommendations")
//    public String showCourseRecommendations(@PathVariable Long studentId, Model model) {
//        List<Course> recommendedCourses = studentService.recommendCourses(studentId);
//        model.addAttribute("recommendedCourses", recommendedCourses);
//        model.addAttribute("studentId", studentId);
//        return "student/recommendations";
//    }
}




