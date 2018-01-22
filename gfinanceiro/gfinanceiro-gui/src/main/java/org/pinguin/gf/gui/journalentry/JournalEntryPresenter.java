package org.pinguin.gf.gui.journalentry;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.facade.journalentry.JournalEntryService;
import org.pinguin.gf.facade.journalentry.JournalEntryTO;
import org.pinguin.gui.util.BindHelper;
import org.pinguin.gui.util.EditMode;
import org.pinguin.gui.util.PropertyAdapter;
import org.pinguin.gui.util.ValueConverter;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JournalEntryPresenter {

	@Inject
	private JournalEntryService service;
	private BindHelper<JournalEntryTO> bindHelper = new BindHelper<>();

	private final ObservableList<AccountTO> debitAccounts = FXCollections.observableArrayList();
	private final ObservableList<AccountTO> creditAccounts = FXCollections.observableArrayList();
	private final Property<AccountTO> debitProperty = new SimpleObjectProperty<>();
	private final Property<AccountTO> creditProperty = new SimpleObjectProperty<>();
	private final Property<String> valueProperty = new SimpleObjectProperty<>("");
	private final Property<Calendar> dateProperty = new SimpleObjectProperty<>(GregorianCalendar.getInstance());
	private final Property<String> descriptionProperty = new SimpleObjectProperty<>("");

	private EditMode editMode = EditMode.CREATE;
	private JournalEntryTO to;

	private Function<Void, Void> closeWindowCommand;

	public JournalEntryPresenter() {
	}

	public ObservableList<AccountTO> getDebitAccounts() {
		return debitAccounts;
	}

	public ObservableList<AccountTO> getCreditAccounts() {
		return creditAccounts;
	}

	public Property<String> valueProperty() {
		return valueProperty;
	}

	public Property<AccountTO> debitProperty() {
		return debitProperty;
	}

	public Property<AccountTO> creditProperty() {
		return creditProperty;
	}

	public Property<Calendar> dateProperty() {
		return dateProperty;
	}

	public Property<String> descriptionProperty() {
		return descriptionProperty;
	}

	public JournalEntryTO getTo() {
		return to;
	}

	public void setTo(JournalEntryTO to) {
		this.to = to;

		if (to != null) {
			mapToPresenter(to);
		}
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}

	public Function<Void, Void> getCloseWindowCommand() {
		return closeWindowCommand;
	}

	public void setCloseWindowCommand(Function<Void, Void> closeWindowCommand) {
		this.closeWindowCommand = closeWindowCommand;
	}

	private void mapToPresenter(JournalEntryTO to) {

		bindHelper.setTo(to);
		bindHelper.bind("debitAccount", debitProperty);
		bindHelper.bind("creditAccount", creditProperty);
		bindHelper.bind("value",
				new PropertyAdapter<BigDecimal, String>(valueProperty, new ValueConverter<BigDecimal, String>() {

					@Override
					public BigDecimal convert1(String value) {
						try {
							return new BigDecimal(value);
						} catch (Exception e) {
							return BigDecimal.ZERO;
						}
					}

					@Override
					public String convert2(BigDecimal value) {
						if (value == null) {
							return "0.00";
						}
						return value.toString();
					}
				}));
		bindHelper.bind("date", dateProperty);
		bindHelper.bind("description", descriptionProperty);
	}

	public void save() {
		if (editMode.equals(EditMode.CREATE)) {
			service.createJournalEntry(to);
			clearForm();
		} else if (editMode.equals(EditMode.UPDATE)) {
			service.updateJournalEntry(to);
			closeWindowCommand.apply(null);
		}
	}

	private void clearForm() {
		bindHelper.getProperty("value").setValue(BigDecimal.valueOf(0.0).setScale(2));
		bindHelper.getProperty("description").setValue("");
	}

	public void cancel() {
		if (closeWindowCommand != null) {
			closeWindowCommand.apply(null);
		}
	}

}
