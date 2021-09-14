package org.pinguin.gf.service.api.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccStatementEntryTO {

	/** Conta de debito / credito */
	private Long id;
	private LocalDateTime date;
	private AccountTO origin;
	private AccountTO account;
	private BigDecimal value;
	private String description;
	private BigDecimal balance;
	private Boolean future;

	public AccStatementEntryTO(LocalDateTime date, AccountTO origin, AccountTO account, BigDecimal value,
			String description, BigDecimal balance, Boolean future) {
		this.date = date;
		this.origin = origin;
		this.account = account;
		this.value = value;
		this.description = description;
		this.balance = balance;
		this.future = future;
	}
}
