package org.pinguin.gf.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccStatementEntryTO;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.account.DayResultTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountServiceProxy implements AccountService {

	private final String accountResourceUrl = "http://localhost:8080/gf/accounts";
	private final ParameterizedTypeReference<List<AccountTO>> accListTypeRef = new ParameterizedTypeReference<List<AccountTO>>() {
	};
	private final ParameterizedTypeReference<List<AccStatementEntryTO>> accStateListTypeRef = new ParameterizedTypeReference<List<AccStatementEntryTO>>() {
	};
	private final ParameterizedTypeReference<List<DayResultTO>> dayResultListTypeRef = new ParameterizedTypeReference<List<DayResultTO>>() {
	};

	@Inject
	private RestTemplate restTemplate;

	@Override
	public AccountTO createAccount(AccountTO account) {
		return restTemplate.postForObject(accountResourceUrl, new HttpEntity<>(account), AccountTO.class);
	}

	@Override
	public AccountTO updateAccount(Long id, AccountTO account) {
		String resourceUrl = accountResourceUrl + '/' + id;
		ResponseEntity<AccountTO> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT,
				new HttpEntity<>(account), AccountTO.class);
		return response.getBody();
	}

	@Override
	public AccountTO deleteAccount(Long id) {
		String entityUrl = accountResourceUrl + "/" + id;
		restTemplate.delete(entityUrl);
		return null;
	}

	@Override
	public AccountTO retrieveById(Long id) {
		return restTemplate.getForObject(accountResourceUrl + "/" + id, AccountTO.class);
	}

	@Override
	public List<AccStatementEntryTO> retrieveStatements(Long id, LocalDate start, LocalDate end,
			boolean periodBalance) {
		String resourceUrl = accountResourceUrl + '/' + id + "/statements";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl).queryParam("start", start)
				.queryParam("end", end).queryParam("periodBalance", periodBalance);
		final ResponseEntity<List<AccStatementEntryTO>> response = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET, null, accStateListTypeRef);
		return response.getBody();
	}

	@Override
	public List<AccountTO> retrieveAll(Optional<String> filters, Optional<String> sort, Optional<String> page,
			Optional<String> pageSize, Optional<String> fields) {
		final ResponseEntity<List<AccountTO>> response = restTemplate.exchange(accountResourceUrl, HttpMethod.GET, null,
				accListTypeRef);
		return response.getBody();
	}

	@Override
	public List<AccountTO> retrieveAnalytical() {
		final ResponseEntity<List<AccountTO>> response = restTemplate.exchange(accountResourceUrl + "/analytical",
				HttpMethod.GET, null, accListTypeRef);
		return response.getBody();
	}

	@Override
	public List<AccountTO> retrieveIncomeAccounts() {
		final ResponseEntity<List<AccountTO>> response = restTemplate.exchange(accountResourceUrl + "/income",
				HttpMethod.GET, null, accListTypeRef);
		return response.getBody();
	}

	@Override
	public List<AccountTO> retrieveExpenseAccounts() {
		final ResponseEntity<List<AccountTO>> response = restTemplate.exchange(accountResourceUrl + "/expenses",
				HttpMethod.GET, null, accListTypeRef);
		return response.getBody();
	}

	@Override
	public List<DayResultTO> retrieveCashFlow(Long id, LocalDate start, LocalDate end) {
		String resourceUrl = accountResourceUrl + '/' + id + "/cashflow";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl).queryParam("start", start)
				.queryParam("end", end);
		final ResponseEntity<List<DayResultTO>> response = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET, null, dayResultListTypeRef);
		return response.getBody();
	}

}
