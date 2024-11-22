package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Course;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Exam;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.ExamResult;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Teacher;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.CourseRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.ExamRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.TeacherRepository;
import com.projetpedagogique.pegagogicalplatform.Util.PdfReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    @Autowired
    private AIService aiService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private InterestExtractionService interestExtractionService;

    public String generateQuestionsFromPdf(String pdfFilePath) {
        try {
            // Extraire le texte du PDF
            String pdfContent = PdfReader.extractTextFromPdf(pdfFilePath);

            // Appeler l'API OpenAI pour générer des questions
            return aiService.generateQuestions(pdfContent);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier PDF : " + e.getMessage());
            return null;
        }
    }

    public Course createCourse(Long teacherId, String title, String description, String file) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setCourseToTeacher(teacher);
        course.setPdfFileName(file);

        // Extraire automatiquement les intérêts du texte de description
        String interests = interestExtractionService.extractInterests(description);
        course.setInterest(interests);

        return courseRepository.save(course);
    }



    public List<Course> getCoursesByTeacher(Long teacherId) {
        Optional<Teacher> teacher = teacherRepository.findById(teacherId);

        if (teacher.isPresent()) {
            return teacher.get().getCourses();
        } else {
            // Retourner une liste vide si l'enseignant ou les cours ne sont pas présents
            return new ArrayList<>();
        }
    }
    public Teacher getUser(String username){
        return teacherRepository.findUserByUsername(username);
    }

    public Exam createExam(Long teacherId, Long courseId, String examName, String description, Date date, String pdfFileName) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Exam exam = new Exam();
        exam.setExamName(examName);
        exam.setDescription(description);
        exam.setDate(date);
        exam.setIsActive(false);
        exam.setTeacher(teacher);
        exam.setCourse(course);
        exam.setPdfFileName(pdfFileName);  // Ajouter le nom du fichier PDF

        return examRepository.save(exam);
    }




    public List<ExamResult> getExamResultsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return examRepository.findByCourse(course).stream()
                .flatMap(exam -> exam.getResults().stream())
                .collect(Collectors.toList());
    }
    public List<Exam> getExamsForEnrolledCourses(Long courseId){
        return examRepository.findByCourseId(courseId)
                .stream()
                .filter(Exam::getIsActive) // Filtrer les examens actifs
                .collect(Collectors.toList()); // Collecter les résultats en une liste
    }
    public List<Exam> getExamsByTeacher(Long teacherId) {
        // Logique pour récupérer tous les examens créés par l'enseignant (à adapter selon votre logique de DAO)
        return examRepository.findExamsByTeacherId(teacherId);
    }
    // Récupérer la liste des examens d'un cours spécifique
    public List<Exam> getExamsForCourse(Long courseId) {
        return examRepository.findByCourseId(courseId);  // Utiliser la méthode du repository pour récupérer les examens
    }
    public Exam getExamById(Long examId){
        return examRepository.findExamById(examId);
    }
    public Exam updateExam(Exam exam){
        return examRepository.save(exam);
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    public void deleteExam(Long examId) {
        examRepository.deleteById(examId);
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }
    public Course updateCourse(Long courseId, Course c) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setTitle(c.getTitle());
        course.setDescription(c.getDescription());
        course.setPdfFileName(c.getPdfFileName());

        // Ré-extraction des intérêts lors de la mise à jour du cours
        String interests = interestExtractionService.extractInterests(c.getDescription());
        course.setInterest(interests);

        return courseRepository.save(course);
    }

}