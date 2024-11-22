package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @ElementCollection
    private List<String> options;  // Multiple-choice options

    @Column(name = "correct_answer")
    private String correctAnswer;


    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;  // This sets the relationship with the Exam
}
