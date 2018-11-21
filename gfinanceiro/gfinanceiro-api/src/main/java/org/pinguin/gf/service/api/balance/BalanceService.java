package org.pinguin.gf.service.api.balance;

import java.util.List;

import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;

public interface BalanceService {

	List<BalanceTO> retrieveBalance();

}