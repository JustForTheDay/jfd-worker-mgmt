package com.jfd.worker.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for the Worker Registration service.
 * This application acts as a REST API, persists data to MongoDB, and produces messages to Kafka.
 * It's enabled for service discovery and configuration refresh.
 */
@SpringBootApplication
@EnableDiscoveryClient // Enables service registration and discovery (e.g., with Eureka)
@RefreshScope // Allows configuration to be refreshed at runtime (e.g., from Spring Cloud Config Server)
@EnableAsync
public class WorkerMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerMgmtApplication.class, args);
	}

}
