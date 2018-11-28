package org.pinguin.gf.service.api.planning;

import java.util.HashSet;
import java.util.Set;

import org.springframework.hateoas.ResourceSupport;

public class PlanningTO extends ResourceSupport {
	private Long planId;
	private MonthYearTO monthYear;
	private Set<AccountPlanningTO> accountPlannings = new HashSet<>();

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public MonthYearTO getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(MonthYearTO monthYear) {
		this.monthYear = monthYear;
	}

	public Set<AccountPlanningTO> getAccountPlannings() {
		return accountPlannings;
	}

	public void setAccountPlannings(Set<AccountPlanningTO> accountPlannings) {
		this.accountPlannings = accountPlannings;
	}
}
