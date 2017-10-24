package com.alen.lucene;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


//@SpringBootApplication注解相当于使用@Configuration，@EnableAutoConfiguration和@ComponentScan和他们的默认属性：
@SpringBootApplication
public class LuceneApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuceneApplication.class, args);
	}
}
