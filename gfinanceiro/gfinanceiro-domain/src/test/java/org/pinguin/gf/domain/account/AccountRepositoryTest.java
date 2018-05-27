package org.pinguin.gf.domain.account;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository repo;

	@Test
	public void testCreateAccount() {

		final Account account = new Account("Caixa", AccountNature.CREDIT);

		repo.save(account);

		final List<Account> all = repo.findAll();

		Assert.assertEquals(1, all.size());

		System.out.println(all.get(0));

	}

	@Test
	public void testRetrieveByParameters() {

		final Account account = new Account("Caixa", AccountNature.CREDIT);

		repo.save(account);

		for (final Account item : repo.findAll(QAccount.account.name.eq("Caixa"))) {
			System.out.println(item);
		}
	}

}
