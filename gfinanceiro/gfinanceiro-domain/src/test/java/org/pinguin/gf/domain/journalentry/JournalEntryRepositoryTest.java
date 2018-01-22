package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pinguin.core.domain.Parameter;
import org.pinguin.core.domain.RepositoryBase;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class JournalEntryRepositoryTest {

	private Injector injector = Guice.createInjector(new JpaPersistModule("main"));

	@Before
	public void beforeTest() {
		injector.getInstance(PersistService.class).start();
	}

	@After
	public void afterTest() {
		JournalEntryRepository repo = injector.getInstance(JournalEntryRepository.class);
		AccountRepository accountRepo = injector.getInstance(AccountRepository.class);

		clearEntities(repo);
		clearEntities(accountRepo);
	}

	private <T> void clearEntities(RepositoryBase<T, ?> repo) {
		for (T entity : repo.retrieveAll()) {
			repo.delete(entity);
		}
	}

	@Test
	public void testCreateJournalEntry() {

		JournalEntryRepository repo = injector.getInstance(JournalEntryRepository.class);
		AccountRepository accountRepo = injector.getInstance(AccountRepository.class);

		Account debitAccount = accountRepo.create(new Account("Caixa", AccountNature.CREDIT));
		Account creditAccount = accountRepo.create(new Account("Despesa", AccountNature.DEBIT));
		JournalEntry entry = new JournalEntry(debitAccount, creditAccount, BigDecimal.valueOf(100.0),
				Calendar.getInstance(), "Teste");

		repo.create(entry);

		List<JournalEntry> all = repo.retrieveByQuery("select j from JournalEntry j");

		Assert.assertEquals(1, all.size());

	}

	@Test
	public void testRetrieveByDate() {

		JournalEntryRepository repo = injector.getInstance(JournalEntryRepository.class);
		AccountRepository accountRepo = injector.getInstance(AccountRepository.class);

		Account debitAccount = accountRepo.create(new Account("Caixa", AccountNature.CREDIT));
		Account creditAccount = accountRepo.create(new Account("Despesa", AccountNature.DEBIT));

		Calendar cal = Calendar.getInstance();
		cal.set(2016, 11, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		System.out.println(cal.getTime().toString());
		System.out.println(cal.getTimeInMillis());
		JournalEntry entry = new JournalEntry(debitAccount, creditAccount, BigDecimal.valueOf(100.0), cal, "Teste");

		repo.create(entry);

		Parameter<Calendar> param = new Parameter<Calendar>("date", cal);
		List<JournalEntry> all = repo.retrieveByQuery("select e from JournalEntry e where date >= :date", param);
		Assert.assertEquals(1, all.size());

	}

}
