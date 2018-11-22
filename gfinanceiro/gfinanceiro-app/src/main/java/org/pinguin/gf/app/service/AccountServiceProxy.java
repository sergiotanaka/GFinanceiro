package org.pinguin.gf.app.service;

import java.util.List;
import java.util.Optional;

import org.pinguin.gf.service.api.account.AccStatementEntryTO;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccountServiceProxy implements AccountService {

	@Override
	public AccountTO createAccount(AccountTO account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountTO updateAccount(Long id, AccountTO account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountTO deleteAccount(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountTO retrieveById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccStatementEntryTO> retrieveStatements(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountTO> retrieveAll(Optional<String> filters, Optional<String> sort, Optional<String> page,
			Optional<String> pageSize, Optional<String> fields) {
		RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl = "http://localhost:8080/gf/accounts";
		List<AccountTO> foo = (List<AccountTO>) restTemplate.getForObject(fooResourceUrl + "/1", AccountTO.class);
		return foo;
	}

	@Override
	public List<AccountTO> retrieveAnalytical() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountTO> retrieveIncomeAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountTO> retrieveExpenseAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

}
