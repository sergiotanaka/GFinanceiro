package org.pinguin.gf.service.api.account;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AccountTO {

	private Long accountId;
	private String name;
	private AccountNatureTO nature;
	private AccountTO parent;
	private List<String> tags = new ArrayList<>();

	public AccountTO(String name, AccountNatureTO nature) {
		this.name = name;
		this.nature = nature;
	}
}
