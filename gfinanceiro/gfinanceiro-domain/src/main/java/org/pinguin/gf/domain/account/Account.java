package org.pinguin.gf.domain.account;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long accountId;
	private String name;
	@Enumerated(EnumType.STRING)
	private AccountNature nature;
	@OneToOne(fetch = FetchType.EAGER)
	private Account parent;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> tags = new HashSet<>();

	public Account() {
		super();
	}

	public Account(String name, AccountNature nature) {
		super();
		this.name = name;
		this.nature = nature;
	}

	public Account(String name, AccountNature nature, Account parent) {
		super();
		this.name = name;
		this.nature = nature;
		this.parent = parent;
	}

	public Account(String name, AccountNature nature, Account parent, String... tag) {
		super();
		this.name = name;
		this.nature = nature;
		this.parent = parent;
		this.tags.addAll(new HashSet<>(Arrays.asList(tag)));
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

	public AccountNature getNature() {
		return nature;
	}

	public void setNature(AccountNature nature) {
		this.nature = nature;
	}

	public Account getParent() {
		return parent;
	}

	public void setParent(Account parent) {
		this.parent = parent;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nature == null) ? 0 : nature.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nature != other.nature)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", name=" + name + ", nature=" + nature + ", parent=" + parent
				+ ", tags=" + tags + "]";
	}

}
