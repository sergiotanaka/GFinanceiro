package org.pinguin.gf.service.api.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.pinguin.gf.service.api.account.AccountTO;
import org.springframework.hateoas.ResourceSupport;

public class JournalEntryTO extends ResourceSupport {

	private Long entryId;
	private AccountTO debitAccount;
	private AccountTO creditAccount;
	private BigDecimal value;
	private LocalDateTime date;
	private String description;

	public JournalEntryTO() {
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public AccountTO getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(AccountTO debitAccount) {
		this.debitAccount = debitAccount;
	}

	public AccountTO getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(AccountTO creditAccount) {
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

}
