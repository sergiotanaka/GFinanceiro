package org.pinguin.gf.service;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.account.BasicAccounts;
import org.pinguin.gf.domain.account.BasicAccountsRepository;
import org.pinguin.gf.domain.common.impl.CustomQueryDslJpaRepositoryImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.pinguin.gf.domain", repositoryBaseClass = CustomQueryDslJpaRepositoryImpl.class)
@EntityScan("org.pinguin.gf.domain")
@SpringBootApplication(scanBasePackages = "org.pinguin.gf")
public class GFinanceiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(GFinanceiroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            final AccountRepository repo = ctx.getBean(AccountRepository.class);

            // Contas basicas
            final Account asset = repo.save(new Account("Ativo", AccountNature.CREDIT));
            final Account liability = repo.save(new Account("Passivo", AccountNature.DEBIT));
            final Account income = repo.save(new Account("Receita", AccountNature.DEBIT));
            final Account expense = repo.save(new Account("Despesa", AccountNature.DEBIT));
            final Account capital = repo.save(new Account("Capital", AccountNature.DEBIT));
            // Ativos
            repo.save(new Account("Caixa", AccountNature.CREDIT, asset));
            repo.save(new Account("C/C Santander", AccountNature.CREDIT, asset));
            repo.save(new Account("Poupança Santander", AccountNature.CREDIT, asset));
            repo.save(new Account("Investimento - GP", AccountNature.CREDIT, asset));
            repo.save(new Account("Investimento - CDB", AccountNature.CREDIT, asset));
            // Passivo
            repo.save(new Account("C. Crédito Santander", AccountNature.CREDIT, liability));
            // Receitas
            repo.save(new Account("Salário", AccountNature.CREDIT, income));
            repo.save(new Account("Renda investimento - GP", AccountNature.CREDIT, income));
            repo.save(new Account("Renda investimento - CDB", AccountNature.CREDIT, income));
            repo.save(new Account("Outras receitas", AccountNature.CREDIT, income));
            // Despesas
            repo.save(new Account("Contas residenciais", AccountNature.CREDIT, expense));
            repo.save(new Account("Mercado", AccountNature.CREDIT, expense));
            repo.save(new Account("Moradia", AccountNature.CREDIT, expense));
            repo.save(new Account("Saúde", AccountNature.CREDIT, expense));
            repo.save(new Account("Transporte", AccountNature.CREDIT, expense));
            repo.save(new Account("Bares / Restaurantes", AccountNature.CREDIT, expense));
            repo.save(new Account("Compras", AccountNature.CREDIT, expense));
            repo.save(new Account("Cuidados pessoais", AccountNature.CREDIT, expense));
            repo.save(new Account("Impostos / Taxas", AccountNature.CREDIT, expense));
            repo.save(new Account("Lazer", AccountNature.CREDIT, expense));
            repo.save(new Account("Presentes / Doações", AccountNature.CREDIT, expense));
            repo.save(new Account("TV / Internet / Telefonia", AccountNature.CREDIT, expense));
            
            final BasicAccounts ba = new BasicAccounts();
            ba.setAsset(asset);
            ba.setLiability(liability);
            ba.setIncome(income);
            ba.setExpense(expense);
            ba.setCapital(capital);
            
            final BasicAccountsRepository baRepo = ctx.getBean(BasicAccountsRepository.class);
            baRepo.save(ba);
        };
    }

}
