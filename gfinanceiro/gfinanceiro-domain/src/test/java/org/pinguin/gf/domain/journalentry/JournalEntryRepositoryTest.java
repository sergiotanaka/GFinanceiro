package org.pinguin.gf.domain.journalentry;

import static org.pinguin.gf.domain.journalentry.QJournalEntry.journalEntry;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.account.QAccount;
import org.pinguin.gf.domain.common.impl.ParseException;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper;
import org.pinguin.gf.domain.common.impl.RequestParamsMapper.Result;
import org.pinguin.gf.domain.common.impl.TagsFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class JournalEntryRepositoryTest {

	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private JournalEntryRepository repo;

	private RequestParamsMapper<JournalEntry> reqMapper = new RequestParamsMapper<>(JournalEntry.class);

	@Test
	public void testFindJournalEntry() {

		final Account caixa = accRepo.save(new Account("Caixa", AccountNature.CREDIT));
		repo.save(new JournalEntry(caixa, caixa, BigDecimal.valueOf(3.0), LocalDateTime.now(), "teste"));

		Iterable<JournalEntry> it = repo.findAll(journalEntry.creditAccount.name.eq("Caixa"));
		List<JournalEntry> list = new ArrayList<>();
		it.forEach(list::add);
		Assertions.assertEquals(1, list.size());

		Iterable<JournalEntry> it2 = repo.findAll(journalEntry.creditAccount.name.eq("Banco"));
		List<JournalEntry> list2 = new ArrayList<>();
		it2.forEach(list2::add);
		Assertions.assertEquals(0, list2.size());

		Page<JournalEntry> page = repo.findAll(journalEntry.creditAccount.name.eq("Caixa"), Pageable.unpaged());
		System.out.println(page);

	}

	@Test
	public void testFindJournalEntryByTags() {

		final Account caixa = accRepo.save(new Account("Caixa", AccountNature.CREDIT));
		repo.save(new JournalEntry(caixa, caixa, BigDecimal.valueOf(3.0), LocalDateTime.now(), "teste",
				new Tag("Sergio"), new Tag("Raquel")));

		Iterable<JournalEntry> it = repo.findAll(journalEntry.tags.any().name.upper().eq("RAQUEL"));
		List<JournalEntry> list = new ArrayList<>();
		it.forEach(list::add);
		Assertions.assertEquals(1, list.size());

		Iterable<JournalEntry> it2 = repo.findAll(journalEntry.tags.any().name.upper().eq("SILVANA"));
		List<JournalEntry> list2 = new ArrayList<>();
		it2.forEach(list2::add);
		Assertions.assertEquals(0, list2.size());

	}

	@Test
	public void testMapRequest() throws UnsupportedEncodingException {

		final Function<String, Path<?>> pathRetriever = new Function<>() {
			@Override
			public Path<?> apply(String fieldName) {
				if (fieldName.equals("name")) {
					return QAccount.account.name;
				} else if (fieldName.equals("nature")) {
					return QAccount.account.nature;
				}
				return null;
			}
		};
		reqMapper.setPathRetriever(pathRetriever);

		final String filters = URLEncoder.encode("name=@Conta;(nature==CREDIT,nature==DEBIT)", "UTF-8");

		final Result result = reqMapper.map(Optional.of(filters), Optional.of("-accountId"), Optional.of("2"),
				Optional.of("10"), Optional.empty());

		for (int i = 1; i <= 50; i++) {
			accRepo.save(new Account("Conta" + i, i % 2 == 0 ? AccountNature.CREDIT : AccountNature.DEBIT));
		}
		final Page<Account> page = accRepo.findAll(result.getPredicate(), result.getPageable());
		Assertions.assertEquals(10, page.getContent().size());
		Assertions.assertEquals("Conta30", page.getContent().get(0).getName());

		final Page<Account> page2 = accRepo.findAll(result.getPredicate(), page.nextPageable());
		Assertions.assertEquals(10, page2.getContent().size());
		Assertions.assertEquals("Conta20", page2.getContent().get(0).getName());

		Page<Account> page3 = accRepo.findAll(
				Projections.bean(Account.class, QAccount.account.name, QAccount.account.nature), result.getPredicate(),
				result.getPageable());
		Assertions.assertEquals(10, page3.getContent().size());

	}

	@Test
	public void testTagsFilterParser() throws ParseException {

		final Account caixa = accRepo.save(new Account("Caixa", AccountNature.CREDIT));
		repo.save(new JournalEntry(caixa, caixa, BigDecimal.valueOf(3.0), LocalDateTime.now(), "teste",
				new Tag("Sergio"), new Tag("Raquel")));

		Iterable<JournalEntry> it = repo.findAll(parseTagsFilter("Sergio and Raquel"));
		List<JournalEntry> list = new ArrayList<>();
		it.forEach(list::add);
		Assertions.assertEquals(1, list.size());

		Iterable<JournalEntry> it2 = repo.findAll(parseTagsFilter("Sergio and Silvana"));
		List<JournalEntry> list2 = new ArrayList<>();
		it2.forEach(list2::add);
		Assertions.assertEquals(0, list2.size());

		Iterable<JournalEntry> it3 = repo.findAll(parseTagsFilter("Sergio or Silvana"));
		List<JournalEntry> list3 = new ArrayList<>();
		it3.forEach(list3::add);
		Assertions.assertEquals(1, list3.size());

		Iterable<JournalEntry> it4 = repo.findAll(parseTagsFilter("Sergio and not Silvana"));
		List<JournalEntry> list4 = new ArrayList<>();
		it4.forEach(list4::add);
		Assertions.assertEquals(1, list4.size());

		Iterable<JournalEntry> it5 = repo.findAll(parseTagsFilter("(Sergio and Silvana) or (Sergio and Raquel)"));
		List<JournalEntry> list5 = new ArrayList<>();
		it5.forEach(list5::add);
		Assertions.assertEquals(1, list5.size());

	}

	private BooleanExpression parseTagsFilter(final String filter) throws ParseException {
		TagsFilterParser parser = new TagsFilterParser();
		parser.ReInit(new StringReader(filter.toUpperCase()));
		return parser.expr();
	}

}
