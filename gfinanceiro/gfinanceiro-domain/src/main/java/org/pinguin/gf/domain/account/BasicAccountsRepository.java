package org.pinguin.gf.domain.account;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;

@Transactional(rollbackOn = { Exception.class, RuntimeException.class })
public class BasicAccountsRepository {

	@Inject
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
