package org.pinguin.gf.service.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BasicAccountsTO {
	private Long id = 1L;
	private AccountTO asset;
	private AccountTO liability;
	private AccountTO income;
	private AccountTO expense;
	private AccountTO capital;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountTO getAsset() {
		return asset;
	}

	public void setAsset(AccountTO asset) {
		this.asset = asset;
	}

	public AccountTO getLiability() {
		return liability;
	}

	public void setLiability(AccountTO liability) {
		this.liability = liability;
	}

	public AccountTO getIncome() {
		return income;
	}

	public void setIncome(AccountTO income) {
		this.income = income;
	}

	public AccountTO getExpense() {
		return expense;
	}

	public void setExpense(AccountTO expense) {
		this.expense = expense;
	}

	public AccountTO getCapital() {
		return capital;
	}

	public void setCapital(AccountTO capital) {
		this.capital = capital;
	}

	@Override
	public String toString() {
		return "BasicAccountsTO [id=" + id + ", asset=" + asset + ", liability=" + liability + ", income=" + income
				+ ", expense=" + expense + ", capital=" + capital + "]";
	}

}
