package org.pinguin.gf.service.api.journalentry;

import java.util.List;

public interface JournalEntryService {

	JournalEntryTO createEntry(JournalEntryTO entry);

	JournalEntryTO updateEntry(Long id, JournalEntryTO account);

	JournalEntryTO deleteEntry(Long id);

	JournalEntryTO retrieveById(Long id);

	List<JournalEntryTO> retrieveAll();

}