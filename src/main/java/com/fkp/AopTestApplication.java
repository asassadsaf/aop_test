package com.fkp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan(basePackages = "com.fkp.mapper")
public class AopTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AopTestApplication.class, args);
	}

}
