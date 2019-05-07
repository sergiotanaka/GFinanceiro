package org.pinguin.gf.service.api.planning;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface PlanningService {

	PlanningTO createPlanning(PlanningTO to);

	PlanningTO updatePlanning(Long planningId, PlanningTO to);

	PlanningTO deletePlanning(Long id);

	PlanningTO retrieveById(Long id);

	List<PlanningTO> retrieveAll();

	ResponseEntity<InputStreamResource> retrieveReport(Long id);

}
