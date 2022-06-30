package com.batch.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.batch.demo.runner"})
@ComponentScan(basePackages = {"com.batch.demo.config"})
@EnableBatchProcessing
public class SpringBootBatchProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBatchProcessingApplication.class, args);
	}

}
