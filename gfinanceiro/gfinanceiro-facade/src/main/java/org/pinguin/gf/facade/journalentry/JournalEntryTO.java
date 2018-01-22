package org.pinguin.gf.facade.journalentry;

import java.math.BigDecimal;
import java.util.Calendar;

import org.pinguin.gf.facade.account.AccountTO;

public class JournalEntryTO {

    private Long id;
    private AccountTO debitAccount;
    private AccountTO creditAccount;
    private BigDecimal value;
    private Calendar date;
    private String description;

    public JournalEntryTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountTO getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(AccountTO debitAccount) {
        this.debitAccount = debitAccount;
    }

    public AccountTO getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(AccountTO creditAccount) {
        this.creditAccount = creditAccount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "JournalEntryTO [id=" + id + ", debitAccount=" + debitAccount + ", creditAccount=" + creditAccount
            + ", value=" + value + ", date=" + date + ", description=" + description + "]";
    }
}
