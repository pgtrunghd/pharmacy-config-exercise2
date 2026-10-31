package com.example.pharmacy.service;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PharmacyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacyServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner printBranchAndCheckDatabase(
            DataSource dataSource,
            @Value("${app.branch-name}") String branchName,
            @Value("${app.hotline}") String hotline) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                if (!connection.isValid(2)) {
                    throw new IllegalStateException("PostgreSQL connection is invalid");
                }
                System.out.println("PostgreSQL connected: " + connection.getCatalog());
            }
            System.out.println("Branch: " + branchName);
            System.out.println("Hotline: " + hotline);
        };
    }
}

