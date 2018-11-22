package org.pinguin.gf.app;

import org.pinguin.gf.app.service.AccountServiceProxy;
import org.pinguin.gf.service.api.account.AccountService;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountService.class).to(AccountServiceProxy.class);
	}

}
