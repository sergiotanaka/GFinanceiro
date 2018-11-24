package org.pinguin.gf.gui.journalentry;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JournalEntryListPresenter {

	private final Locale locale = new Locale("pt", "BR");
	private final DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance(locale);

	@Inject
	private AccountService accService;

	private final ObservableList<JournalEntryItem> entries = FXCollections.observableArrayList();
	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();
	private final Property<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private Property<String> textAreaProperty = new SimpleStringProperty();

	public JournalEntryListPresenter() {
		nf.setParseBigDecimal(true);
	}

	@Inject
	public void init() {
		getAccounts().addAll(accService.retrieveAnalytical());
	}

	public Property<String> textAreaProperty() {
		return textAreaProperty;
	}

	public Property<AccountTO> accountProperty() {
		return accountProperty;
	}

	public ObservableList<AccountTO> getAccounts() {
		return accounts;
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
				JournalEntryItem entry = new JournalEntryItem();
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

				entries.add(entry);
			}

		} catch (final Exception e) {
			throw new IllegalStateException("Erro ao ler o texto", e);
		}

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
