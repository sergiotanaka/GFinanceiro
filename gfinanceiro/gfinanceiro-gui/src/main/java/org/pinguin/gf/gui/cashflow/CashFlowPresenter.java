package org.pinguin.gf.gui.cashflow;

import static java.util.Optional.empty;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.account.DayResultTO;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CashFlowPresenter {

	@Inject
	private AccountService accService;

	private final Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> endDateProperty = new SimpleObjectProperty<>();
	private final Property<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();

	private final ObservableList<DayResultTO> cashFlow = FXCollections.observableArrayList();

	@Inject
	private void init() {
		accounts.addAll(accService.retrieveAll(empty(), empty(), empty(), empty(), empty()));

		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDateProperty.setValue(startDate);

		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.DAY_OF_MONTH, 1);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		endDateProperty.setValue(endDate);
	}

	public void retrieve() {
		final List<DayResultTO> result = accService.retrieveCashFlow(accountProperty.getValue().getAccountId(),
				map(startDateProperty.getValue()), map(endDateProperty.getValue()));

		cashFlow.clear();
		cashFlow.addAll(result);

		for (DayResultTO item : result) {
			System.out.println(item);
		}
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

	public ObservableList<DayResultTO> getCashFlow() {
		return cashFlow;
	}

	public Property<AccountTO> accountProperty() {
		return accountProperty;
	}

	private LocalDate map(Calendar calendar) {
		TimeZone tz = calendar.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDate.ofInstant(calendar.toInstant(), zid);
	}

}
