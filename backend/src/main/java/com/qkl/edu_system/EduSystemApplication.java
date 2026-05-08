package com.qkl.edu_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.qkl.edu_system.mapper")
public class EduSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(EduSystemApplication.class, args);
	}
}