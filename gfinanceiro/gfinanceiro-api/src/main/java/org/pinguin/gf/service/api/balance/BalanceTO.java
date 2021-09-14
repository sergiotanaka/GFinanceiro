package org.pinguin.gf.service.api.balance;

import java.math.BigDecimal;

import org.pinguin.gf.service.api.account.AccountTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceTO {
	private AccountTO account;
	private BigDecimal credits = BigDecimal.ZERO;
	private BigDecimal debits = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;

	public BalanceTO(AccountTO accountTO) {
		this.account = accountTO;
	}

}
