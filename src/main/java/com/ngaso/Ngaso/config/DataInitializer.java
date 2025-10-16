package com.ngaso.Ngaso.config;

import com.ngaso.Ngaso.DAO.AdministrateurRepository;
import com.ngaso.Ngaso.Models.entites.Administrateur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataInitializer {

    @Value("${APP_ADMIN_EMAIL}")
    private String email;

    @Value("${APP_ADMIN_PASSWORD}")
    private String password;

    @Bean
    public CommandLineRunner initAdmin(AdministrateurRepository administrateurRepository) {
        return args -> {
            if (!administrateurRepository.existsByEmail(email)) {
                Administrateur admin = new Administrateur();
                admin.setNom("Admin");
                admin.setEmail(email);
                admin.setPassword(new BCryptPasswordEncoder().encode(password));
                administrateurRepository.save(admin);
            }
        };
    }
}
