package org.pinguin.gf.service.api.account;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.pinguin.gf.domain.account.QAccount.account;
import static org.pinguin.gf.domain.journalentry.QJournalEntry.journalEntry;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.account.BasicAccounts;
import org.pinguin.gf.domain.account.BasicAccountsRepository;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper.Result;
import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.service.infra.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.xebia.extras.selma.Selma;

/**
 * Trata as requisicoes para as URIs e metodos de Account.
 */
@RestController
@RequestMapping("/gf/accounts")
public class AccountController implements AccountService {

	@Autowired
	private AccountRepository repo;
	@Autowired
	private JournalEntryRepository jEntryRepo;
	@Autowired
	private BasicAccountsRepository basicAccRepo;

	private AccountMapper mapper;
	private RequestParamsMapper<Account> reqMapper = new RequestParamsMapper<>(Account.class);

	public AccountController() {
		mapper = Selma.mapper(AccountMapper.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.account.AccountService#createAccount(org.pinguin.
	 * gf.service.api.account.AccountTO)
	 */
	@Override
	@PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public AccountTO createAccount(@Valid @RequestBody AccountTO account) {

		Account saved = repo.save(mapper.asEntity(account));

		AccountTO response = mapper.asTO(saved);
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.account.AccountService#updateAccount(java.lang.
	 * Long, org.pinguin.gf.service.api.account.AccountTO)
	 */
	@Override
	@PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO updateAccount(@PathVariable("id") Long id, @Valid @RequestBody AccountTO account) {

		account.setAccountId(id);
		Account saved = repo.save(mapper.asEntity(account));

		AccountTO response = mapper.asTO(saved);
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.account.AccountService#deleteAccount(java.lang.
	 * Long)
	 */
	@Override
	@DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO deleteAccount(@PathVariable("id") Long id) {

		Optional<Account> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		AccountTO response = mapper.asTO(found.get());
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.account.AccountService#retrieveById(java.lang.
	 * Long)
	 */
	@Override
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public AccountTO retrieveById(@PathVariable("id") Long id) {
		Optional<Account> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		AccountTO response = mapper.asTO(found.get());
		response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.account.AccountService#retrieveStatements(java.
	 * lang.Long)
	 */
	@Override
	@GetMapping(value = "/{id}/statements", produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccStatementEntryTO> retrieveStatements(@PathVariable("id") Long id,
			@RequestParam("start") @DateTimeFormat(iso = ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = ISO.DATE) LocalDate end,
			@RequestParam("periodBalance") boolean periodBalance) {

		final Account root = repo.getOne(id);
		final List<Account> accs = retrieveAnalyticalAccounts(root);

		Iterable<JournalEntry> retrieved = null;
		if (periodBalance) {
			retrieved = jEntryRepo.findAll(journalEntry.debitAccount.in(accs).or(journalEntry.creditAccount.in(accs))
					.and(journalEntry.date.after(start.atStartOfDay()).or(journalEntry.date.eq(start.atStartOfDay()))
							.and(journalEntry.date.before(end.plusDays(1).atStartOfDay()))),
					Sort.by("date", "entryId"));
		} else {
			retrieved = jEntryRepo.findAll(journalEntry.debitAccount.in(accs).or(journalEntry.creditAccount.in(accs))
					.and(journalEntry.date.before(end.plusDays(1).atStartOfDay())), Sort.by("date", "entryId"));
		}

		final Set<Long> accIds = accs.stream().map(a -> a.getAccountId()).collect(Collectors.toSet());

		BigDecimal balance = BigDecimal.ZERO;
		List<AccStatementEntryTO> result = new ArrayList<>();
		for (JournalEntry item : retrieved) {

			AccStatementEntryTO entry = new AccStatementEntryTO();

			entry.setId(item.getEntryId());
			entry.setDate(item.getDate());
			entry.setOrigin(mapper.asTO(item.getCreditAccount()));
			entry.setAccount(mapper.asTO(item.getDebitAccount()));
			if (accIds.contains(item.getCreditAccount().getAccountId())) {
				if (root.getNature().equals(AccountNature.DEBIT)) {
					entry.setValue(safe(item.getValue()).multiply(BigDecimal.valueOf(-1.0)).setScale(2));
				} else {
					entry.setValue(safe(item.getValue()).setScale(2));
				}
			} else {
				if (root.getNature().equals(AccountNature.DEBIT)) {
					entry.setValue(safe(item.getValue()).setScale(2));
				} else {
					entry.setValue(safe(item.getValue()).multiply(BigDecimal.valueOf(-1.0)).setScale(2));
				}
			}
			entry.setDescription(item.getDescription());

			// Calculo do saldo
			balance = balance.add(entry.getValue());
			entry.setBalance(balance.setScale(2));

			if (!item.getDate().isBefore(start.atStartOfDay())) {
				result.add(entry);
			}
		}

		return result;
	}

	private BigDecimal safe(final BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private List<Account> retrieveAnalyticalAccounts(Account parent) {
		final Iterable<Account> retrieved = repo.findAll(account.parent.eq(parent));
		if (!retrieved.iterator().hasNext()) {
			return new ArrayList<>(Arrays.asList(parent));
		} else {
			final List<Account> result = new ArrayList<>();
			for (Account item : retrieved) {
				result.addAll(retrieveAnalyticalAccounts(item));
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pinguin.gf.service.api.account.AccountService#retrieveAll(java.util.
	 * Optional, java.util.Optional, java.util.Optional, java.util.Optional,
	 * java.util.Optional)
	 */
	@Override
	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveAll(@RequestParam(value = "filters", required = false) Optional<String> filters,
			@RequestParam(value = "sort", required = false) Optional<String> sort,
			@RequestParam(value = "page", required = false) Optional<String> page,
			@RequestParam(value = "pageSize", required = false) Optional<String> pageSize,
			@RequestParam(value = "fields", required = false) Optional<String> fields) {

		final Result result = reqMapper.map(filters, sort, page, pageSize, fields);

		List<AccountTO> list = new ArrayList<>();
		for (Account entity : repo.findAll(result.getPredicate(), result.getPageable())) {
			AccountTO to = mapper.asTO(entity);
			to.add(linkTo(AccountController.class).slash(to.getAccountId()).withSelfRel());
			if (to.getParent() != null) {
				to.add(linkTo(AccountController.class).slash(to.getParent().getAccountId()).withRel("parent"));
			}
			to.setParent(null);
			list.add(to);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pinguin.gf.service.api.account.AccountService#retrieveAnalSytical()
	 */
	@Override
	@GetMapping(value = "/analytical", produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveAnalytical() {
		final List<AccountTO> list = new ArrayList<>();
		for (final Account entity : repo.findAll(account.accountId
				.notIn(select(account.parent.accountId).from(account).where(account.parent.accountId.isNotNull())))) {
			final AccountTO to = mapper.asTO(entity);
			to.add(linkTo(AccountController.class).slash(to.getAccountId()).withSelfRel());
			if (to.getParent() != null) {
				to.add(linkTo(AccountController.class).slash(to.getParent().getAccountId()).withRel("parent"));
			}
			to.setParent(null);
			list.add(to);
		}
		return list;
	}

	@Override
	@GetMapping(value = "/income", produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveIncomeAccounts() {
		final BasicAccounts basicAccs = basicAccRepo.getOne(1L);

		final List<Account> accs = new ArrayList<>();
		accs.addAll(retrieveAnalyticalAccounts(basicAccs.getIncome()));

		final List<AccountTO> result = new ArrayList<>();
		for (Account item : accs) {
			result.add(mapper.asTO(item));
		}

		return result;
	}

	@Override
	@GetMapping(value = "/expenses", produces = MediaTypes.HAL_JSON_VALUE)
	public List<AccountTO> retrieveExpenseAccounts() {
		final BasicAccounts basicAccs = basicAccRepo.getOne(1L);

		final List<Account> accs = new ArrayList<>();
		accs.addAll(retrieveAnalyticalAccounts(basicAccs.getExpense()));

		final List<AccountTO> result = new ArrayList<>();
		for (Account item : accs) {
			result.add(mapper.asTO(item));
		}

		return result;
	}

	@Override
	@GetMapping(value = "/{id}/cashflow", produces = MediaTypes.HAL_JSON_VALUE)
	public List<DayResultTO> retrieveCashFlow(@PathVariable("id") Long id,
			@RequestParam("start") @DateTimeFormat(iso = ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = ISO.DATE) LocalDate end) {

		final Account root = repo.getOne(id);
		final List<Account> accs = retrieveAnalyticalAccounts(root);
		final Set<Long> accIds = accs.stream().map(a -> a.getAccountId()).collect(Collectors.toSet());

		Iterable<JournalEntry> retrieved = jEntryRepo.findAll(journalEntry.debitAccount.in(accs)
				.or(journalEntry.creditAccount.in(accs)).and(journalEntry.date.before(end.plusDays(1).atStartOfDay())),
				Sort.by("date", "entryId"));

		LocalDate first = null;
		final Map<LocalDate, DayResultTO> map = new HashMap<>();
		for (final JournalEntry entry : retrieved) {
			final LocalDate date = entry.getDate().toLocalDate();
			if (first == null) {
				first = date;
			}
			if (!map.containsKey(date)) {
				map.put(date, new DayResultTO(date));
			}
			// Calulando o resultado do dia
			if (accIds.contains(entry.getCreditAccount().getAccountId())) {
				if (root.getNature().equals(AccountNature.DEBIT)) {
					map.get(date).setResult(map.get(date).getResult().subtract(safe(entry.getValue())).setScale(2));
				} else {
					map.get(date).setResult(map.get(date).getResult().add(safe(entry.getValue()).setScale(2)));
				}
			} else {
				if (root.getNature().equals(AccountNature.DEBIT)) {
					map.get(date).setResult(map.get(date).getResult().add(safe(entry.getValue()).setScale(2)));
				} else {
					map.get(date).setResult(map.get(date).getResult().subtract(safe(entry.getValue()).setScale(2)));
				}
			}
		}

		BigDecimal balance = BigDecimal.ZERO;
		final List<DayResultTO> result = new ArrayList<>();
		for (LocalDate aux = first; aux.isBefore(end) || aux.isEqual(end); aux = aux.plusDays(1)) {
			if (!map.containsKey(aux)) {
				continue;
			}
			final DayResultTO item = map.get(aux);
			balance = balance.add(item.getResult());

			if (aux.isEqual(start) || aux.isAfter(start)) {
				item.setBalance(balance);
				result.add(item);
			}
		}

		return result;
	}

}
