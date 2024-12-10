package com.thyme.ai.thymeai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@SpringBootApplication
@EnableScheduling
public class ThymeAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThymeAiApplication.class, args);
	}

}
