package org.pinguin.gf.domain.account;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class AccountRepositoryTest {

    @Test
    public void testCreateJournalEntry() {

        Injector injector = Guice.createInjector(new JpaPersistModule("main"));
        injector.getInstance(PersistService.class).start();
        AccountRepository repo = injector.getInstance(AccountRepository.class);

        Account account = new Account("Caixa", AccountNature.CREDIT);

        repo.create(account);

        List<Account> all = repo.retrieveByQuery("select a from Account a");

        Assert.assertEquals(1, all.size());

        System.out.println(all.get(0));

    }

}
