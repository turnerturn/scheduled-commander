package com.turnerturn.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/*
curl --location --request POST 'http://localhost:8080/actuator/restart'
*/

@SpringBootApplication
public class SandboxApplication {
    public static void main(String[] args) {
		SpringApplication.run(SandboxApplication.class, args);
    }
}
@EnableScheduling
@Configuration
class SandboxApplicationConfiguration {
    
	@Bean
	public RestartEndpoint restartEndpoint() {
		return new RestartEndpoint();
	}
	@Bean
	public ThreadPoolTaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		scheduler.setThreadNamePrefix("task-scheduler-");
		scheduler.initialize();
		return scheduler;
	}
}