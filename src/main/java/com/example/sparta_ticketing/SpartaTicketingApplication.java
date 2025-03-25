package com.example.sparta_ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpartaTicketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaTicketingApplication.class, args);
    }

}
