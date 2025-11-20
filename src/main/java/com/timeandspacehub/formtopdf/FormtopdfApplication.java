package com.timeandspacehub.formtopdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FormtopdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormtopdfApplication.class, args);
	}

}
