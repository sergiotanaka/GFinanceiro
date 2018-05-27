package org.pinguin.gf.domain.journalentry;

import static org.pinguin.gf.domain.journalentry.QJournalEntry.journalEntry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
public class JournalEntryRepositoryTest {
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private JournalEntryRepository repo;

	@Test
	public void testFindJournalEntry() {

		final Account caixa = accRepo.save(new Account("Caixa", AccountNature.CREDIT));
		repo.save(new JournalEntry(caixa, caixa, BigDecimal.valueOf(3.0), LocalDateTime.now(), "teste"));

		Iterable<JournalEntry> it = repo.findAll(journalEntry.creditAccount.name.eq("Caixa"));
		List<JournalEntry> list = new ArrayList<>();
		it.forEach(list::add);
		Assert.assertEquals(1, list.size());
		
		Iterable<JournalEntry> it2 = repo.findAll(journalEntry.creditAccount.name.eq("Banco"));
		List<JournalEntry> list2 = new ArrayList<>();
		it2.forEach(list2::add);
		Assert.assertEquals(0, list2.size());

	}

}
