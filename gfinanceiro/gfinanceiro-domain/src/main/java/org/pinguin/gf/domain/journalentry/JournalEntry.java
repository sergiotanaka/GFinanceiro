package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.pinguin.gf.domain.account.Account;

@Entity
public class JournalEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long entryId;
	/** Destino */
	@OneToOne(fetch = FetchType.EAGER)
	private Account debitAccount;
	/** Origem */
	@OneToOne(fetch = FetchType.EAGER)
	private Account creditAccount;
	private BigDecimal value;
	private LocalDateTime date;
	private Boolean future = false;

	private String description;

	public JournalEntry() {
		super();
	}

	public JournalEntry(Account debitAccount, Account creditAccount, BigDecimal value, LocalDateTime date,
			String description) {
		super();
		this.debitAccount = debitAccount;
		this.creditAccount = creditAccount;
		this.value = value;
		this.date = date;
		this.description = description;
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long id) {
		this.entryId = id;
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

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getFuture() {
		return future;
	}

	public void setFuture(Boolean future) {
		this.future = future;
	}

	@Override
	public String toString() {
		return "JournalEntry [entryId=" + entryId + ", debitAccount=" + debitAccount + ", creditAccount="
				+ creditAccount + ", value=" + value + ", date=" + date + ", future=" + future + ", description="
				+ description + "]";
	}

}
