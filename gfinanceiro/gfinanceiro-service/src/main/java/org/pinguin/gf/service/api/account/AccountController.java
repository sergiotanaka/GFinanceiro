package org.pinguin.gf.service.api.account;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.server.PathParam;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.service.infra.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Trata as requisicoes para as URIs e metodos de Account.
 */
@RestController
@RequestMapping("/gf/accounts")
public class AccountController {

	@Autowired
	private AccountRepository repo;
	private AccountMapper mapper;

	public AccountController() {
		mapper = new AccountMapper(AccountController.class, AccountTO.class);
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaTypes.HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public AccountTO createAccount(AccountTO account) {
		final Account entity = new Account();
		entity.setId(account.getAccountId());
		entity.setName(account.getName());
		return mapper.toResource(repo.save(entity));
	}

	// @Path("/{accountId}")
	public AccountTO updateAccount(@PathParam("accountId") Long accountId, AccountTO account) {
		return account;
	}

	// @Path("/{accountId}")
	public AccountTO deleteAccount(@PathParam("accountId") Long accountId) {
		return null;
	}

	// @Path("/{accountId}")
	public AccountTO retrieveById(@PathParam("accountId") Long accountId) {
		AccountTO acc = new AccountTO();
		acc.setAccountId(accountId);
		acc.setName("Conta");
		return acc;
	}

	/**
	 * TODO: parametros.
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveAll() {
		List<AccountTO> list = new ArrayList<>();
		for (Account entity : repo.findAll()) {
			list.add(mapper.toResource(entity));
		}
		return list;
	}
}
