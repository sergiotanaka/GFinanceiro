package org.pinguin.gf.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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
			
//			final EntityManagerFactory emf = ctx.getBean(EntityManagerFactory.class);
//			final EntityManager em = emf.createEntityManager();
//
//			em.getTransaction().begin();
//			final Query qry1 = em.createNativeQuery("DROP TABLE IF EXISTS AccountPlanning CASCADE;");
//			qry1.executeUpdate();
//			Query qry2 = em.createNativeQuery("DROP TABLE IF EXISTS Planning CASCADE;");
//			qry2.executeUpdate();
//			em.getTransaction().commit();

			final AccountRepository repo = ctx.getBean(AccountRepository.class);

			if (!repo.findAll().isEmpty()) {
				return;
			}

			// Contas basicas
			final Account asset = repo.save(new Account("Ativo", AccountNature.DEBIT));
			final Account liability = repo.save(new Account("Passivo", AccountNature.CREDIT));
			final Account income = repo.save(new Account("Receita", AccountNature.CREDIT));
			final Account expense = repo.save(new Account("Despesa", AccountNature.DEBIT));
			final Account capital = repo.save(new Account("Capital", AccountNature.CREDIT));
			// Ativos
			repo.save(new Account("Caixa", AccountNature.DEBIT, asset));
			repo.save(new Account("C/C Santander", AccountNature.DEBIT, asset));
			repo.save(new Account("Poupança Santander", AccountNature.DEBIT, asset));
			repo.save(new Account("Investimento - GP", AccountNature.DEBIT, asset));
			repo.save(new Account("Investimento - CDB", AccountNature.DEBIT, asset));
			// Passivo
			repo.save(new Account("C. Crédito Santander", AccountNature.CREDIT, liability));
			// Receitas
			repo.save(new Account("Salário", AccountNature.CREDIT, income, "LIQUIDO DE VENCIMENTO"));
			repo.save(new Account("Renda investimento - GP", AccountNature.CREDIT, income));
			repo.save(new Account("Renda investimento - CDB", AccountNature.CREDIT, income));
			repo.save(new Account("Outras receitas", AccountNature.CREDIT, income));
			// Despesas
			repo.save(new Account("Contas residenciais", AccountNature.DEBIT, expense, "INTERNET COMGAS CIA DE GA",
					"INTERNET AES ELETROPAULO"));
			repo.save(new Account("Mercado", AccountNature.DEBIT, expense, "QUITANDA BURITI", "PAO DE ACUCAR"));
			repo.save(new Account("Moradia", AccountNature.DEBIT, expense, "CONDOMINIO EDIFICIOS C CO"));
			repo.save(new Account("Saúde", AccountNature.DEBIT, expense, "MAYARA J F SILVA", "DIEGO FREITAS TAVARES",
					"DROG SAO PAULO", "STUDIO F F BAST"));
			repo.save(new Account("Transporte", AccountNature.DEBIT, expense, "PRODATA MOBILIT"));
			repo.save(new Account("Bares / Restaurantes", AccountNature.DEBIT, expense, "SANTO TEMAKI", "RAGAZZO"));
			repo.save(new Account("Compras", AccountNature.DEBIT, expense));
			repo.save(new Account("Cuidados pessoais", AccountNature.DEBIT, expense));
			repo.save(new Account("Impostos / Taxas", AccountNature.DEBIT, expense));
			repo.save(new Account("Lazer", AccountNature.DEBIT, expense));
			repo.save(new Account("Presentes / Doações", AccountNature.DEBIT, expense));
			repo.save(new Account("TV / Internet / Telefonia", AccountNature.DEBIT, expense, "CLARO SP"));

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
