package org.pinguin.gf.service.api.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DayResultTO {

	private LocalDate date;
	private BigDecimal result;
	private BigDecimal balance;

	public DayResultTO(final LocalDate date) {
		this.date = date;
		result = BigDecimal.ZERO;
		balance = BigDecimal.ZERO;
	}

}
