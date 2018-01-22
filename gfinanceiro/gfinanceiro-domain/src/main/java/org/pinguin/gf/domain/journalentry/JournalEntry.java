package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.pinguin.gf.domain.account.Account;

@Entity
public class JournalEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	/** Destino */
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private Account debitAccount;
	/** Origem */
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private Account creditAccount;
	@NotNull
	private BigDecimal value;
	@NotNull
	private Calendar date;

	private String description;

	public JournalEntry() {
		super();
	}

	public JournalEntry(Account debitAccount, Account creditAccount, BigDecimal value, Calendar date,
			String description) {
		super();
		this.debitAccount = debitAccount;
		this.creditAccount = creditAccount;
		this.value = value;
		this.date = date;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(Account debitAccount) {
		this.debitAccount = debitAccount;
	}

	public Account getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(Account creditAccount) {
		this.creditAccount = creditAccount;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "JournalEntry [id=" + id + ", debitAccount=" + debitAccount + ", creditAccount=" + creditAccount
				+ ", value=" + value + ", date=" + date + ", description=" + description + "]";
	}

}
