package org.pinguin.gf.domain.account;

import javax.persistence.EntityManager;

public class BasicAccountsRepository {

	// @Inject
	private EntityManager entityManager;

	public BasicAccounts retrieve() {
		BasicAccounts found = entityManager.find(BasicAccounts.class, 1L);
		if (found == null) {
			found = new BasicAccounts();
			entityManager.persist(found);
		}
		return found;
	}

	public BasicAccounts update(BasicAccounts accounts) {
		BasicAccounts merged = entityManager.merge(accounts);
		return merged;
	}

}
