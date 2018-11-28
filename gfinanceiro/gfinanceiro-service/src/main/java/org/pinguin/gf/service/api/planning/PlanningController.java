package org.pinguin.gf.service.api.planning;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.domain.planning.PlanningRepository;
import org.pinguin.gf.service.infra.PlanningMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.xebia.extras.selma.Selma;

@RestController
@RequestMapping("/gf/plannings")
public class PlanningController implements PlanningService {

	@Autowired
	private PlanningRepository repo;
	private PlanningMapper mapper;

	public PlanningController() {
		mapper = Selma.mapper(PlanningMapper.class);
	}

	@Override
	@PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PlanningTO createPlanning(@Valid @RequestBody PlanningTO to) {

		Planning saved = repo.save(mapper.asEntity(to));

		PlanningTO response = mapper.asTO(saved);
		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());

		return response;
	}

	@Override
	@PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public PlanningTO updatePlanning(@PathVariable("id") Long planningId, @Valid @RequestBody PlanningTO to) {

		to.setPlanId(planningId);
		Planning saved = repo.save(mapper.asEntity(to));

		PlanningTO response = mapper.asTO(saved);
		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());

		return response;
	}

	@Override
	@DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public PlanningTO deletePlanning(@PathVariable("id") Long id) {
		Optional<Planning> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		PlanningTO response = mapper.asTO(found.get());
		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());
		return response;
	}

	@Override
	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public PlanningTO retrieveById(@PathVariable("id") Long id) {
		Optional<Planning> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		PlanningTO response = mapper.asTO(found.get());
		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());
		return response;
	}

	@Override
	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public List<PlanningTO> retrieveAll() {

		final List<PlanningTO> result = new ArrayList<>();

		for (final Planning entity : repo.findAll()) {
			result.add(mapper.asTO(entity));
		}

		return result;
	}

}
