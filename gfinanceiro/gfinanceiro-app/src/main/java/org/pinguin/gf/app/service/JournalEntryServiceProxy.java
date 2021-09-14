package org.pinguin.gf.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class JournalEntryServiceProxy implements JournalEntryService {

	private final String entryResourceUrl = "http://localhost:8080/gf/journalentries";
	private final ParameterizedTypeReference<List<JournalEntryTO>> entryListTypeRef = new ParameterizedTypeReference<List<JournalEntryTO>>() {
	};

	@Inject
	private RestTemplate restTemplate;

	@Override
	public JournalEntryTO createEntry(JournalEntryTO entry) {
		return restTemplate.postForObject(entryResourceUrl, new HttpEntity<>(entry), JournalEntryTO.class);
	}

	@Override
	public boolean exists(final LocalDateTime date, final BigDecimal value, final String description) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(entryResourceUrl + "/exists")
				.queryParam("date", date).queryParam("value", value).queryParam("description", description);
		return restTemplate.getForObject(builder.build().encode().toUri(), Boolean.class);
	}

	@Override
	public JournalEntryTO updateEntry(Long id, JournalEntryTO entry) {
		String resourceUrl = entryResourceUrl + '/' + id;
		ResponseEntity<JournalEntryTO> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT,
				new HttpEntity<>(entry), JournalEntryTO.class);
		return response.getBody();
	}

	@Override
	public JournalEntryTO deleteEntry(Long id) {
		String entityUrl = entryResourceUrl + "/" + id;
		restTemplate.delete(entityUrl);
		return null;
	}

	@Override
	public JournalEntryTO retrieveById(Long id) {
		return restTemplate.getForObject(entryResourceUrl + "/" + id, JournalEntryTO.class);
	}

	@Override
	public List<JournalEntryTO> retrieveAll() {
		final ResponseEntity<List<JournalEntryTO>> response = restTemplate.exchange(entryResourceUrl, HttpMethod.GET,
				null, entryListTypeRef);
		return response.getBody();
	}

}
