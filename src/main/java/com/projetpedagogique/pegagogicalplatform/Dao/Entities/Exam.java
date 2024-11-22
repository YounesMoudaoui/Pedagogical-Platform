package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName;
    @Column(length = 1000)
    private String description;
    private Date date;
    private Boolean isActive= false;
    private String pdfFileName;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @OneToMany(mappedBy = "exam")
    private List<ExamResult> results;
}
