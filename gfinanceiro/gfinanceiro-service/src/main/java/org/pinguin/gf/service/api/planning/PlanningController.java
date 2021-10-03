package org.pinguin.gf.service.api.planning;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.domain.planning.PlanningRepository;
import org.pinguin.gf.service.infra.PlanningMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	@Autowired
	private PdfReportGenerator reportGenerator;

	private PlanningMapper mapper;

	public PlanningController() {
		mapper = Selma.mapper(PlanningMapper.class);
	}

	@Override
	@PostMapping(produces = "application/hal+json")
	@ResponseStatus(HttpStatus.CREATED)
	public PlanningTO createPlanning(@Valid @RequestBody PlanningTO to) {

		Planning saved = repo.save(mapper.asEntity(to));

		PlanningTO response = mapper.asTO(saved);
//		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());

		return response;
	}

	@Override
	@PutMapping(value = "/{id}", produces = "application/hal+json")
	public PlanningTO updatePlanning(@PathVariable("id") Long planningId, @Valid @RequestBody PlanningTO to) {

		to.setPlanId(planningId);
		Planning saved = repo.save(mapper.asEntity(to));

		PlanningTO response = mapper.asTO(saved);
//		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());

		return response;
	}

	@Override
	@DeleteMapping(value = "/{id}", produces = "application/hal+json")
	public PlanningTO deletePlanning(@PathVariable("id") Long id) {
		Optional<Planning> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		PlanningTO response = mapper.asTO(found.get());
//		response.add(linkTo(PlanningController.class).slash(response.getPlanId()).withSelfRel());
		return response;
	}

	@Override
	@GetMapping(value = "/{id}", produces = "application/hal+json")
	public PlanningTO retrieveById(@PathVariable("id") Long id) {
		final Optional<Planning> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		return mapper.asTO(found.get());
	}

	@Override
	@GetMapping(produces = "application/hal+json")
	public List<PlanningTO> retrieveAll() {

		final List<PlanningTO> result = new ArrayList<>();

		for (final Planning entity : repo.findAll()) {
			result.add(mapper.asTO(entity));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@GetMapping(value = "/report/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> retrieveReport(@PathVariable("id") Long id) {

		Optional<Planning> found = repo.findById(id);

		if (found.isPresent()) {
			final ByteArrayInputStream bis = reportGenerator.build(found.get());
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		}

		return (ResponseEntity<InputStreamResource>) ResponseEntity.noContent();
	}

}
