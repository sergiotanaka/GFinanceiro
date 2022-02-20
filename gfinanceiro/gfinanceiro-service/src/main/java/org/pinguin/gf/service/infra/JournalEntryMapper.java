package org.pinguin.gf.service.infra;

import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;

import fr.xebia.extras.selma.Mapper;

@Mapper(withIgnoreFields = { "org.pinguin.gf.domain.journalentry.Tag.entries",
		"org.pinguin.gf.domain.journalentry.JournalEntry.file",
		"org.pinguin.gf.service.api.journalentry.JournalEntryTO.file" })
public interface JournalEntryMapper {

	JournalEntryTO asTO(JournalEntry entity);

	JournalEntry asEntity(JournalEntryTO to);

}
