package org.example.credit_processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CreditProcessingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreditProcessingApplication.class, args);
    }
}
