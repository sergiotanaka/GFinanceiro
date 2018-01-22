package org.pinguin.gf.domain.planning;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Planning {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@Embedded
	private MonthYear monthYear;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<AccountPlanning> accountPlannings = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MonthYear getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(MonthYear monthYear) {
		this.monthYear = monthYear;
	}

	public Set<AccountPlanning> getAccountPlannings() {
		return accountPlannings;
	}

	public void setAccountPlannings(Set<AccountPlanning> accountPlannings) {
		this.accountPlannings = accountPlannings;
	}

}
