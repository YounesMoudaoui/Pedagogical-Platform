package com.projetpedagogique.pegagogicalplatform.Service;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Roles;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Teacher;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.User;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.RolesRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.StudentRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.TeacherRepository;
import com.projetpedagogique.pegagogicalplatform.Dao.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User u) {
        if (Objects.equals(u.getRole1(), "STUDENT")){
            Student student = new Student();
            student.setEmail(u.getEmail());
            student.setUsername(u.getUsername());
            student.setPassword(passwordEncoder.encode(u.getPassword()));
            student.setRole(getRoleStudent());
            return studentRepository.save(student);}
        else if (Objects.equals(u.getRole1(), "TEACHER")){
            Teacher teacher = new Teacher();
            teacher.setUsername(u.getUsername());
            teacher.setEmail(u.getEmail());
            teacher.setPassword(passwordEncoder.encode(u.getPassword()));
            teacher.setRole(getRoleTeacher());
            return teacherRepository.save(teacher);}
        else{
            User user = new User();
            user.setUsername(u.getUsername());
            user.setPassword(passwordEncoder.encode(u.getPassword()));
            user.setEmail(u.getEmail());
            user.setRole(getRoleAdmin());
            return userRepository.save(user);

        }
    }
    public Roles getRoleAdmin(){
        return rolesRepository.findByRole("ADMIN");
    }
    public Roles getRoleStudent(){
        return rolesRepository.findByRole("STUDENT");
    }
    public Roles getRoleTeacher(){
        return rolesRepository.findByRole("TEACHER");
    }
    public User getUser(String username){
        return userRepository.findUserByUsername(username);
    }
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
