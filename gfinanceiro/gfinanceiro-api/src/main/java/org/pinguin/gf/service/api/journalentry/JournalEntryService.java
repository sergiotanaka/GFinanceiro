package org.pinguin.gf.service.api.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface JournalEntryService {

	JournalEntryTO createEntry(JournalEntryTO entry, MultipartFile file);
	
	boolean exists(LocalDateTime date, BigDecimal value, String description);

	JournalEntryTO updateEntry(Long id, JournalEntryTO account);

	JournalEntryTO deleteEntry(Long id);

	JournalEntryTO retrieveById(Long id);

	List<JournalEntryTO> retrieveAll();
	
	ResponseEntity<byte[]> retrieveAttachment(Long id);

}