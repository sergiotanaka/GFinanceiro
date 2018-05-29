package org.pinguin.gf.domain.journalentry;

import org.pinguin.gf.domain.common.impl.CustomQueryDslJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.pinguin.gf.domain", repositoryBaseClass = CustomQueryDslJpaRepositoryImpl.class)
@EntityScan("org.pinguin.gf.domain")
@SpringBootApplication(scanBasePackages = "org.pinguin.gf")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}