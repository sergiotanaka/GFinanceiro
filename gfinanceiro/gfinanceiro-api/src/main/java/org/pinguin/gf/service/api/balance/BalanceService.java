package org.pinguin.gf.service.api.balance;

import java.time.LocalDate;
import java.util.List;

public interface BalanceService {

	List<BalanceTO> retrieveBalance(LocalDate start, LocalDate end);

}