package org.pinguin.gf.service.api.balance;

import java.math.BigDecimal;

import org.pinguin.gf.service.api.account.AccountTO;

public class BalanceTO {

	private AccountTO account;
	private BigDecimal credits = BigDecimal.ZERO;
	private BigDecimal debits = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;

	public BalanceTO() {
	}

	public BalanceTO(AccountTO account) {
		this.account = account;
	}

	public AccountTO getAccount() {
		return account;
	}

	public void setAccount(AccountTO account) {
		this.account = account;
	}

	public BigDecimal getCredits() {
		return credits;
	}

	public void setCredits(BigDecimal credits) {
		this.credits = credits;
	}

	public BigDecimal getDebits() {
		return debits;
	}

	public void setDebits(BigDecimal debits) {
		this.debits = debits;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "BalanceTO [account=" + account.getName() + ", balance=" + balance + "]";
	}

}
