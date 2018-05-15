package org.pinguin.gf.service.api.account;

import org.springframework.hateoas.ResourceSupport;

public class AccountTO extends ResourceSupport {

	private Long accountId;
	private String name;
	private AccountNatureTO nature;
	private AccountTO parent;

	public AccountTO() {
	}

	public AccountTO(String name, AccountNatureTO nature) {
		this.name = name;
		this.nature = nature;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AccountNatureTO getNature() {
		return nature;
	}

	public void setNature(AccountNatureTO nature) {
		this.nature = nature;
	}

	public AccountTO getParent() {
		return parent;
	}

	public void setParent(AccountTO parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountTO [accountId=");
		builder.append(accountId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", nature=");
		builder.append(nature);
		builder.append(", parent=");
		builder.append(parent);
		builder.append("]");
		return builder.toString();
	}

}
