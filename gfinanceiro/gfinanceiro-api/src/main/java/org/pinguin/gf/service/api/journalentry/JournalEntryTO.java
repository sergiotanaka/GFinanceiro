package org.pinguin.gf.service.api.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.pinguin.gf.service.api.account.AccountTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class JournalEntryTO {
	private Long entryId;
	private AccountTO debitAccount;
	private AccountTO creditAccount;
	private BigDecimal value;
	private LocalDateTime date;
	private String description;
	private Boolean future;
}
