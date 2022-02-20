package org.pinguin.gf.service.api.journalentry;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.pinguin.gf.domain.attachment.Attachment;
import org.pinguin.gf.domain.attachment.AttachmentRepository;
import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.domain.journalentry.JournalEntryRepository;
import org.pinguin.gf.domain.journalentry.Tag;
import org.pinguin.gf.domain.journalentry.TagRepository;
import org.pinguin.gf.service.infra.JournalEntryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.xebia.extras.selma.Selma;

/**
 * Trata as requisicoes para as URIs e metodos de JournalEntry (lancamentos).
 */
@RestController
@RequestMapping("/gf/journalentries")
public class JournalEntryController implements JournalEntryService {

	@Autowired
	private JournalEntryRepository repo;
	@Autowired
	private TagRepository tagRepo;
	@Autowired
	private AttachmentRepository attachmRepo;

	private JournalEntryMapper mapper;

	public JournalEntryController() {
		mapper = Selma.mapper(JournalEntryMapper.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.journalentry.JournalEntryService#createEntry(org.
	 * pinguin.gf.service.api.journalentry.JournalEntryTO)
	 */
	@Override
	@Transactional
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/hal+json")
	@ResponseStatus(HttpStatus.CREATED)
	public JournalEntryTO createEntry(@Valid @RequestPart("entry") JournalEntryTO entry,
			@RequestPart(value = "attachment", required = false) MultipartFile attachment) {
		if (exists(entry.getDate(), entry.getValue(), entry.getDescription())) {
			throw new IllegalArgumentException("Ja' existe lançamento semelhante.");
		}

		final JournalEntry mapped = mergeTags(mapper.asEntity(entry));
		final JournalEntry saved = repo.save(mapped);

		if (attachment != null) {
			try {
				attachmRepo.save(
						new Attachment(saved.getEntryId(), attachment.getOriginalFilename(), attachment.getBytes()));
			} catch (final IOException e) {
				throw new IllegalStateException("Falha ao converter bytes em Bytes.", e);
			}
		}

		return mapper.asTO(saved);
	}

	@Override
	@GetMapping(value = "/exists", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean exists(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
			@RequestParam("value") BigDecimal value, @RequestParam("description") String description) {
		final List<JournalEntry> found = repo.findByValueAndDate(value, date);
		for (final JournalEntry item : found) {
			float dist = levenshteinDistance(item.getDescription(), description);
			if (dist < 0.2f) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.journalentry.JournalEntryService#updateEntry(java.
	 * lang.Long, org.pinguin.gf.service.api.journalentry.JournalEntryTO)
	 */
	@Override
	@PutMapping(value = "/{id}", produces = "application/hal+json")
	public JournalEntryTO updateEntry(@PathVariable("id") Long id, @Valid @RequestBody JournalEntryTO account) {

		account.setEntryId(id);
		JournalEntry saved = repo.save(mapper.asEntity(account));

		return mapper.asTO(saved);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.journalentry.JournalEntryService#deleteEntry(java.
	 * lang.Long)
	 */
	@Override
	@DeleteMapping(value = "/{id}", produces = "application/hal+json")
	public JournalEntryTO deleteEntry(@PathVariable("id") Long id) {

		Optional<JournalEntry> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}

		JournalEntryTO response = mapper.asTO(found.get());
		repo.delete(found.get());
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.journalentry.JournalEntryService#retrieveById(java
	 * .lang.Long)
	 */
	@Override
	@GetMapping(value = "/{id}", produces = "application/hal+json")
	public JournalEntryTO retrieveById(@PathVariable("id") Long id) {
		Optional<JournalEntry> found = repo.findById(id);
		if (!found.isPresent()) {
			return null;
		}
		return mapper.asTO(found.get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pinguin.gf.service.api.journalentry.JournalEntryService#retrieveAll()
	 */
	@Override
	@GetMapping(produces = "application/hal+json")
	public List<JournalEntryTO> retrieveAll() {
		List<JournalEntryTO> list = new ArrayList<>();
		for (JournalEntry entity : repo.findAll()) {
			JournalEntryTO to = mapper.asTO(entity);
			list.add(to);
		}
		return list;
	}

	private float levenshteinDistance(final String first, final String second) {
		final LevenshteinDistance levDist = LevenshteinDistance.getDefaultInstance();
		final int dist = levDist.apply(first.toUpperCase(), second.toUpperCase()).intValue();
		int length = larger(first, second).length();
		System.out.println("dist: " + dist + " lenght: " + length);
		return ((float) dist / (float) length);
	}

	private String larger(final String first, final String second) {
		if (first == null && second == null) {
			return "";
		} else if (first != null && second == null) {
			return first;
		} else if (first == null && second != null) {
			return second;
		} else {
			return first.length() >= second.length() ? first : second;
		}
	}

	private JournalEntry mergeTags(final JournalEntry entity) {
		final List<Tag> tagWithIds = entity.getTags().stream().filter(t -> t.getTagId() != null)
				.collect(Collectors.toList());
		entity.getTags().removeAll(tagWithIds);
		tagWithIds.forEach(t -> tagRepo.findById(t.getTagId()).ifPresent(f -> entity.getTags().add(f)));
		return entity;
	}

	@Override
	@GetMapping(value = "/{id}/attachment")
	public ResponseEntity<byte[]> retrieveAttachment(@PathVariable("id") Long id) {
		final Optional<JournalEntry> found = repo.findById(id);
		if (!found.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		final Optional<Attachment> attachment = attachmRepo.findByEntryId(found.get().getEntryId());
		if (attachment.isPresent()) {
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + attachment.get().getFileName() + "\"")
					.body(attachment.get().getContent());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
