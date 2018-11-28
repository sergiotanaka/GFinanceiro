package org.pinguin.gf.service.infra;

import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.service.api.planning.PlanningTO;

import fr.xebia.extras.selma.Mapper;

@Mapper(withIgnoreFields = { "org.pinguin.gf.service.api.planning.PlanningTO.id",
		"org.pinguin.gf.service.api.planning.PlanningTO.links", "org.pinguin.gf.service.api.account.AccountTO.id",
		"org.pinguin.gf.service.api.account.AccountTO.links" })
public interface PlanningMapper {

	PlanningTO asTO(Planning plan);

	Planning asEntity(PlanningTO to);

}
