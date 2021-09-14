package org.pinguin.gf.domain.journalentry;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

//@RunWith(SpringRunner.class)
@DataJpaTest
public class JournalEntryRepositoryTest {

//	@Autowired
//	private AccountRepository accRepo;
//	@Autowired
//	private JournalEntryRepository repo;
//
//	private RequestParamsMapper<JournalEntry> reqMapper = new RequestParamsMapper<>(JournalEntry.class);
//
//	@Test
//	public void testFindJournalEntry() {
//
//		final Account caixa = accRepo.save(new Account("Caixa", AccountNature.CREDIT));
//		repo.save(new JournalEntry(caixa, caixa, BigDecimal.valueOf(3.0), LocalDateTime.now(), "teste"));
//
//		Iterable<JournalEntry> it = repo.findAll(journalEntry.creditAccount.name.eq("Caixa"));
//		List<JournalEntry> list = new ArrayList<>();
//		it.forEach(list::add);
//		Assert.assertEquals(1, list.size());
//
//		Iterable<JournalEntry> it2 = repo.findAll(journalEntry.creditAccount.name.eq("Banco"));
//		List<JournalEntry> list2 = new ArrayList<>();
//		it2.forEach(list2::add);
//		Assert.assertEquals(0, list2.size());
//
//		Page<JournalEntry> page = repo.findAll(journalEntry.creditAccount.name.eq("Caixa"), Pageable.unpaged());
//		System.out.println(page);
//
//	}
//
//	@Test
//	public void testMapRequest() throws UnsupportedEncodingException {
//
//		final Function<String, Path<?>> pathRetriever = new Function<String, Path<?>>() {
//			@Override
//			public Path<?> apply(String fieldName) {
//				if (fieldName.equals("name")) {
//					return QAccount.account.name;
//				} else if (fieldName.equals("nature")) {
//					return QAccount.account.nature;
//				}
//				return null;
//			}
//		};
//		reqMapper.setPathRetriever(pathRetriever);
//
//		final String filters = URLEncoder.encode("name=@Conta;(nature==CREDIT,nature==DEBIT)", "UTF-8");
//
//		final Result result = reqMapper.map(Optional.of(filters), Optional.of("-accountId"), Optional.of("2"),
//				Optional.of("10"), Optional.empty());
//
//		for (int i = 1; i <= 50; i++) {
//			accRepo.save(new Account("Conta" + i, i % 2 == 0 ? AccountNature.CREDIT : AccountNature.DEBIT));
//		}
//		final Page<Account> page = accRepo.findAll(result.getPredicate(), result.getPageable());
//		Assert.assertEquals(10, page.getContent().size());
//		Assert.assertEquals("Conta30", page.getContent().get(0).getName());
//
//		final Page<Account> page2 = accRepo.findAll(result.getPredicate(), page.nextPageable());
//		Assert.assertEquals(10, page2.getContent().size());
//		Assert.assertEquals("Conta20", page2.getContent().get(0).getName());
//
//		Page<Account> page3 = accRepo.findAll(
//				Projections.bean(Account.class, QAccount.account.name, QAccount.account.nature), result.getPredicate(),
//				result.getPageable());
//		Assert.assertEquals(10, page3.getContent().size());
//
//	}

}
