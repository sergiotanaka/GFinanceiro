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

	@Override
	public String toString() {
		return "PlanningTO [planId=" + planId + ", monthYear=" + monthYear + ", accountPlannings=" + accountPlannings
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((monthYear == null) ? 0 : monthYear.hashCode());
		result = prime * result + ((planId == null) ? 0 : planId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanningTO other = (PlanningTO) obj;
		if (monthYear == null) {
			if (other.monthYear != null)
				return false;
		} else if (!monthYear.equals(other.monthYear))
			return false;
		if (planId == null) {
			if (other.planId != null)
				return false;
		} else if (!planId.equals(other.planId))
			return false;
		return true;
	}

}
