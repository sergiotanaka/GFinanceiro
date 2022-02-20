package org.pinguin.gf.domain.attachment;

import java.util.Optional;

import org.pinguin.gf.domain.journalentry.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AttachmentRepository extends JpaRepository<Attachment, Long>, QuerydslPredicateExecutor<Attachment> {

	Optional<Attachment> findByEntryId(final Long entryId);

}
