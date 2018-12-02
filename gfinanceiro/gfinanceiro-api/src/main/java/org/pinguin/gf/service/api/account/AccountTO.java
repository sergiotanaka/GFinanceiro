package org.pinguin.gf.service.api.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AccountTO extends ResourceSupport {

	private Long accountId;
	private String name;
	private AccountNatureTO nature;
	private AccountTO parent;
	private List<String> tags = new ArrayList<>();

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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "AccountTO [accountId=" + accountId + ", name=" + name + ", nature=" + nature + ", parent=" + parent
				+ ", tags=" + tags + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountTO other = (AccountTO) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		return true;
	}

}
