package org.pinguin.gf.app.service;

import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.journalentry.TagService;
import org.pinguin.gf.service.api.journalentry.TagTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TagServiceProxy implements TagService {

	private final String typeResourceUrl = "http://localhost:8080/gf/tags";
	private final ParameterizedTypeReference<List<TagTO>> tagTypeRef = new ParameterizedTypeReference<List<TagTO>>() {
	};

	@Inject
	private RestTemplate restTemplate;

	@Override
	public TagTO createTag(TagTO tag) {
		return restTemplate.postForObject(typeResourceUrl, new HttpEntity<>(tag), TagTO.class);
	}

	@Override
	public TagTO updateTag(Long id, TagTO account) {
		String resourceUrl = typeResourceUrl + '/' + id;
		ResponseEntity<TagTO> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, new HttpEntity<>(account),
				TagTO.class);
		return response.getBody();
	}

	@Override
	public TagTO deleteTag(Long id) {
		String entityUrl = typeResourceUrl + "/" + id;
		ResponseEntity<TagTO> response = restTemplate.exchange(entityUrl, HttpMethod.DELETE, new HttpEntity<>(id),
				TagTO.class);
		return response.getBody();
	}

	@Override
	public TagTO retrieveById(Long id) {
		return restTemplate.getForObject(typeResourceUrl + "/" + id, TagTO.class);
	}

	@Override
	public List<TagTO> retrieveAll() {
		final ResponseEntity<List<TagTO>> response = restTemplate.exchange(typeResourceUrl, HttpMethod.GET, null,
				tagTypeRef);
		return response.getBody();
	}

}
