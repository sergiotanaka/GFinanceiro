package org.pinguin.gf.facade.planning;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.domain.planning.PlanningRepository;

public class PlanningService {

	private final DozerBeanMapper mapper = new DozerBeanMapper();

	private PlanningRepository repo;

	public PlanningTO createPlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		Planning created = repo.save(entity);

		return mapper.map(created, PlanningTO.class);
	}

	public PlanningTO updatePlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		Planning updated = repo.save(entity);

		return mapper.map(updated, PlanningTO.class);
	}

	public PlanningTO deletePlanning(PlanningTO planning) {

		Planning entity = mapper.map(planning, Planning.class);

		 repo.delete(entity);

		return mapper.map(entity, PlanningTO.class);
	}

	public List<PlanningTO> retrievePlannings() {

		Iterable<Planning> all = repo.findAll();

		List<PlanningTO> result = new ArrayList<>();
		for (Planning entity : all) {
			result.add(mapper.map(entity, PlanningTO.class));
		}

		return result;
	}

}
