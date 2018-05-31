package org.pinguin.gf.service.api.account;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.pinguin.gf.domain.account.QAccount.account;
import static org.pinguin.gf.domain.journalentry.QJournalEntry.journalEntry;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper.Result;
import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.service.infra.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AccountController {

    @Autowired
    private AccountRepository repo;
    @Autowired
    private JournalEntryRepository jEntryRepo;
    private AccountMapper mapper;
    private RequestParamsMapper<Account> reqMapper = new RequestParamsMapper<>(Account.class);

    public AccountController() {
        mapper = Selma.mapper(AccountMapper.class);
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountTO createAccount(@Valid @RequestBody AccountTO account) {

        Account saved = repo.save(mapper.asEntity(account));

        AccountTO response = mapper.asTO(saved);
        response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

        return response;
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public AccountTO updateAccount(@PathVariable("id") Long id, @Valid @RequestBody AccountTO account) {

        account.setAccountId(id);
        Account saved = repo.save(mapper.asEntity(account));

        AccountTO response = mapper.asTO(saved);
        response.add(linkTo(AccountController.class).slash(response.getAccountId()).withSelfRel());

        return response;
    }

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

    @GetMapping(value = "/{id}/statements", produces = MediaTypes.HAL_JSON_VALUE)
    public List<AccStatementEntryTO> retrieveStatements(@PathVariable("id") Long id) {
        final Account root = repo.getOne(id);
        final List<Account> accs = retrieveAnalyticalAccounts(root);
        
        final Iterable<JournalEntry> retrieved = jEntryRepo.findAll(journalEntry.debitAccount.in(accs).or(journalEntry.creditAccount.in(accs)));
//        final Iterable<JournalEntry> retrieved = jEntryRepo.findAll();
        
        BigDecimal balance = BigDecimal.ZERO;
        List<AccStatementEntryTO> result = new ArrayList<>();
        for (JournalEntry item : retrieved) {

            AccStatementEntryTO entry = new AccStatementEntryTO();

            entry.setId(item.getEntryId());
            entry.setDate(item.getDate());
            entry.setOrigin(mapper.asTO(item.getCreditAccount()));
            entry.setAccount(mapper.asTO(item.getDebitAccount()));
            if (accs.contains(item.getCreditAccount())) {
                if (root.getNature().equals(AccountNature.DEBIT)) {
                    entry.setValue(item.getValue().multiply(BigDecimal.valueOf(-1.0)).setScale(2));
                } else {
                    entry.setValue(item.getValue().setScale(2));
                }
            } else {
                if (root.getNature().equals(AccountNature.DEBIT)) {
                    entry.setValue(item.getValue().setScale(2));
                } else {
                    entry.setValue(item.getValue().multiply(BigDecimal.valueOf(-1.0)).setScale(2));
                }
            }
            entry.setDescription(item.getDescription());

            // Calculo do saldo
            balance = balance.add(entry.getValue());
            entry.setBalance(balance.setScale(2));
            result.add(entry);
        }
        
        return result;
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


    /**
     * TODO: parametros.
     * 
     * @param sort
     * @param page
     * @param pageSize
     * @param fields
     * @return
     */
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

    /**
     * Retorna as contas analiticas.
     * 
     * @return
     */
    @GetMapping(value = "/analytical", produces = MediaTypes.HAL_JSON_VALUE)
    public List<AccountTO> retrieveAnalSytical() {
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

}
