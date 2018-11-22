package org.pinguin.gf.gui.balance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.service.api.balance.BalanceTO;
import org.pinguin.gf.service.api.common.PeriodTO;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BalancePresenter {
	@Inject
	private JournalEntryService service;

	private final Property<Calendar> startDateProperty = new SimpleObjectProperty<>();
	private final Property<Calendar> endDateProperty = new SimpleObjectProperty<>();

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

	public ObservableList<BalanceTO> getBalanceList() {
		return balanceList;
	}

	public void retrieve() {
		// FIXME
//		List<BalanceTO> result = service
//				.retrieveBalance(new PeriodTO(startDateProperty.getValue(), endDateProperty.getValue()));
		List<BalanceTO> result = new ArrayList<>();
		balanceList.clear();
		balanceList.addAll(result);

		System.out.println(result);
	}
}
