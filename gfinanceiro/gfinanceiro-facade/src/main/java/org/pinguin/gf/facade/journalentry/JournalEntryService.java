package org.pinguin.gf.facade.journalentry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dozer.DozerBeanMapper;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.account.BasicAccountsRepository;
import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.facade.account.AccountNatureTO;
import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.facade.common.PeriodTO;

public class JournalEntryService {

	private final DozerBeanMapper mapper = new DozerBeanMapper();

	private JournalEntryRepository repo;
	private AccountRepository accRepo;
	private BasicAccountsRepository basicAccRepo;

	public JournalEntryTO createJournalEntry(JournalEntryTO entry) {

		JournalEntry entity = mapper.map(entry, JournalEntry.class);

		JournalEntry created = repo.save(entity);

		return mapper.map(created, JournalEntryTO.class);
	}

	public JournalEntryTO updateJournalEntry(JournalEntryTO entry) {

		JournalEntry entity = mapper.map(entry, JournalEntry.class);

		JournalEntry updated = repo.save(entity);

		return mapper.map(updated, JournalEntryTO.class);
	}

	public JournalEntryTO deleteJournalEntry(JournalEntryTO entry) {

		JournalEntry entity = mapper.map(entry, JournalEntry.class);

		repo.delete(entity);

		return mapper.map(entity, JournalEntryTO.class);
	}

	public List<JournalEntryTO> retrieveJournalEntries() {

		// List<JournalEntry> all = repo.retrieveByQuery("select j from JournalEntry
		// j");
		//
		// List<JournalEntryTO> result = new ArrayList<>();
		// for (JournalEntry entity : all) {
		// result.add(mapper.map(entity, JournalEntryTO.class));
		// }
		//
		// return result;

		return null;
	}

	public JournalEntryTO retrieveJournalEntryById(Long id) {
		JournalEntry entity = repo.findById(id).get();
		return mapper.map(entity, JournalEntryTO.class);
	}

	public List<AccStatementEntryTO> retrieveAccountStatement(AccountTO account, PeriodTO period,
			boolean periodBalance) {

		roundPeriod(period);

		Account parent = accRepo.findById(account.getId()).get();

		List<Account> accs = retrieveAnalyticalAccounts(parent);

		final List<JournalEntry> retrieved = new ArrayList<>();

		if (periodBalance) {
			retrieved.addAll(repo.retrieveByQuery(
					"select j from JournalEntry j where (j.debitAccount in (:debitAccount) or j.creditAccount in (:creditAccount))"
							+ " and date >= :startDate and date <= :endDate order by date",
					new Parameter<>("debitAccount", accs), new Parameter<>("creditAccount", accs),
					new Parameter<>("startDate", period.getStart()), new Parameter<>("endDate", period.getEnd())));
		} else {
			retrieved.addAll(repo.retrieveByQuery(
					"select j from JournalEntry j where (j.debitAccount in (:debitAccount) or j.creditAccount in (:creditAccount)) and date <= :endDate order by date",
					new Parameter<>("debitAccount", accs), new Parameter<>("creditAccount", accs),
					new Parameter<>("endDate", period.getEnd())));
		}

		BigDecimal balance = BigDecimal.ZERO;
		List<AccStatementEntryTO> result = new ArrayList<>();
		for (JournalEntry item : retrieved) {

			AccStatementEntryTO entry = new AccStatementEntryTO();

			entry.setId(item.getId());
			entry.setDate(item.getDate());
			entry.setOrigin(mapper.map(item.getCreditAccount(), AccountTO.class));
			entry.setAccount(mapper.map(item.getDebitAccount(), AccountTO.class));
			if (accs.contains(item.getCreditAccount())) {
				if (parent.getNature().equals(AccountNature.DEBIT)) {
					entry.setValue(item.getValue().multiply(BigDecimal.valueOf(-1.0)).setScale(2));
				} else {
					entry.setValue(item.getValue().setScale(2));
				}
			} else {
				if (parent.getNature().equals(AccountNature.DEBIT)) {
					entry.setValue(item.getValue().setScale(2));
				} else {
					entry.setValue(item.getValue().multiply(BigDecimal.valueOf(-1.0)).setScale(2));
				}
			}
			entry.setDescription(item.getDescription());

			// Calculo do saldo
			balance = balance.add(entry.getValue());
			entry.setBalance(balance.setScale(2));

			if (!item.getDate().before(period.getStart())) {
				result.add(entry);
			}

		}

		return result;
	}

	private List<Account> retrieveAnalyticalAccounts(Account parent) {

		List<Account> retrieved = accRepo.retrieveByQuery("select a from Account a where a.parent = :account");

		if (retrieved.isEmpty()) {
			return new ArrayList<>(Arrays.asList(parent));
		} else {
			List<Account> result = new ArrayList<>();
			for (Account item : retrieved) {
				result.addAll(retrieveAnalyticalAccounts(item));
			}
			return result;
		}
	}

	public List<BalanceTO> retrieveBalance(PeriodTO period) {

		roundPeriod(period);

		List<JournalEntry> retrieved = repo.retrieveByQuery(
				"select j from JournalEntry j where date >= :startDate and date <= :endDate order by date");

		final Map<Account, BalanceTO> balance = new HashMap<>();

		for (JournalEntry entry : retrieved) {
			// Tratar credito
			sumToAccount(entry.getCreditAccount(), entry.getValue(), BigDecimal.ZERO, entry.getValue(), balance);
			// Tratar debito
			sumToAccount(entry.getDebitAccount(), BigDecimal.ZERO, entry.getValue(),
					entry.getValue().multiply(BigDecimal.valueOf(-1.0)), balance);
		}

		List<BalanceTO> result = new ArrayList<>();
		for (Entry<Account, BalanceTO> entry : balance.entrySet()) {
			BalanceTO balanceTO = entry.getValue();
			if (balanceTO.getAccount().getNature().equals(AccountNatureTO.DEBIT)) {
				balanceTO.setBalance(balanceTO.getBalance().multiply(BigDecimal.valueOf(-1.0)));
			}
			balanceTO.setCredits(balanceTO.getCredits().setScale(2));
			balanceTO.setDebits(balanceTO.getDebits().setScale(2));
			balanceTO.setBalance(balanceTO.getBalance().setScale(2));
			result.add(entry.getValue());
		}
		// TODO Adicionar resultado
		// 1. Encontrar a despesa e a receita
		Account income = basicAccRepo.retrieve().getIncome();
		Account expense = basicAccRepo.retrieve().getExpense();
		BigDecimal incomeBalance = balance.containsKey(income) ? balance.get(income).getBalance() : BigDecimal.ZERO;
		BigDecimal expenseBalance = balance.containsKey(expense) ? balance.get(expense).getBalance() : BigDecimal.ZERO;
		BalanceTO balanceResult = new BalanceTO(new AccountTO("Resultado", AccountNatureTO.CREDIT));
		balanceResult.setCredits(expenseBalance);
		balanceResult.setDebits(incomeBalance);
		balanceResult.setBalance(incomeBalance.subtract(expenseBalance));
		result.add(balanceResult);

		return result;

	}

	private void sumToAccount(Account acc, BigDecimal credit, BigDecimal debit, BigDecimal balance,
			Map<Account, BalanceTO> balanceMap) {
		if (!balanceMap.containsKey(acc)) {
			balanceMap.put(acc, new BalanceTO(mapper.map(acc, AccountTO.class)));
		}
		BalanceTO balanceTO = balanceMap.get(acc);
		balanceTO.setCredits(balanceTO.getCredits().add(credit));
		balanceTO.setDebits(balanceTO.getDebits().add(debit));
		balanceTO.setBalance(balanceTO.getBalance().add(balance));

		if (acc.getParent() != null) {
			sumToAccount(acc.getParent(), credit, debit, balance, balanceMap);
		}
	}

	private void roundPeriod(PeriodTO period) {
		cleanHours(period.getStart());
		cleanHours(period.getEnd());
		period.getEnd().add(Calendar.DAY_OF_MONTH, 1);
		period.getEnd().add(Calendar.SECOND, -1);
	}

	private void cleanHours(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
}
