package org.pinguin.gf.gui.balance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.pinguin.gf.service.api.balance.BalanceService;
import org.pinguin.gf.service.api.balance.BalanceTO;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Presenter do form "Resumo"
 */
public class BalancePresenter {
	@Inject
	private BalanceService service;

	private final Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> endDateProperty = new SimpleObjectProperty<>();
	private final StringProperty tagFilterProperty = new SimpleStringProperty();

	private final ObservableList<BalanceTO> balanceList = FXCollections.observableArrayList();

	public BalancePresenter() {
		// FIXME remover os horarios
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDateProperty.setValue(startDate);

		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.DAY_OF_MONTH, 1);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		endDateProperty.setValue(endDate);
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

	public ObservableList<BalanceTO> getBalanceList() {
		return balanceList;
	}

	public void retrieve() {
		final List<BalanceTO> result = service.retrieveBalance(map(startDateProperty.getValue()),
				map(endDateProperty.getValue()), tagFilterProperty.getValue(), false);
		balanceList.clear();
		balanceList.addAll(result);
	}

	private LocalDate map(Calendar calendar) {
		TimeZone tz = calendar.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDate.ofInstant(calendar.toInstant(), zid);
	}
}
