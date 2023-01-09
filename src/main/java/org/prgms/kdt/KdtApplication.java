package org.prgms.kdt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@ComponentScan(basePackages = {"org.prgms.kdt.customer","org.prgms.kdt.config"})
public class KdtApplication {
	public static void main(String[] args) {
		SpringApplication.run(KdtApplication.class, args);
	}


}
