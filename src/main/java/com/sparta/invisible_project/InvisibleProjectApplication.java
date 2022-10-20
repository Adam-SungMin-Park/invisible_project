package com.sparta.invisible_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InvisibleProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvisibleProjectApplication.class, args);
    }

}
