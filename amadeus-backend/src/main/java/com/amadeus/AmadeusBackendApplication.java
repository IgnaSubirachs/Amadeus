package com.amadeus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Amadeus Backend
 * 
 * @SpringBootApplication activa:
 * - @Configuration: Configuración de beans
 * - @EnableAutoConfiguration: Configuración automática de Spring Boot
 * - @ComponentScan: Escaneo de componentes en este paquete y subpaquetes
 */
@SpringBootApplication
public class AmadeusBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmadeusBackendApplication.class, args);
    }
}
