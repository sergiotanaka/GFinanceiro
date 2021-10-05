package org.pinguin.gf.domain.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.pinguin.gf.domain.account.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class JournalEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long entryId;
	/** Destino */
	@OneToOne(fetch = FetchType.EAGER)
	private Account debitAccount;
	/** Origem */
	@OneToOne(fetch = FetchType.EAGER)
	private Account creditAccount;
	private BigDecimal value;
	private LocalDateTime date;
	private Boolean future = false;

	private String description;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
	private List<Tag> tags = new ArrayList<>();

	public JournalEntry(Account debitAccount, Account creditAccount, BigDecimal value, LocalDateTime date,
			String description, Tag... tags) {
		super();
		this.debitAccount = debitAccount;
		this.creditAccount = creditAccount;
		this.value = value;
		this.date = date;
		this.description = description;
		this.tags.addAll(Arrays.asList(tags));
	}
}
