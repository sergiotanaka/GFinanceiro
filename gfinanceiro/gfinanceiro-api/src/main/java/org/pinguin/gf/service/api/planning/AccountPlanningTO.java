package org.pinguin.gf.service.api.planning;

import java.math.BigDecimal;

import org.pinguin.gf.service.api.account.AccountTO;

public class AccountPlanningTO {
	private Long accPlanId;
	private AccountTO account;
	private BigDecimal value;

	public Long getAccPlanId() {
		return accPlanId;
	}

	public void setAccPlanId(Long accPlanId) {
		this.accPlanId = accPlanId;
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
