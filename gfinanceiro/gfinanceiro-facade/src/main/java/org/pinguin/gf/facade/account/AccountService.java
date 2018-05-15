package org.pinguin.gf.facade.account;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.account.BasicAccounts;
import org.pinguin.gf.domain.account.BasicAccountsRepository;
import org.pinguin.gf.facade.common.ParameterTO;

public class AccountService {

	private final DozerBeanMapper mapper = new DozerBeanMapper();

	private AccountRepository repo;
	private BasicAccountsRepository basicAccRepo;

	public AccountTO createAccount(AccountTO account) {

		Account entity = mapper.map(account, Account.class);

		Account created = null;

		return mapper.map(created, AccountTO.class);
	}

	public AccountTO updateAccount(AccountTO account) {

		Account entity = mapper.map(account, Account.class);

		Account updated = null;

		return mapper.map(updated, AccountTO.class);
	}

	public AccountTO deleteAccount(AccountTO account) {

		Account entity = mapper.map(account, Account.class);

		Account deleted = null;

		return mapper.map(deleted, AccountTO.class);
	}

	public List<AccountTO> retrieveAccounts() {

		List<Account> all = null;

		List<AccountTO> result = new ArrayList<>();
		for (Account entity : all) {
			result.add(mapper.map(entity, AccountTO.class));
		}

		return result;
	}

	private List<Account> retrieveAnalyticalAccounts(Account parent) {
		// List<Account> retrieved = repo.retrieveByQuery("select a from Account a where
		// a.parent = :account",
		// new Parameter<>("account", parent));
		// if (retrieved.isEmpty()) {
		// return new ArrayList<>(Arrays.asList(parent));
		// } else {
		// List<Account> result = new ArrayList<>();
		// for (Account item : retrieved) {
		// result.addAll(retrieveAnalyticalAccounts(item));
		// }
		// return result;
		// }
		return null;
	}

	public List<AccountTO> retrieveAnalyticalAccounts() {

		final BasicAccounts basicAccs = basicAccRepo.retrieve();
		List<Account> roots = new ArrayList<>();
		roots.add(basicAccs.getAsset());
		roots.add(basicAccs.getLiability());
		roots.add(basicAccs.getCapital());
		roots.add(basicAccs.getIncome());
		roots.add(basicAccs.getExpense());

		final List<Account> accs = new ArrayList<>();
		for (Account root : roots) {
			accs.addAll(retrieveAnalyticalAccounts(root));
		}

		final List<AccountTO> result = new ArrayList<>();
		for (Account item : accs) {
			result.add(mapper.map(item, AccountTO.class));
		}

		return result;
	}

	public List<AccountTO> retrieveIncomeAccounts() {
		final BasicAccounts basicAccs = basicAccRepo.retrieve();

		final List<Account> accs = new ArrayList<>();
		accs.addAll(retrieveAnalyticalAccounts(basicAccs.getIncome()));

		final List<AccountTO> result = new ArrayList<>();
		for (Account item : accs) {
			result.add(mapper.map(item, AccountTO.class));
		}

		return result;
	}

	public List<AccountTO> retrieveExpenseAccounts() {
		final BasicAccounts basicAccs = basicAccRepo.retrieve();

		final List<Account> accs = new ArrayList<>();
		accs.addAll(retrieveAnalyticalAccounts(basicAccs.getExpense()));

		final List<AccountTO> result = new ArrayList<>();
		for (Account item : accs) {
			result.add(mapper.map(item, AccountTO.class));
		}

		return result;
	}

	public AccountTO retrieveAccountById(Long id) {
		// return mapper.map(repo.retrieveById(id), AccountTO.class);
		return null;
	}

	public List<AccountTO> retrieveAccountByParams(String jpql, ParameterTO<?>... paramsTO) {

//		Parameter<?>[] params = transform(paramsTO);
//
//		List<Account> retrieved = repo.retrieveByQuery(jpql, params);
//		List<AccountTO> result = new ArrayList<>();
//		for (Account entity : retrieved) {
//			result.add(mapper.map(entity, AccountTO.class));
//		}
//
//		return result;
		return null;
	}

//	private Parameter<?>[] transform(ParameterTO<?>... paramsTO) {
//		Parameter[] params = new Parameter[paramsTO.length];
//		for (int i = 0; i < paramsTO.length; i++) {
//			params[i] = mapper.map(paramsTO[i], Parameter.class);
//		}
//		return params;
//	}

	public BasicAccountsTO retrieveBasicAccounts() {
		return mapper.map(basicAccRepo.retrieve(), BasicAccountsTO.class);
	}

	public BasicAccountsTO updateBasicAccounts(BasicAccountsTO accs) {
		BasicAccounts entity = mapper.map(accs, BasicAccounts.class);
		BasicAccounts updated = basicAccRepo.update(entity);
		return mapper.map(updated, BasicAccountsTO.class);
	}
}
