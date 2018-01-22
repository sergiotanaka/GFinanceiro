package org.pinguin.gf.domain.account;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BasicAccounts {
	@Id
	private Long id = 1L;
	@OneToOne(fetch = FetchType.EAGER)
	private Account asset;
	@OneToOne(fetch = FetchType.EAGER)
	private Account liability;
	@OneToOne(fetch = FetchType.EAGER)
	private Account income;
	@OneToOne(fetch = FetchType.EAGER)
	private Account expense;
	@OneToOne(fetch = FetchType.EAGER)
	private Account capital;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getAsset() {
		return asset;
	}

	public void setAsset(Account asset) {
		this.asset = asset;
	}

	public Account getLiability() {
		return liability;
	}

	public void setLiability(Account liability) {
		this.liability = liability;
	}

	public Account getIncome() {
		return income;
	}

	public void setIncome(Account income) {
		this.income = income;
	}

	public Account getExpense() {
		return expense;
	}

	public void setExpense(Account expense) {
		this.expense = expense;
	}

	public Account getCapital() {
		return capital;
	}

	public void setCapital(Account capital) {
		this.capital = capital;
	}

}
