package org.pinguin.gf.facade.journalentry;

import java.math.BigDecimal;
import java.util.Calendar;

import org.pinguin.gf.facade.account.AccountTO;

public class AccStatementEntryTO {

	/** Conta de debito / credito */
	private Long id;
	private Calendar date;
	private AccountTO origin;
	private AccountTO account;
	private BigDecimal value;
	private String description;
	private BigDecimal balance;

	public AccStatementEntryTO() {
	}

	public AccStatementEntryTO(Calendar date, AccountTO origin, AccountTO account, BigDecimal value, String description,
			BigDecimal balance) {
		this.date = date;
		this.origin = origin;
		this.account = account;
		this.value = value;
		this.description = description;
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public AccountTO getOrigin() {
		return origin;
	}

	public void setOrigin(AccountTO origin) {
		this.origin = origin;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "AccStatementEntryTO [id=" + id + ", date=" + date + ", origin=" + origin + ", account=" + account
				+ ", value=" + value + ", description=" + description + ", balance=" + balance + "]";
	}

}
