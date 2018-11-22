package org.pinguin.gf.gui.accstatement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.gui.journalentry.OpenJournalEntryCommand;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryParam;
import org.pinguin.gf.service.api.account.AccStatementEntryTO;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.pinguin.gui.util.EditMode;

import com.google.inject.Injector;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AccStatementPresenter {
	@Inject
	private Injector injector;
	@Inject
	private JournalEntryService service;

	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();
	private final Property<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> endDateProperty = new SimpleObjectProperty<>();
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

	public Property<Boolean> periodBalanceProperty() {
		return periodBalanceProperty;
	}

	public ObservableList<AccStatementEntryTO> accStatementEntries() {
		return accStatementEntries;
	}

	public void retrieve() {
		// List<AccStatementEntryTO> result =
		// service.retrieveAccountStatement(accountProperty.getValue(),
		// new PeriodTO(startDateProperty.getValue(), endDateProperty.getValue()),
		// periodBalanceProperty.getValue());
		List<AccStatementEntryTO> result = new ArrayList<>();
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

}
