package org.pinguin.gf.app.service;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.planning.PlanningService;
import org.pinguin.gf.service.api.planning.PlanningTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
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

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<InputStreamResource> retrieveReport(Long id) {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_PDF));

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<byte[]> response = restTemplate.exchange(planResourceUrl + "/report/" + id, HttpMethod.GET,
				entity, byte[].class);

		if (response.getStatusCode() != HttpStatus.OK) {
			return (ResponseEntity<InputStreamResource>) ResponseEntity.notFound();
		}

		return ResponseEntity.ok().body(new InputStreamResource(new ByteArrayInputStream(response.getBody())));
		// String userHome = System.getProperty("user.home");
		// Files.write(Paths.get(userHome + "/test.pdf"), response.getBody());
	}

}
