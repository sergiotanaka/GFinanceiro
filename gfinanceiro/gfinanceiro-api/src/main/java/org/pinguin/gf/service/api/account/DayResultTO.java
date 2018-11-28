package org.pinguin.gf.service.api.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DayResultTO {

	private LocalDate date;
	private BigDecimal result;
	private BigDecimal balance;

	public DayResultTO() {
	}

	public DayResultTO(final LocalDate date) {
		this.date = date;
		result = BigDecimal.ZERO;
		balance = BigDecimal.ZERO;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getResult() {
		return result;
	}

	public void setResult(BigDecimal result) {
		this.result = result;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "DayResultTO [date=" + date + ", result=" + result + ", balance=" + balance + "]";
	}

}
