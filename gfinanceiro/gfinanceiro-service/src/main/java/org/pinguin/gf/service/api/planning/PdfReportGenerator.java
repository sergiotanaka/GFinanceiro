package org.pinguin.gf.service.api.planning;

import static com.lowagie.text.FontFactory.getFont;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.planning.AccountPlanning;
import org.pinguin.gf.domain.planning.MonthYear;
import org.pinguin.gf.domain.planning.Planning;
import org.pinguin.gf.service.api.balance.BalanceService;
import org.pinguin.gf.service.api.balance.BalanceTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfReportGenerator {

	private static final Color BODY_DARK = new Color(249, 225, 202);
	private static final Font HEAD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

	@Autowired
	private AccountRepository repo;
	@Inject
	private BalanceService balService;

	public static class AccPlanTreeItem {
		private AccountPlanning accPlan;
		final private List<AccPlanTreeItem> childs = new ArrayList<>();
		private BigDecimal accomplished;

		public AccPlanTreeItem() {
		}

		public AccPlanTreeItem(final AccountPlanning accPlan) {
			this.accPlan = accPlan;
		}

		public AccountPlanning getAccPlan() {
			return accPlan;
		}

		public void setAccPlan(AccountPlanning accPlan) {
			this.accPlan = accPlan;
		}

		public List<AccPlanTreeItem> getChilds() {
			return childs;
		}

		public void setAccomplished(BigDecimal accomplished) {
			this.accomplished = accomplished;
		}

		public BigDecimal getAccomplished() {
			return accomplished;
		}

	}

	public ByteArrayInputStream build(final Planning planning) {

		try {

			final Paragraph title = new Paragraph(
					"Planejamento / Realizado - " + planning.getMonthYear().getMonth() + "/"
							+ planning.getMonthYear().getYear(),
					getFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new Color(255, 0, 0)));
			title.setAlignment(Element.ALIGN_CENTER);

			final PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(90);
			table.setWidths(new int[] { 3, 2, 2, 2 });
			table.addCell(headerCell("Conta"));
			table.addCell(headerCell("Planejado"));
			table.addCell(headerCell("Realizado"));
			table.addCell(headerCell("Percentual"));
			int i = 0;
			// 1. Montando a arvore
			final AccPlanTreeItem root = transform(planning);
			addAccPlanTree(table, root, i);

			final Document document = new Document();
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, out);
			document.open();
			document.add(title);
			document.add(table);
			document.close();

			return new ByteArrayInputStream(out.toByteArray());

		} catch (final DocumentException ex) {
			throw new IllegalStateException("Falha ao gerar relatorio.", ex);
		}
	}

	private void addAccPlanTree(final PdfPTable table, final AccPlanTreeItem item, int idx) {
		// Verificar se e' root
		if (item.getAccPlan().getAccount() != null) {
			addAccPlanCells(table, item, idx);
			idx++;
		}

		for (AccPlanTreeItem child : item.getChilds()) {
			addAccPlanTree(table, child, idx);
		}
	}

	private void addAccPlanCells(final PdfPTable table, final AccPlanTreeItem item, final int idx) {
		table.addCell(textCell(item.getAccPlan().getAccount().getName(), idx % 2 == 0));
		table.addCell(numberCell(item.getAccPlan().getValue(), idx % 2 == 0));
		table.addCell(numberCell(item.getAccomplished(), idx % 2 == 0));
		try {
			table.addCell(textCell(item.getAccomplished().divide(item.getAccPlan().getValue(), 4, RoundingMode.HALF_UP)
					.multiply(BigDecimal.valueOf(100)).setScale(2) + "%", idx % 2 == 0));
		} catch (ArithmeticException e) {
			table.addCell(textCell(e.getMessage(), idx % 2 == 0));
		}
	}

	private AccPlanTreeItem transform(final Planning planning) {

		// 1. Montando o Map com ID da conta e TreeItem correspondente
		final Map<Long, AccPlanTreeItem> accPlanMap = new HashMap<>();
		for (final AccountPlanning accPlan : planning.getAccountPlannings()) {
			accPlanMap.put(accPlan.getAccount().getAccountId(), new AccPlanTreeItem(accPlan));
			// 2. Inserir os pais da conta no map
			if (accPlan.getAccount().getParent() != null
					&& !accPlanMap.containsKey(accPlan.getAccount().getParent().getAccountId())) {
				accPlanMap.putAll(retrieveParents(accPlan.getAccount().getParent().getAccountId()));
			}
		}

		// 2. Montando a arvore
		final AccPlanTreeItem root = new AccPlanTreeItem();
		root.setAccPlan(new AccountPlanning());
		for (AccPlanTreeItem item : accPlanMap.values()) {
			if (item.getAccPlan().getAccount().getParent() != null) {
				final AccPlanTreeItem parent = accPlanMap
						.get(item.getAccPlan().getAccount().getParent().getAccountId());
				parent.getChilds().add(item);
			} else {
				root.getChilds().add(item);
			}
		}
		// 3. Preencher realizados
		Map<Long, BigDecimal> balanceMap = retrieveBalanceMap(planning);
		fillAccomplished(root, balanceMap);

		// 4. Totalizar planejados
		totalize(root);

		return root;
	}

	private BigDecimal totalize(AccPlanTreeItem root) {
		if (root.getChilds().isEmpty()) {
			// Caso analitico
			if (root.getAccPlan().getValue() == null) {
				return BigDecimal.ZERO;
			}
			return root.getAccPlan().getValue();
		} else {
			// Caso sintetico
			BigDecimal total = BigDecimal.ZERO;
			for (final AccPlanTreeItem child : root.getChilds()) {
				total = total.add(totalize(child));
			}
			root.getAccPlan().setValue(total.setScale(2));
			return total;
		}
	}

	private BigDecimal fillAccomplished(AccPlanTreeItem item, Map<Long, BigDecimal> map) {
		if (item.getChilds().isEmpty()) {
			// Caso analitico
			if (item.getAccPlan().getAccount() == null
					|| !map.containsKey(item.getAccPlan().getAccount().getAccountId())) {
				item.setAccomplished(BigDecimal.ZERO);
				return BigDecimal.ZERO;
			}
			final BigDecimal balance = map.get(item.getAccPlan().getAccount().getAccountId());
			item.setAccomplished(balance);
			return balance;
		} else {
			// Caso sintetico
			BigDecimal total = BigDecimal.ZERO;
			for (AccPlanTreeItem child : item.getChilds()) {
				total = total.add(fillAccomplished(child, map));
			}
			item.setAccomplished(total.setScale(2));
			return total;
		}
	}

	private Map<Long, BigDecimal> retrieveBalanceMap(final Planning planning) {
		MonthYear monthYear = planning.getMonthYear();
		final LocalDate start = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), 1);
		final LocalDate end = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), 1).plusMonths(1).minusDays(1);
		final List<BalanceTO> balance = balService.retrieveBalance(start, end, false);
		final Map<Long, BigDecimal> balanceMap = balance.stream()
				.collect(Collectors.toMap(b -> b.getAccount().getAccountId(), BalanceTO::getBalance));
		return balanceMap;
	}

	private Map<Long, AccPlanTreeItem> retrieveParents(final Long accId) {
		final Map<Long, AccPlanTreeItem> accPlanMap = new HashMap<>();

		final Optional<Account> account = repo.findById(accId);
		if (!account.isPresent()) {
			return accPlanMap;
		}

		final AccountPlanning parent = new AccountPlanning();
		parent.setAccount(account.get());
		accPlanMap.put(account.get().getAccountId(), new AccPlanTreeItem(parent));
		if (account.get().getParent() != null) {
			accPlanMap.putAll(retrieveParents(account.get().getParent().getAccountId()));
		}
		return accPlanMap;
	}

	private PdfPCell headerCell(final String title) {
		final PdfPCell hcell = new PdfPCell(new Phrase(title, HEAD_FONT));
		hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		hcell.setBackgroundColor(new Color(255, 231, 147));
		return hcell;
	}

	private PdfPCell textCell(final String value, boolean dark) {
		final PdfPCell cell = new PdfPCell(new Phrase(value));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		if (dark) {
			cell.setBackgroundColor(BODY_DARK);
		}
		return cell;
	}

	private PdfPCell numberCell(final BigDecimal value, boolean dark) {
		try {
			final PdfPCell cell = new PdfPCell(new Phrase(value.toString()));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			if (dark) {
				cell.setBackgroundColor(BODY_DARK);
			}
			return cell;
		} catch (Exception e) {
			throw e;
		}
	}
}
