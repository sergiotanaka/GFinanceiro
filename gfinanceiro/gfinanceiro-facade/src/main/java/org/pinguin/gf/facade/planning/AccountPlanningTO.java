package org.pinguin.gf.facade.planning;

import java.math.BigDecimal;

import org.pinguin.gf.facade.account.AccountTO;

public class AccountPlanningTO {

	private Long id;
	private AccountTO account;
	private BigDecimal value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountTO getAccount() {
		return account;
	}

	public void setAccount(AccountTO account) {
		this.account = account;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
