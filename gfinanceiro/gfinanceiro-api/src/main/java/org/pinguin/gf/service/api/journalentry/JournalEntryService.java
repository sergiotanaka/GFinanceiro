package org.pinguin.gf.service.api.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface JournalEntryService {

	JournalEntryTO createEntry(JournalEntryTO entry);

	boolean exists(LocalDateTime date, BigDecimal value, String description);

	JournalEntryTO updateEntry(Long id, JournalEntryTO account);

	JournalEntryTO deleteEntry(Long id);

	JournalEntryTO retrieveById(Long id);

	List<JournalEntryTO> retrieveAll();

}