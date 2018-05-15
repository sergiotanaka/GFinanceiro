package org.pinguin.gf.service.infra;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.service.api.account.AccountTO;

import fr.xebia.extras.selma.Mapper;

@Mapper(withIgnoreFields = { "org.pinguin.gf.service.api.account.AccountTO.id",
		"org.pinguin.gf.service.api.account.AccountTO.links" })
public interface AccountMapper {

	AccountTO asTO(Account acc);

	Account asEntity(AccountTO to);
}
