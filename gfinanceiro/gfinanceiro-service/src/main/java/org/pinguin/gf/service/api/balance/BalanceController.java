package org.pinguin.gf.service.api.balance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.BasicAccounts;
import org.pinguin.gf.domain.account.BasicAccountsRepository;
import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.service.api.account.AccountNatureTO;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.infra.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.xebia.extras.selma.Selma;

@RestController
@RequestMapping("/gf/balance")
public class BalanceController {

    @Autowired
    private JournalEntryRepository repo;
    @Autowired
    private BasicAccountsRepository basicAccRepo;
    private AccountMapper accMapper = Selma.mapper(AccountMapper.class);

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public List<BalanceTO> retrieveBalance() {

        List<JournalEntry> retrieved = repo.findAll();

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
        BasicAccounts ba = basicAccRepo.getOne(1L);

        Account income = ba.getIncome();
        Account expense = ba.getExpense();
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
            balanceMap.put(acc, new BalanceTO(accMapper.asTO(acc)));
        }
        BalanceTO balanceTO = balanceMap.get(acc);
        balanceTO.setCredits(balanceTO.getCredits().add(credit));
        balanceTO.setDebits(balanceTO.getDebits().add(debit));
        balanceTO.setBalance(balanceTO.getBalance().add(balance));

        if (acc.getParent() != null) {
            sumToAccount(acc.getParent(), credit, debit, balance, balanceMap);
        }
    }

}