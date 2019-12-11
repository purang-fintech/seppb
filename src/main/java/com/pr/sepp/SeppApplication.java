package com.pr.sepp;

import com.pr.sepp.common.config.factory.DefinitionPropertySourceFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableSwagger2
@EnableAsync
@MapperScan({"com.pr.sepp.**.dao"})
@PropertySource(name = "spring-local", value = "classpath:spring.properties",
		factory = DefinitionPropertySourceFactory.class)
@SpringBootApplication
@EnableTransactionManagement
public class SeppApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeppApplication.class, args);
	}
}
