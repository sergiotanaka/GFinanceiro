package org.pinguin.gf.gui.journalentry;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.pinguin.gf.service.api.journalentry.TagService;
import org.pinguin.gf.service.api.journalentry.TagTO;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JournalEntryListPresenter {

	private final Locale locale = new Locale("pt", "BR");
	private final DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance(locale);

	@Inject
	private AccountService accService;
	@Inject
	private TagService tagService;
	@Inject
	private JournalEntryService journalEntryService;

	private final ObservableList<JournalEntryItem> entries = FXCollections.observableArrayList();
	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();
	private final Property<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private Property<String> textAreaProperty = new SimpleStringProperty();
	private Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private Property<Calendar> endDateProperty = new SimpleObjectProperty<>();
	private ObservableList<TagTO> candidateTags = FXCollections.observableArrayList();

	public JournalEntryListPresenter() {
		nf.setParseBigDecimal(true);
	}

	@Inject
	public void init() {
		getAccounts().addAll(accService.retrieveAnalytical());
		getCandidateTags().addAll(tagService.retrieveAll());

		LocalDate endDate = LocalDate.now().withDayOfMonth(15);
		if (LocalDate.now().isAfter(endDate)) {
			endDate = endDate.plusMonths(1);
		}
		final LocalDate startDate = endDate.minusMonths(1);

		startDateProperty.setValue(map(startDate));
		endDateProperty.setValue(map(endDate));
	}

	public Property<String> textAreaProperty() {
		return textAreaProperty;
	}

	public Property<AccountTO> accountProperty() {
		return accountProperty;
	}

	public Property<Calendar> startDateProperty() {
		return startDateProperty;
	}

	public Property<Calendar> endDateProperty() {
		return endDateProperty;
	}

	public ObservableList<AccountTO> getAccounts() {
		return accounts;
	}

	public ObservableList<TagTO> getCandidateTags() {
		return candidateTags;
	}

	public ObservableList<JournalEntryItem> getEntries() {
		return entries;
	}

	public void process() {
		try {
			final String text = textAreaProperty.getValue();

			// 1. Iterar por linhas
			BufferedReader reader = new BufferedReader(new StringReader(text));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// 2. Split pelo separador
				String[] splitted = line.split("\t");
				// 3. Preencher a lista
				JournalEntryItem entry = null;
				if (splitted.length <= 4) {
					entry = mapCredit(splitted);
				} else {
					entry = mapAccount(splitted);
				}

				entries.add(entry);
			}

		} catch (final Exception e) {
			throw new IllegalStateException("Erro ao ler o texto", e);
		}
	}

	public String save() {
		int success = 0;
		int exists = 0;
		for (JournalEntryItem item : entries) {
			JournalEntryTO to = new JournalEntryTO();
			to.setEntryId(item.entryIdProperty().get());
			to.setDebitAccount(item.debitAccountProperty().get());
			to.setCreditAccount(item.creditAccountProperty().get());
			to.setValue(item.valueProperty().get());
			to.setDate(item.dateProperty().get());
			to.setDescription(item.descriptionProperty().get());
			to.getTags().addAll(item.getTags());
			to.setFuture(false);
			if (journalEntryService.exists(to.getDate(), to.getValue(), to.getDescription())) {
				exists++;
				log.warn("Entrada ja' existente: {}", to.toString());
			} else {
				journalEntryService.createEntry(to);
				success++;
			}
		}
		return String.format("Foram salvos %d registros e descartados %d registros por já existir.", success, exists);

	}

	public void cancel() {
		// TODO Auto-generated method stub

	}

	public void clean() {
		entries.clear();
	}

	public void reloadAccounts() {
		getAccounts().clear();
		getAccounts().addAll(accService.retrieveAnalytical());
		getCandidateTags().clear();
		getCandidateTags().addAll(tagService.retrieveAll());
	}

	// METODOS DE APOIO //

	private JournalEntryItem mapCredit(String[] splitted) {
		final JournalEntryItem entry = new JournalEntryItem();

		LocalDateTime date = parseDate(splitted[0].trim());
		// Tratamento para parcelas
		boolean isInstallment = date.isBefore(map(startDateProperty.getValue()));
		entry.dateProperty().set(isInstallment ? map(startDateProperty.getValue()) : date);
		entry.descriptionProperty().set(splitted[1].trim() + (isInstallment ? " " + splitted[0].trim() : ""));
		entry.creditAccountProperty().set(accountProperty.getValue());
		entry.debitAccountProperty().set(parseDescription(splitted[1].trim()));
		if (splitted.length > 3) {
			entry.valueProperty().set(parseValue(splitted[3].trim()));
		}
		return entry;
	}

	private JournalEntryItem mapAccount(String[] splitted) {
		final JournalEntryItem entry = new JournalEntryItem();
		entry.dateProperty().set(parseDate(splitted[0].trim()));
		entry.descriptionProperty().set(splitted[1].trim());
		if (splitted[4].trim().isEmpty()) {
			entry.creditAccountProperty().set(accountProperty.getValue());
			entry.debitAccountProperty().set(parseDescription(splitted[1].trim()));
			entry.valueProperty().set(parseValue(splitted[5].trim()).multiply(BigDecimal.valueOf(-1)));
		} else {
			entry.creditAccountProperty().set(parseDescription(splitted[1].trim()));
			entry.debitAccountProperty().set(accountProperty.getValue());
			entry.valueProperty().set(parseValue(splitted[4].trim()));
		}
		return entry;
	}

	private Calendar map(final LocalDate localDate) {
		final Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 0, 0, 0);
		return calendar;
	}

	private LocalDateTime map(final Calendar value) {
		TimeZone tz = value.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDateTime.ofInstant(value.toInstant(), zid);
	}

	private AccountTO parseDescription(final String description) {
		for (AccountTO acc : accounts) {
			for (final String tag : acc.getTags()) {
				if (description.toUpperCase().contains(tag.toUpperCase())) {
					return acc;
				}
			}
		}
		return null;
	}

	private LocalDateTime parseDate(final String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateStr, formatter);
		return localDate.atStartOfDay();
	}

	private BigDecimal parseValue(final String valueStr) {
		return (BigDecimal) nf.parse(valueStr, new ParsePosition(0));
	}
}
