package org.pinguin.gf.app.service;

import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.balance.BalanceService;
import org.pinguin.gf.service.api.balance.BalanceTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BalanceServiceProxy implements BalanceService {

	private final String balanceResourceUrl = "http://localhost:8080/gf/balance";
	private final ParameterizedTypeReference<List<BalanceTO>> balanceTypeRef = new ParameterizedTypeReference<List<BalanceTO>>() {
	};

	@Inject
	private RestTemplate restTemplate;

	@Override
	public List<BalanceTO> retrieveBalance() {
		final ResponseEntity<List<BalanceTO>> response = restTemplate.exchange(balanceResourceUrl, HttpMethod.GET, null,
				balanceTypeRef);
		return response.getBody();
	}

}
