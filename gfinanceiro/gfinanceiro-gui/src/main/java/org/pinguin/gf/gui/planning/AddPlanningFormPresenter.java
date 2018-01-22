package org.pinguin.gf.gui.planning;

import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.facade.planning.MonthTO;
import org.pinguin.gf.facade.planning.MonthYearTO;
import org.pinguin.gf.facade.planning.PlanningService;
import org.pinguin.gf.facade.planning.PlanningTO;
import org.pinguin.gui.util.BindHelper;
import org.pinguin.gui.util.EditMode;
import org.pinguin.gui.util.PropertyAdapter;
import org.pinguin.gui.util.ValueConverter;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddPlanningFormPresenter {

	@Inject
	private PlanningService service;

	private BindHelper<MonthYearTO> bindHelper = new BindHelper<>();

	private final ObservableList<MonthTO> months = FXCollections.observableArrayList();
	private final Property<MonthTO> monthProperty = new SimpleObjectProperty<>();
	private final Property<String> yearProperty = new SimpleStringProperty();

	private Function<Void, Void> closeWindowCommand;
	private EditMode editMode = EditMode.CREATE;
	private PlanningTO to;

	public ObservableList<MonthTO> getMonths() {
		return months;
	}

	public Property<MonthTO> monthProperty() {
		return monthProperty;
	}

	public Property<String> yearProperty() {
		return yearProperty;
	}

	public PlanningTO getTo() {
		return to;
	}

	public void setTo(PlanningTO to) {
		this.to = to;

		if (to != null) {
			mapToPresenter(to);
		}
	}

	private void mapToPresenter(PlanningTO to) {
		if (to.getMonthYear() == null) {
			to.setMonthYear(new MonthYearTO());
		}
		bindHelper.setTo(to.getMonthYear());
		bindHelper.bind("month", monthProperty);
		bindHelper.bind("year",
				new PropertyAdapter<Integer, String>(yearProperty, new ValueConverter<Integer, String>() {

					@Override
					public Integer convert1(String value) {
						try {
							return Integer.valueOf(value);
						} catch (Exception e) {
							return 0;
						}
					}

					@Override
					public String convert2(Integer value) {
						return value.toString();
					}
				}));
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

	public void save() {
		if (editMode.equals(EditMode.CREATE)) {
			service.createPlanning(to);
		} else if (editMode.equals(EditMode.UPDATE)) {
			service.updatePlanning(to);
		}
	}

	public void cancel() {
		if (closeWindowCommand != null) {
			closeWindowCommand.apply(null);
		}
	}

}
