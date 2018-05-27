package org.pinguin.gf.service.api.account;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.service.infra.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.xebia.extras.selma.Selma;

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
		mapper = Selma.mapper(AccountMapper.class);
	}

	@PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public AccountTO createAccount(@Valid @RequestBody AccountTO account) {

		Account saved = repo.save(mapper.asEntity(account));

		AccountTO response = mapper.asTO(saved);
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

		return response;
	}

	@PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO updateAccount(@PathVariable("id") Long id, @Valid @RequestBody AccountTO account) {

		account.setAccountId(id);
		Account saved = repo.save(mapper.asEntity(account));

		AccountTO response = mapper.asTO(saved);
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

		return response;
	}

	@DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO deleteAccount(@PathVariable("id") Long id) {

		Optional<Account> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		AccountTO response = mapper.asTO(found.get());
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());
		return response;
	}

	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO retrieveById(@PathVariable("id") Long id) {
		Optional<Account> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		AccountTO response = mapper.asTO(found.get());
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());
		return response;
	}

	/**
	 * TODO: parametros.
	 * 
	 * @return
	 */
	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveAll(@RequestParam(value = "filters", required = false) String filters) {
		System.out.println(filters);
		List<AccountTO> list = new ArrayList<>();
		for (Account entity : repo.findAll()) {
			AccountTO to = mapper.asTO(entity);
			to.add(linkTo(AccountController.class).slash(to.getAccountId()).withSelfRel());
			list.add(to);
		}
		return list;
	}
}
