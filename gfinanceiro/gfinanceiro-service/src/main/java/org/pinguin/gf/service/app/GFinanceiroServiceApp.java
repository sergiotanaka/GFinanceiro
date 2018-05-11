package org.pinguin.gf.service.app;

import org.pinguin.gf.service.resources.AccountResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GFinanceiroServiceApp extends Application<GFinanceiroServiceConfig> {

	@Override
	public String getName() {
		return "opmet-service";
	}

	@Override
	public void initialize(Bootstrap<GFinanceiroServiceConfig> bootstrap) {
	}

	@Override
	public void run(GFinanceiroServiceConfig config, Environment environment) throws Exception {
		// Registrando resources
		final AccountResource accResource = new AccountResource();

		environment.jersey().register(accResource);
	}

	public static void main(String[] args) throws Exception {
		new GFinanceiroServiceApp().run(args);
	}

}
