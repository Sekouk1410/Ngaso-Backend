package com.ngaso.Ngaso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.ngaso.Ngaso.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register/novice").permitAll()
                .requestMatchers("/auth/register/professionnel").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/health/**").permitAll()
                // Password change (Novice & Professionnel uniquement)
                .requestMatchers("/auth/change-password").hasAnyRole("Novice", "Professionnel")
                // Swagger/OpenAPI docs
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Public listing of specialites (front needs it without auth)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/admin/specialites").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // Admin scope
                .requestMatchers("/admin/**").hasRole("Admin")
                // Professionnel scope
                .requestMatchers("/professionnels/**").hasRole("Professionnel")
                // Projets en cours visible uniquement par les professionnels
                .requestMatchers("/projets/en-cours").hasRole("Professionnel")
                // Novice scope
                .requestMatchers(
                    "/novices/me/propositions",
                    "/novices/me/propositions/*/accepter",
                    "/novices/me/propositions/*/refuser"
                ).hasRole("Novice")
                // Conversations (Novice & Professionnel)
                .requestMatchers(
                    "/conversations/me",
                    "/conversations/*/messages",
                    "/conversations/*/messages/upload"
                ).hasAnyRole("Novice", "Professionnel")
                // Notifications (Novice & Professionnel)
                .requestMatchers(
                    "/notifications/me",
                    "/notifications/me/count",
                    "/notifications/me/read"
                ).hasAnyRole("Novice", "Professionnel")
                // User profile (Novice & Professionnel)
                .requestMatchers("/users/me/photo").hasAnyRole("Novice", "Professionnel")
                    .requestMatchers(
                    "/projets/*/etapes",
                    "/projets/novices/*",
                    "/projets/me",
                    "/projets/*",
                    "/projets/etapes/*/valider",
                    "/projets/etapes/*/professionnels",
                    "/projets/etapes/*/demandes",
                            "/projets/*/demandes",
                            "/projets/demandes/*/annuler").hasRole("Novice")
                // Others must be authenticated
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
