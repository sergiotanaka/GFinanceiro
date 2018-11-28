package org.pinguin.gf.app.service;

import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.planning.PlanningService;
import org.pinguin.gf.service.api.planning.PlanningTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PlanningServiceProxy implements PlanningService {

	private final String planResourceUrl = "http://localhost:8080/gf/plannings";
	private final ParameterizedTypeReference<List<PlanningTO>> planListTypeRef = new ParameterizedTypeReference<List<PlanningTO>>() {
	};

	@Inject
	private RestTemplate restTemplate;

	@Override
	public PlanningTO createPlanning(PlanningTO to) {
		return restTemplate.postForObject(planResourceUrl, new HttpEntity<>(to), PlanningTO.class);
	}

	@Override
	public PlanningTO updatePlanning(Long planningId, PlanningTO to) {
		String resourceUrl = planResourceUrl + '/' + planningId;
		ResponseEntity<PlanningTO> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, new HttpEntity<>(to),
				PlanningTO.class);
		return response.getBody();
	}

	@Override
	public PlanningTO deletePlanning(Long id) {
		String entityUrl = planResourceUrl + "/" + id;
		restTemplate.delete(entityUrl);
		return null;
	}

	@Override
	public PlanningTO retrieveById(Long id) {
		return restTemplate.getForObject(planResourceUrl + "/" + id, PlanningTO.class);
	}

	@Override
	public List<PlanningTO> retrieveAll() {
		final ResponseEntity<List<PlanningTO>> response = restTemplate.exchange(planResourceUrl, HttpMethod.GET, null,
				planListTypeRef);
		return response.getBody();
	}

}
