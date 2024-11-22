package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.*;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Transactional
public class ExamService {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private AIService aiService;

    // Créer un examen avec des questions générées par l'IA
    public Exam createExamWithQuestions(Long teacherId, Long courseId, String examName, String description, Date date, String pdfFileName) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        Exam exam = new Exam();
        exam.setExamName(examName);
        exam.setDescription(description);
        exam.setDate(date);
        exam.setPdfFileName(pdfFileName);
        exam.setIsActive(false);
        exam.setTeacher(teacher);
        exam.setCourse(course);

        // Générer des questions à partir du fichier PDF
        String generatedText = aiService.generateQuestions("src/main/resources/uploads/" + pdfFileName);
        List<Question> generatedQuestions = parseGeneratedQuestions(generatedText);

        for (Question question : generatedQuestions) {
            question.setExam(exam); // Assigner l'examen à chaque question
            try {
                questionRepository.save(question);
            } catch (Exception e) {
                e.printStackTrace();  // Pour afficher les erreurs dans la console
                // Log en cas d'erreur
                System.out.println("Erreur lors de la sauvegarde de la question : " + question.getQuestionText());
            } // Sauvegarder chaque question individuellement
        }
        exam.setQuestions(generatedQuestions);


        return examRepository.save(exam);
    }

    // Analyse du texte généré par l'IA en une liste de questions
    public List<Question> parseGeneratedQuestions(String generatedText) {
        if (generatedText == null || generatedText.isEmpty()) {
            throw new IllegalArgumentException("Le texte généré est vide, impossible d'analyser les questions.");
        }

        List<Question> questions = new ArrayList<>();
        String[] lines = generatedText.split("\n");

        Question currentQuestion = null;
        List<String> options = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            // Identifier la question (ligne commençant par un numéro)
            if (line.matches("^\\d+\\.\\s+.*")) {
                // Sauvegarder la question précédente si elle existe
                if (currentQuestion != null && !options.isEmpty()) {
                    currentQuestion.setOptions(options);
                    questions.add(currentQuestion);
                }

                // Créer une nouvelle question
                currentQuestion = new Question();
                currentQuestion.setQuestionText(line);
                options = new ArrayList<>();
            }
            // Identifier les options de réponse (A, B, C, D)
            else if (line.matches("^[A-D]\\).*")) {
                options.add(line);
            }
            // Identifier la réponse correcte (ligne contenant "Réponse:")
            else if (line.toLowerCase().contains("réponse:")) {
                if (currentQuestion != null) {
                    currentQuestion.setCorrectAnswer(line.split("(?i)réponse:")[1].trim());
                    System.out.println("Correct Answer: " + currentQuestion.getCorrectAnswer());
                }
            }
        }

        // Sauvegarder la dernière question si elle existe
        if (currentQuestion != null && !options.isEmpty()) {
            currentQuestion.setOptions(options);
            questions.add(currentQuestion);
        }

        return questions;
    }



    // Sauvegarder le résultat de l'examen pour un étudiant
    public void saveResult(Exam exam, Student student, double score) {
        ExamResult examResult = new ExamResult();
        examResult.setExam(exam);
        examResult.setStudent(student);
        examResult.setScore(score);
        examResultRepository.save(examResult);
    }

}
