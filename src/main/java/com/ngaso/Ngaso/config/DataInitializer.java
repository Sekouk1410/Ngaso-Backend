package com.ngaso.Ngaso.config;

import com.ngaso.Ngaso.DAO.AdministrateurRepository;
import com.ngaso.Ngaso.Models.entites.Administrateur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataInitializer {

    @Value("${APP_ADMIN_EMAIL:admin@ngaso.com}")
    private String email;

    @Value("${APP_ADMIN_PASSWORD:adminngaso123}")
    private String password;

    @Bean
    public CommandLineRunner initAdmin(AdministrateurRepository administrateurRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                System.err.println("[DataInitializer] APP_ADMIN_EMAIL ou APP_ADMIN_PASSWORD non défini. Seed admin ignoré.");
                return;
            }
            String normalized = email.trim();
            var existingOpt = administrateurRepository.findByEmailIgnoreCase(normalized);
            if (existingOpt.isEmpty()) {
                Administrateur admin = new Administrateur();
                admin.setNom("Admin");
                admin.setEmail(normalized);
                admin.setPassword(passwordEncoder.encode(password));
                administrateurRepository.save(admin);
                System.out.println("[DataInitializer] Administrateur créé: " + normalized);
            } else {
                Administrateur admin = existingOpt.get();
                // Si le mot de passe ne correspond pas à la valeur configurée, on met à jour
                if (!passwordEncoder.matches(password, admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode(password));
                    administrateurRepository.save(admin);
                    System.out.println("[DataInitializer] Mot de passe administrateur mis à jour pour: " + admin.getEmail());
                }
            }
        };
    }
}
