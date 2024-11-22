package com.projetpedagogique.pegagogicalplatform.Util;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Exam;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendExamNotification(Student student, Exam exam) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Nouvel Examen Disponible");
        message.setText("Un nouvel examen " + exam.getExamName() + " est disponible. Veuillez vous connecter pour le passer.");
        mailSender.send(message);
    }
}
