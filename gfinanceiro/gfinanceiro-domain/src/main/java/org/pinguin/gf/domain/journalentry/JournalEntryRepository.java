package org.pinguin.gf.domain.journalentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface JournalEntryRepository
		extends JpaRepository<JournalEntry, Long>, QuerydslPredicateExecutor<JournalEntry> {

}
