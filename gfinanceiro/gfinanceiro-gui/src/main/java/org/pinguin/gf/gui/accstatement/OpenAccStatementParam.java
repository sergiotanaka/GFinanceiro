package org.pinguin.gf.gui.accstatement;

import java.util.Calendar;

import org.pinguin.gf.service.api.account.AccountTO;

import javafx.stage.Window;

public class OpenAccStatementParam {
	private Window owner;
	private AccountTO account;
	private Calendar startDate;
	private Calendar endDate;

	public OpenAccStatementParam() {
	}

	public OpenAccStatementParam(Window owner) {
		this.owner = owner;
	}

	public OpenAccStatementParam(Window owner, AccountTO account, Calendar startDate, Calendar endDate) {
		super();
		this.owner = owner;
		this.account = account;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Window getOwner() {
		return owner;
	}

	public void setOwner(Window owner) {
		this.owner = owner;
	}

	public AccountTO getAccount() {
		return account;
	}

	public void setAccount(AccountTO account) {
		this.account = account;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

}
