package org.pinguin.gf.service.api.account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

	AccountTO createAccount(AccountTO account);

	AccountTO updateAccount(Long id, AccountTO account);

	AccountTO deleteAccount(Long id);

	AccountTO retrieveById(Long id);

	List<AccStatementEntryTO> retrieveStatements(Long id);

	/**
	 * TODO: parametros.
	 * 
	 * @param sort
	 * @param page
	 * @param pageSize
	 * @param fields
	 * @return
	 */
	List<AccountTO> retrieveAll(Optional<String> filters, Optional<String> sort, Optional<String> page,
			Optional<String> pageSize, Optional<String> fields);

	/**
	 * Retorna as contas analiticas.
	 * 
	 * @return
	 */
	List<AccountTO> retrieveAnalSytical();

}