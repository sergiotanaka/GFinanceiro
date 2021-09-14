package org.pinguin.gf.service.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class BasicAccountsTO {
	private Long id = 1L;
	private AccountTO asset;
	private AccountTO liability;
	private AccountTO income;
	private AccountTO expense;
	private AccountTO capital;
}
