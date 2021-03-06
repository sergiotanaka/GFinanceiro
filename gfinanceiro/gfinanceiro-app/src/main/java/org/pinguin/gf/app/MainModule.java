package org.pinguin.gf.app;

import org.pinguin.gf.app.service.AccountServiceProxy;
import org.pinguin.gf.app.service.BalanceServiceProxy;
import org.pinguin.gf.app.service.JournalEntryServiceProxy;
import org.pinguin.gf.app.service.PlanningServiceProxy;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.balance.BalanceService;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.planning.PlanningService;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountService.class).to(AccountServiceProxy.class);
		bind(JournalEntryService.class).to(JournalEntryServiceProxy.class);
		bind(BalanceService.class).to(BalanceServiceProxy.class);
		bind(PlanningService.class).to(PlanningServiceProxy.class);
	}

}
