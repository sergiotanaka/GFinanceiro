package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface JournalEntryRepository
		extends JpaRepository<JournalEntry, Long>, QuerydslPredicateExecutor<JournalEntry> {

	List<JournalEntry> findByValueAndDate(final BigDecimal value, final LocalDateTime date);

}
