package com.cqu.wb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MainApplication /* extends SpringBootServletInitializer */ {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

/*
	// 以war包形式在web容器中运行
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MainApplication.class);
	}
*/
}
