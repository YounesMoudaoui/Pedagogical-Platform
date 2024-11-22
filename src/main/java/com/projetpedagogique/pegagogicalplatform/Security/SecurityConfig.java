package com.projetpedagogique.pegagogicalplatform.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)  // Désactiver les restrictions sur les iframes
                )
                // Désactiver CSRF pour simplifier les tests (à réactiver en production)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/admin/create-user").permitAll()  // Autoriser l'accès à la page de connexion
                        .requestMatchers("/students/**").hasRole("STUDENT")  // Pas de préfixe "ROLE_" ici
                        .requestMatchers("/teachers/**").hasRole("TEACHER")  // Pas de préfixe "ROLE_"
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Pas de préfixe "ROLE_"
                        .anyRequest().authenticated()  // Toutes les autres requêtes nécessitent une authentification
                )
                .formLogin(form -> form
                                .loginPage("/login")  // Page de connexion personnalisée
//                        .successHandler(customAuthenticationSuccessHandler())
                                .successHandler(customAuthenticationSuccessHandler())  // Gérer les redirections après connexion
                                .permitAll()  // Permettre l'accès à tous à la page de connexion
                )
                .logout(LogoutConfigurer::permitAll);  // Permettre à tout le monde de se déconnecter
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT username AS principal, password AS credentials, enabled FROM users WHERE username=?")
                .authoritiesByUsernameQuery("SELECT u.username AS principal, r.role AS role FROM users u INNER JOIN roles r ON u.role_id = r.id WHERE u.username=?")
                .passwordEncoder(passwordEncoder)
                .rolePrefix("ROLE_");
    }
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {
                String role = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ROLE_TEACHER") || auth.equals("ROLE_STUDENT"))
                        .findFirst()
                        .orElse("ROLE_USER");

                if ("ROLE_ADMIN".equals(role)) {
                    response.sendRedirect("/admin/dashboard");
                } else if ("ROLE_TEACHER".equals(role)) {
                    response.sendRedirect("/teachers/dashboard");
                } else if ("ROLE_STUDENT".equals(role)) {
                    response.sendRedirect("/students/dashboard");
                }
            }
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
