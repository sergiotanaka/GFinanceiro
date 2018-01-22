package org.pinguin.gf.domain.account;

import org.pinguin.core.domain.RepositoryBase;

public class AccountRepository extends RepositoryBase<Account, Long> {

    @Override
    protected Class<Account> getEntityType() {
        return Account.class;
    }

}
