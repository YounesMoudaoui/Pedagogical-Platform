package com.projetpedagogique.pegagogicalplatform.Controller;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.*;
import com.projetpedagogique.pegagogicalplatform.Service.ExamService;
import com.projetpedagogique.pegagogicalplatform.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private ExamService examService;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/uploads/";

    @GetMapping("/dashboard")
    public String showTeacherDashboard( Model model) {
        return "teacher/teacher-dashboard";
    }

    // Création d'un cours par un enseignant
    @GetMapping("/create-course")
    public String showCreateCourseForm( Model model, Authentication authentication) {
        Teacher teacher = teacherService.getUser(authentication.getName());
        return "teacher/create-course";  // Renvoie la vue 'create-course.html'
    }

    @PostMapping("courses")
    public String createCourse( @RequestParam String title,
                               @RequestParam String description, Model model,
                               @RequestParam("file") MultipartFile file, Authentication authentication) {
        Teacher teacher = teacherService.getUser(authentication.getName());
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "error";
        }

        try {
            // Créer le répertoire si nécessaire
            Path uploadDirectory = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);  // Crée le répertoire s'il n'existe pas
            }

            // Sauvegarder le fichier dans le répertoire d'uploads
            Path path = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.write(path, file.getBytes());

            // Créer le cours avec les détails du fichier
            Course course = teacherService.createCourse(teacher.getId(), title, description, file.getOriginalFilename());
            model.addAttribute("course", course);
            return "teacher/course-details";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error during file upload: " + e.getMessage());
            return "error";
        }
    }


    // Afficher les cours d'un enseignant
    @GetMapping("courses")
    public String getCoursesByTeacher( Model model, Authentication authentication) {
        Teacher teacher = teacherService.getUser(authentication.getName());
        List<Course> courses = teacherService.getCoursesByTeacher(teacher.getId());
        if (courses.isEmpty()) {
            // Si aucun cours n'est trouvé, ajouter un message au modèle
            model.addAttribute("message", "Aucun cours disponible pour cet enseignant.");
            return "teacher/no-courses";
        }
        model.addAttribute("courses", courses);
        return "teacher/teacher-courses";  // Renvoie la vue 'teacher-courses.html'
    }

    // Formulaire pour créer un examen
    @GetMapping("/{courseId}/create-exam")
    public String showCreateExamForm( @PathVariable Long courseId,Model model) {
        model.addAttribute("courseId", courseId);
        return "teacher/create-exam";  // Renvoie la vue 'create-exam.html'
    }

    @PostMapping("/courses/{courseId}/exams")
    public String createExam( @PathVariable Long courseId,
                             @RequestParam String examName, @RequestParam String description,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                             @RequestParam("file") MultipartFile file, Model model, Authentication authentication) {
        Teacher teacher = teacherService.getUser(authentication.getName());
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "redirect:/error";
        }

        try {
            // Save the PDF file
            Path uploadDirectory = Paths.get("src/main/resources/uploads");
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);  // Create the directory if it doesn't exist
            }
            Path path = uploadDirectory.resolve(file.getOriginalFilename());
            Files.write(path, file.getBytes());

            // Create the exam with AI-generated questions
            Exam exam = examService.createExamWithQuestions(teacher.getId(), courseId, examName, description, date, file.getOriginalFilename());
            model.addAttribute("exam", exam);
            model.addAttribute("courseId", courseId);
            return "teacher/exam-details";  // Return the view 'exam-details.html'
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error during file upload: " + e.getMessage());
            return "redirect:/error";
        }
    }
    @GetMapping("/update-course/{courseId}")
    public String updateCourse(@PathVariable Long courseId, Model model) {
        Course course = teacherService.getCourseById(courseId);
        model.addAttribute("course", course);// Pass teacherId to the view for future use
        return "teacher/update_course";
    }

    @PostMapping("/courses/{courseId}/save_course")
    public String saveUpdatedCourse(@PathVariable Long courseId,
                                    @RequestParam String title,
                                    @RequestParam String description,
                                    @RequestParam("file") MultipartFile file, // Handle file as MultipartFile
                                    Model model) {

        try {
            // Get the existing course from the database
            Course course = teacherService.getCourseById(courseId);

            // Update the title and description
            course.setTitle(title);
            course.setDescription(description);

            // Handle file upload if a new file is provided
            if (!file.isEmpty()) {
                // Create the directory if it doesn't exist
                Path uploadDirectory = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadDirectory)) {
                    Files.createDirectories(uploadDirectory);  // Create the directory if it doesn't exist
                }

                // Save the new file to the uploads directory
                Path path = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
                Files.write(path, file.getBytes());

                // Set the new file name to the course
                course.setPdfFileName(file.getOriginalFilename());
            }

            // Save the updated course to the database
            teacherService.updateCourse(courseId, course);

            return "redirect:/teachers/courses";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error during file upload: " + e.getMessage());
            return "redirect:/error";
        }
    }




    @GetMapping("/delete-course/{courseId}")
    public String deleteCourse(@PathVariable Long courseId, Model model) {
        teacherService.deleteCourse(courseId);
        return "redirect:/teacher/courses";
    }

    @GetMapping("/courses/{courseId}/delete-exam/{examId}")
    public String deleteExam(@PathVariable Long courseId,@PathVariable Long examId, Model model) {
        teacherService.deleteExam(examId);
        return "redirect:/teacher/courses/"+courseId+"/exams";
    }





    // Afficher les résultats d'examen pour un cours
    @GetMapping("/courses/{courseId}/results")
    public String getExamResultsForCourse( @PathVariable Long courseId, Model model) {
        List<ExamResult> results = teacherService.getExamResultsForCourse(courseId);
        Course course = teacherService.getCourseById(courseId);
        model.addAttribute("title",course.getTitle());
        model.addAttribute("results", results); // Ajouter teacherId si vous en avez besoin dans la vue
        return "teacher/course-results";  // Renvoie le template Thymeleaf 'course-results.html'
    }
    // Afficher la liste des examens créés par un enseignant
    @GetMapping("/exams")
    public String getExamsByTeacher( Model model, Authentication authentication) {
        Teacher teacher = teacherService.getUser(authentication.getName());
        List<Exam> exams = teacherService.getExamsByTeacher(teacher.getId());
        model.addAttribute("exams", exams);
        return "teacher/teacher-exams";  // Renvoie la vue 'teacher-exams.html'
    }
    // Afficher la liste des examens pour un cours spécifique
    @GetMapping("/courses/{courseId}/exams")
    public String getExamsForCourse( @PathVariable Long courseId, Model model) {
        List<Exam> exams = teacherService.getExamsForCourse(courseId);  // Récupérer les examens liés à ce cours
        model.addAttribute("exams", exams);
        model.addAttribute("courseId", courseId);
        List<String> colors = Arrays.asList("blue", "green", "yellow", "brown", "purple", "orange");
        model.addAttribute("colors", colors);


        return "teacher/course-exams";  // Renvoie la vue 'course-exams.html'
    }

    @PostMapping("/course/{courseId}/exam/{examId}/activate-exam")
    public String activateExam(@PathVariable Long examId,@PathVariable Long courseId, Model model){
        Exam exam = teacherService.getExamById(examId);
        exam.setIsActive(!(exam.getIsActive()));
        teacherService.updateExam(exam);
        return "redirect:/teachers/courses/"+courseId+"/exams";


    }



}
