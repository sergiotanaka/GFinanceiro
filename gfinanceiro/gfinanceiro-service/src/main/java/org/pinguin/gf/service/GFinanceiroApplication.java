package org.pinguin.gf.service;

import java.util.Arrays;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("org.pinguin.gf.domain")
@EntityScan("org.pinguin.gf.domain")
@SpringBootApplication(scanBasePackages = "org.pinguin.gf")
public class GFinanceiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(GFinanceiroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

			AccountRepository repo = ctx.getBean(AccountRepository.class);
			Account entity = new Account();
			entity.setName("Test");
			entity.setNature(AccountNature.CREDIT);
			entity.setParent(null);
			repo.save(entity);
		};
	}

}
