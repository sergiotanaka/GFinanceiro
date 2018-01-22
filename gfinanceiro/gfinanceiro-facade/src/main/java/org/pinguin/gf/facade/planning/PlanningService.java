package org.pinguin.gf.facade.planning;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.dozer.DozerBeanMapper;
import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.domain.planning.PlanningRepository;

public class PlanningService {

	private final DozerBeanMapper mapper = new DozerBeanMapper();

	@Inject
	private PlanningRepository repo;

	public PlanningTO createPlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		Planning created = repo.create(entity);

		return mapper.map(created, PlanningTO.class);
	}

	public PlanningTO updatePlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		Planning updated = repo.update(entity);

		return mapper.map(updated, PlanningTO.class);
	}

	public PlanningTO deletePlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		Planning deleted = repo.delete(entity);

		return mapper.map(deleted, PlanningTO.class);
	}

	public List<PlanningTO> retrievePlannings() {

		List<Planning> all = repo.retrieveAll();

		List<PlanningTO> result = new ArrayList<>();
		for (Planning entity : all) {
			result.add(mapper.map(entity, PlanningTO.class));
		}

		return result;
	}

}
