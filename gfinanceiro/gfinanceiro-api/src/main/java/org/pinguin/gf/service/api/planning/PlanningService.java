package org.pinguin.gf.service.api.planning;

public interface PlanningService {

	void createPlanning(PlanningTO to);

	void updatePlanning(Long planningId, PlanningTO to);

}
