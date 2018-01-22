package org.pinguin.gf.facade.planning;

import java.util.ArrayList;
import java.util.List;

public class PlanningTO {

	private Long id;
	private MonthYearTO monthYear;
	private List<AccountPlanningTO> accountPlannings = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MonthYearTO getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(MonthYearTO monthYear) {
		this.monthYear = monthYear;
	}

	public List<AccountPlanningTO> getAccountPlannings() {
		return accountPlannings;
	}

	public void setAccountPlannings(List<AccountPlanningTO> accountPlannings) {
		this.accountPlannings = accountPlannings;
	}

}
