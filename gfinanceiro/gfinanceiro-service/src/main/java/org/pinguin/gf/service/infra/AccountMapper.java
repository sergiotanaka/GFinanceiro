package org.pinguin.gf.service.infra;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.service.api.account.AccountTO;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class AccountMapper extends ResourceAssemblerSupport<Account, AccountTO> {

	public AccountMapper(Class<?> controllerClass, Class<AccountTO> resourceType) {
		super(controllerClass, resourceType);
	}

	@Override
	public AccountTO toResource(Account entity) {
		return createResourceWithId(entity.getId(), entity);
	}

}
