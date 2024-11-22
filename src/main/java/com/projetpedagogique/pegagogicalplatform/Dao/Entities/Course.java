package com.projetpedagogique.pegagogicalplatform.Dao.Entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 1000)
    private String description;
    private String pdfFileName;
    // Ajouter une propriété pour représenter les intérêts
    @Column(length = 1000)
    private String interest;

    @ManyToMany
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

    @ManyToMany
    @JoinTable(
            name = "teacher_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Exam> exams;

    public void setCourseToTeacher(Teacher teacher){
        this.teachers.add(teacher);
    }
    public Teacher getTeacher(Long id){
        return (Teacher) this.getTeachers().stream().filter(teacher -> teacher.getId().equals(id));
    }
    @ManyToOne
    private Course advancedCourse; // Cours avancé lié à un cours de base

}
