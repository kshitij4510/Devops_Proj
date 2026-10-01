package com.agrirent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for Dockerized Farm Equipment Rental System (AgriRent).
 * Runs on Embedded Tomcat on Port 8085.
 */
@SpringBootApplication
public class AgriRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriRentApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("🚜 Farm Equipment Rental System (AgriRent) Started Successfully!");
        System.out.println("🌐 Application URL: http://localhost:8085");
        System.out.println("📊 Health Endpoint: http://localhost:8085/api/health");
        System.out.println("==================================================================");
    }
}
