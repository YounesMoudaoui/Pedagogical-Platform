package com.projetpedagogique.pegagogicalplatform.Controller;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Teacher;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.User;
import com.projetpedagogique.pegagogicalplatform.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller  // Utilisation de @Controller pour rendre des vues HTML
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Page pour créer un utilisateur (formulaire HTML)
    @GetMapping("/create-user")
    public String showCreateUserForm(Model model) {
        // Ajouter un objet User vide au modèle pour le formulaire
        model.addAttribute("user", new User());
        return "admin/create-user";  // Renvoie le template Thymeleaf 'create-user.html'
    }

    // Traitement du formulaire de création d'utilisateur
    @PostMapping("/create-user")
    public String createUser(@Validated @ModelAttribute("user") User user, Model model) {
        User user1 = adminService.createUser(user);
        model.addAttribute("user", user1);  // Ajouter l'utilisateur créé au modèle
        return "admin/user-details";  // Renvoie le template 'user-details.html' avec les détails de l'utilisateur
    }

    // Page pour afficher la liste des étudiants
    @GetMapping("/students")
    public String getAllStudents(Model model) {
        List<Student> students = adminService.getAllStudents();
        model.addAttribute("students", students);  // Ajouter la liste des étudiants au modèle
        return "admin/student-list";  // Renvoie le template 'student-list.html'
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);  // Ajouter la liste des étudiants au modèle
        return "admin/user-list";  // Renvoie le template 'student-list.html'
    }

    // Page pour afficher la liste des enseignants
    @GetMapping("/teachers")
    public String getAllTeachers(Model model) {
        List<Teacher> teachers = adminService.getAllTeachers();
        model.addAttribute("teachers", teachers);  // Ajouter la liste des enseignants au modèle
        return "admin/teacher-list";  // Renvoie le template 'teacher-list.html'
    }

    // Suppression d'un utilisateur avec confirmation
    @GetMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable Long userId, Model model) {
        adminService.deleteUser(userId);
        // Ajouter un message de confirmation dans le modèle
        model.addAttribute("message", "Utilisateur supprimé avec succès !");
        return "admin/delete-user";  // Renvoie le template de confirmation de suppression
    }
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin/admin-dashboard";  // Return the view 'admin-dashboard.html'
    }
}
