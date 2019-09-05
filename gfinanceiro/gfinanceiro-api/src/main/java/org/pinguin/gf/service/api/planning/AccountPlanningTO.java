package org.pinguin.gf.service.api.planning;

import java.math.BigDecimal;

import org.pinguin.gf.service.api.account.AccountTO;

public class AccountPlanningTO {
	private Long accPlanId;
	private AccountTO account;
	private BigDecimal value;
	private String comment;

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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "AccountPlanningTO [accPlanId=" + accPlanId + ", account=" + account + ", value=" + value + ", comment="
				+ comment + "]";
	}
	
	

}
