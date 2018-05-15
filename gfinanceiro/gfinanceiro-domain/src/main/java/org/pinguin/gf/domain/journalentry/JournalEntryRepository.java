package org.pinguin.gf.domain.journalentry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

}
