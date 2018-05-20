package org.pinguin.gf.service.api.journalentry;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.service.infra.JournalEntryMapper;
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

/**
 * Trata as requisicoes para as URIs e metodos de JournalEntry (lancamentos).
 */
@RestController
@RequestMapping("/gf/journalentries")
public class JournalEntryController {

	@Autowired
	private JournalEntryRepository repo;
	private JournalEntryMapper mapper;

	public JournalEntryController() {
		mapper = Selma.mapper(JournalEntryMapper.class);
	}

	@PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public JournalEntryTO createEntry(@Valid @RequestBody JournalEntryTO entry) {

		JournalEntry saved = repo.save(mapper.asEntity(entry));

		JournalEntryTO response = mapper.asTO(saved);
		response.add(linkTo(JournalEntryController.class).slash(response.getEntryId()).withSelfRel());

		return response;
	}

	@PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public JournalEntryTO updateEntry(@PathVariable("id") Long id, @Valid @RequestBody JournalEntryTO account) {

		account.setEntryId(id);
		JournalEntry saved = repo.save(mapper.asEntity(account));

		JournalEntryTO response = mapper.asTO(saved);
		response.add(linkTo(JournalEntryController.class).slash(response.getEntryId()).withSelfRel());

		return response;
	}

	@DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public JournalEntryTO deleteEntry(@PathVariable("id") Long id) {

		Optional<JournalEntry> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		JournalEntryTO response = mapper.asTO(found.get());
		response.add(linkTo(JournalEntryController.class).slash(response.getEntryId()).withSelfRel());
		return response;
	}

	@GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
	public JournalEntryTO retrieveById(@PathVariable("id") Long id) {
		Optional<JournalEntry> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		JournalEntryTO response = mapper.asTO(found.get());
		response.add(linkTo(JournalEntryController.class).slash(response.getEntryId()).withSelfRel());
		return response;
	}

	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public List<JournalEntryTO> retrieveAll() {
		List<JournalEntryTO> list = new ArrayList<>();
		for (JournalEntry entity : repo.findAll()) {
			JournalEntryTO to = mapper.asTO(entity);
			to.add(linkTo(JournalEntryController.class).slash(to.getEntryId()).withSelfRel());
			list.add(to);
		}
		return list;
	}

}
