package org.pinguin.gf.service.api.planning;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlanningTO {
	private Long planId;
	private MonthYearTO monthYear;
	private Set<AccountPlanningTO> accountPlannings = new HashSet<>();
}
