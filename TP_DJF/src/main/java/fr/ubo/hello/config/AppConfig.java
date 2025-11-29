package fr.ubo.hello.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "fr.ubo.hello")
public class AppConfig {
    // Configuration générale de l'application
}