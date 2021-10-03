package org.pinguin.gf.service.api.journalentry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.pinguin.gf.domain.journalentry.Tag;
import org.pinguin.gf.domain.journalentry.TagRepository;
import org.pinguin.gf.service.infra.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/gf/tags")
public class TagController implements TagService {

	@Autowired
	private TagRepository repo;
	private TagMapper mapper;

	/**
	 * Construtor.
	 */
	public TagController() {
		mapper = Selma.mapper(TagMapper.class);
	}

	@Override
	@PostMapping(produces = "application/hal+json")
	@ResponseStatus(HttpStatus.CREATED)
	public TagTO createTag(@Valid @RequestBody TagTO tagTO) {
		final Tag saved = repo.save(mapper.asEntity(tagTO));
		return mapper.asTO(saved);
	}

	@Override
	@PutMapping(value = "/{id}", produces = "application/hal+json")
	public TagTO updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagTO tagTO) {
		tagTO.setTagId(id);
		final Tag saved = repo.save(mapper.asEntity(tagTO));
		return mapper.asTO(saved);
	}

	@Override
	@DeleteMapping(value = "/{id}", produces = "application/hal+json")
	public TagTO deleteTag(@PathVariable("id") Long id) {
		final Optional<Tag> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		repo.delete(found.get());
		return mapper.asTO(found.get());
	}

	@Override
	@GetMapping(value = "/{id}", produces = "application/hal+json")
	public TagTO retrieveById(@PathVariable("id") Long id) {
		final Optional<Tag> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		return mapper.asTO(found.get());
	}

	@Override
	@GetMapping(produces = "application/hal+json")
	public List<TagTO> retrieveAll() {
		return repo.findAll().stream().map(mapper::asTO).collect(Collectors.toList());
	}

}
