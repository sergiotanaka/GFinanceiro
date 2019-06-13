package org.pinguin.gf.domain.planning;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.pinguin.gf.domain.account.Account;

@Entity
public class AccountPlanning {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long accPlanId;
	@OneToOne(fetch = FetchType.EAGER)
	private Account account;
	private BigDecimal value;

	public Long getAccPlanId() {
		return accPlanId;
	}

	public void setAccPlanId(Long accPlanId) {
		this.accPlanId = accPlanId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AccountPlanning [accPlanId=" + accPlanId + ", account=" + account + ", value=" + value + "]";
	}
}
