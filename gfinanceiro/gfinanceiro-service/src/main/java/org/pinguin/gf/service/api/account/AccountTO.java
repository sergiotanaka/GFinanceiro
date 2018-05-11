package org.pinguin.gf.service.api.account;

public class AccountTO {

	private Long id;
	private String name;
	private AccountNatureTO nature;
	private AccountTO parent;

	public AccountTO() {
	}

	public AccountTO(String name, AccountNatureTO nature) {
		this.name = name;
		this.nature = nature;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return "AccountTO [id=" + id + ", name=" + name + ", nature=" + nature + "]";
	}

}
