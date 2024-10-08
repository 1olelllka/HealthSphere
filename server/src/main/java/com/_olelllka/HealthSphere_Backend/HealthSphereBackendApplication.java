package com._olelllka.HealthSphere_Backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMethodSecurity
@EnableCaching
public class HealthSphereBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthSphereBackendApplication.class, args);
	}
}
