package org.pinguin.gf.domain.journalentry;

import org.pinguin.core.domain.RepositoryBase;

public class JournalEntryRepository extends RepositoryBase<JournalEntry, Long> {

    @Override
    protected Class<JournalEntry> getEntityType() {
        return JournalEntry.class;
    }

}
