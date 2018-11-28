package org.pinguin.gf.service.api.planning;

import java.util.List;

public interface PlanningService {

	PlanningTO createPlanning(PlanningTO to);

	PlanningTO updatePlanning(Long planningId, PlanningTO to);

	PlanningTO deletePlanning(Long id);

	PlanningTO retrieveById(Long id);

	List<PlanningTO> retrieveAll();

}
