package org.pinguin.gf.service.api.planning;

import java.math.BigDecimal;
import java.util.List;

import org.pinguin.gf.service.api.account.AccountTO;

public class PlanningTO {
	private Long planningId;
	private AccountTO account;
	private BigDecimal valude;

	public Long getPlanningId() {
		return planningId;
	}

	public void setPlanningId(Long planningId) {
		this.planningId = planningId;
	}

	public AccountTO getAccount() {
		return account;
	}

	public void setAccount(AccountTO account) {
		this.account = account;
	}

	public BigDecimal getValude() {
		return valude;
	}

	public void setValude(BigDecimal valude) {
		this.valude = valude;
	}

	public MonthYearTO getMonthYear() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMonthYear(MonthYearTO monthYearTO) {
		// TODO Auto-generated method stub

	}

	public List<PlanningTO> getAccountPlannings() {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(BigDecimal value) {
		// TODO Auto-generated method stub

	}

}
