package org.pinguin.gf.gui.accstatement;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.pinguin.gf.gui.journalentry.OpenJournalEntryCommand;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryParam;
import org.pinguin.gf.service.api.account.AccStatementEntryTO;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.pinguin.gui.util.EditMode;

import com.google.inject.Injector;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Presenter do Form de "Historico".
 */
public class AccStatementPresenter {
	@Inject
	private Injector injector;
	@Inject
	private JournalEntryService service;
	@Inject
	private AccountService accService;

	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();
	private final Property<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> endDateProperty = new SimpleObjectProperty<>();
	private final StringProperty tagFilterProperty = new SimpleStringProperty();
	private final Property<Boolean> periodBalanceProperty = new SimpleBooleanProperty(false);
	private final ObservableList<AccStatementEntryTO> accStatementEntries = FXCollections.observableArrayList();

	public AccStatementPresenter() {
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDateProperty.setValue(startDate);

		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.DAY_OF_MONTH, 1);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		endDateProperty.setValue(endDate);
	}

	public ObservableList<AccountTO> getAccounts() {
		return accounts;
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

	public StringProperty tagFilterProperty() {
		return tagFilterProperty;
	}

	public Property<Boolean> periodBalanceProperty() {
		return periodBalanceProperty;
	}

	public ObservableList<AccStatementEntryTO> accStatementEntries() {
		return accStatementEntries;
	}

	public void retrieve() {
		List<AccStatementEntryTO> result = accService.retrieveStatements(accountProperty.getValue().getAccountId(),
				map(startDateProperty.getValue()), map(endDateProperty.getValue()), tagFilterProperty.get(),
				periodBalanceProperty.getValue());
		accStatementEntries.clear();
		accStatementEntries.addAll(result);
	}

	public void deleteJournalEntry(AccStatementEntryTO selectedItem) {
		service.deleteEntry(selectedItem.getId());
	}

	public void editJournalEntry(AccStatementEntryTO selectedItem) {
		JournalEntryTO entry = service.retrieveById(selectedItem.getId());
		injector.getInstance(OpenJournalEntryCommand.class)
				.apply(new OpenJournalEntryParam(entry, null, EditMode.UPDATE));
	}

	private LocalDate map(Calendar calendar) {
		TimeZone tz = calendar.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDate.ofInstant(calendar.toInstant(), zid);
	}

}
