package com.amadeus.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Seguridad
 * 
 * @Configuration - Marca como clase de configuración Spring
 * @EnableWebSecurity - Activa configuración de seguridad web
 * 
 *                    ¿Por qué BCrypt?
 *                    - Algoritmo de hashing seguro y lento (resistente a fuerza
 *                    bruta)
 *                    - Salt automático (cada hash es único)
 *                    - Configuración de "strength" (complejidad)
 * 
 *                    Strength 10 = 2^10 = 1024 iteraciones (balance
 *                    seguridad/rendimiento)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean de PasswordEncoder para hashear passwords
     * 
     * Se inyecta automáticamente donde se necesite
     * Ejemplo de uso: passwordEncoder.encode("plainPassword")
     * 
     * @return BCryptPasswordEncoder con strength 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configuración de seguridad HTTP
     * 
     * TEMPORAL: Deshabilitamos seguridad para poder probar endpoints
     * TODO: Implementar JWT authentication
     * 
     * @param http HttpSecurity builder
     * @return SecurityFilterChain configurado
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para API REST
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir todas las peticiones (TEMPORAL)
                );

        return http.build();
    }
}
