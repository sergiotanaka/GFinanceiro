package org.pinguin.gf.domain.planning;

import org.pinguin.core.domain.RepositoryBase;

public class PlanningRepository extends RepositoryBase<Planning, Long> {

	@Override
	protected Class<Planning> getEntityType() {
		return Planning.class;
	}

}
