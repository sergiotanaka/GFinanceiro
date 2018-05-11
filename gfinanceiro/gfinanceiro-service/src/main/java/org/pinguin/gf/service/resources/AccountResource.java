package org.pinguin.gf.service.resources;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pinguin.gf.service.api.account.AccountTO;

import io.dropwizard.jersey.params.LongParam;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

	@POST
	public AccountTO createAccount(AccountTO account) {
		return null;
	}

	@PUT
	@Path("/{accountId}")
	public AccountTO updateAccount(@PathParam("accountId") LongParam accountId, AccountTO account) {
		return account;
	}

	@DELETE
	@Path("/{accountId}")
	public AccountTO deleteAccount(@PathParam("accountId") LongParam accountId) {
		return null;
	}

	@GET
	@Path("/{accountId}")
	public AccountTO retrieveById(@PathParam("accountId") LongParam accountId) {
		AccountTO acc = new AccountTO();
		acc.setId(1L);
		acc.setName("Conta");
		return acc;
	}

	/**
	 * TODO: parametros.
	 * 
	 * @return
	 */
	@GET
	public List<AccountTO> retrieveAll() {
		return null;
	}
}
